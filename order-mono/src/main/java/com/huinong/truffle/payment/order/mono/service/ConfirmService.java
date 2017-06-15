package com.huinong.truffle.payment.order.mono.service;

import java.awt.Point;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.huinong.truffle.component.base.constants.BaseResult;
import com.huinong.truffle.component.base.constants.ResultCode;
import com.huinong.truffle.payment.order.mono.component.redis.RedisLock;
import com.huinong.truffle.payment.order.mono.component.redis.client.DefRedisClient;
import com.huinong.truffle.payment.order.mono.component.sys.config.OrderAppConf;
import com.huinong.truffle.payment.order.mono.component.zk.SerialGenFactory;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants.CmbPayShopEnum;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants.DirectEventEnum;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants.DirectStateEnum;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants.PayChannelEnum;
import com.huinong.truffle.payment.order.mono.constant.OrderResultCode;
import com.huinong.truffle.payment.order.mono.dao.read.MainOrderReadDAO;
import com.huinong.truffle.payment.order.mono.dao.read.OrderReadDAO;
import com.huinong.truffle.payment.order.mono.dao.read.OutInMoneyReadDAO;
import com.huinong.truffle.payment.order.mono.dao.write.MainOrderWriteDAO;
import com.huinong.truffle.payment.order.mono.dao.write.OrderWriteDAO;
import com.huinong.truffle.payment.order.mono.dao.write.OutInMoneyWriteDAO;
import com.huinong.truffle.payment.order.mono.domain.DirectCash;
import com.huinong.truffle.payment.order.mono.domain.HnpSetlDetail;
import com.huinong.truffle.payment.order.mono.entity.HnpMainOrderEntity;
import com.huinong.truffle.payment.order.mono.entity.HnpOrderEntity;
import com.huinong.truffle.payment.order.mono.entity.OutInMoneyEntity;
import com.huinong.truffle.payment.order.mono.util.MathUtils;

/**
 * 确认收货-结算订单
 * @author peng
 *
 */
@Service("confirmService")
public class ConfirmService {
	private static Logger logger = LoggerFactory.getLogger(ConfirmService.class);
	public Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

	@Autowired
	private DefRedisClient defRedisClient;
	@Autowired
	private MainOrderReadDAO mainOrderReadDAO ;
	@Autowired
	private MainOrderWriteDAO mainOrderWriteDAO ;
	@Autowired
	private OutInMoneyReadDAO outInMoneyReadDAO ;
	@Autowired
	private OutInMoneyWriteDAO outInMoneyWriteDAO ;
	@Autowired
	private OrderReadDAO orderReadDAO ;
	@Autowired
	private OrderWriteDAO orderWriteDAO ;

	
	@Autowired
	private OrderAppConf orderAppConf;
	
	public BaseResult<DirectCash> confirmReceipt(HnpSetlDetail reqDTO) throws Exception {
		if (null == reqDTO) {
			logger.info("结算订单为空");
			return BaseResult.fail(OrderResultCode.PARAM_0004);
		}
		RedisLock lock = null;
		try {
			// 校验参数
			BaseResult<Void> checkMsgResult = checkReceiptReq(reqDTO);
			if(null == checkMsgResult || checkMsgResult.getCode() != ResultCode.SUCCESS.getCode()){
				return new BaseResult<DirectCash>(checkMsgResult.getCode(),checkMsgResult.getMsg());
			}
			// 赋值参数
			String orderSerialNumber = reqDTO.getSerialNumber();
			String mainOrderNo = reqDTO.getMainOrderNo();
			String orderNo = reqDTO.getOrderNo();
			Double amt = reqDTO.getAmt();
			Long appPayeeId = reqDTO.getAppPayeeId();
			// 收款人信息
			String payeeAccount = reqDTO.getPayeeAccount();
			String payeeName = reqDTO.getPayeeName();
			String bankFLG = reqDTO.getBankFLG();
			String payeeBankAddress = reqDTO.getPayeeBankAddress();
			String payeeBank = reqDTO.getPayeeBank();
			String payChannel = reqDTO.getPayChannel();

			// 0 对每一笔预支付订单进行锁处理，防止重复提交
			lock = new RedisLock(defRedisClient,OrderConstants.RedisKey.ORDER_SETL_KEY.value.concat(orderSerialNumber));
			if (!lock.lock()) {
				logger.info("订单：" + orderNo + "結算超时...");
				return BaseResult.fail(OrderResultCode.DB_0007);
			}

			// 1 校验子订单信息 （单笔订单信息）
			HnpOrderEntity orderEntity = orderReadDAO.selectBySerialNumber(orderSerialNumber);
			/*HnpDetailEntity detailDTO = orderItemReadDAO.getDTOByUniqueValue(orderSerialNumber);*/
			// 验证提交付款的订单信息与支付订单信息是否吻合
			/*if (!reqDTO.getObjectUUID().equals(detailDTO.getObjectUUID())) {
				logger.info("订单号:{" + detailDTO.getOrderNo()+ "}付款单信息与支付底单信息不相符,请重新审核");
				return BaseResult.fail(OrderResultCode.DB_0008);
			}*/
			if (!orderEntity.isSettled()) {
				logger.info("订单号:{" + orderEntity.getOrderId() + "},流水号:{"+ orderEntity.getSerialNumber() + "} 还未到帐，请核实");
				return BaseResult.fail(OrderResultCode.DB_0009);
			}
			// 2校验子订单流水在付款记录表中的状态
			OutInMoneyEntity outMoneyDTO = outInMoneyReadDAO.getByOrderSerialNumber(orderSerialNumber,CmbPayShopEnum.CMB_PAY_SHOP_SELLER.val);
			if (null != outMoneyDTO) {
				if (outMoneyDTO.isPayFail()) {
					logger.info("订单号：" + orderEntity.getOrderId() + "付款失败,msg："+ outMoneyDTO.getResMessage());
					return BaseResult.fail(OrderResultCode.DB_0010);
				}
				if (outMoneyDTO.isPaySuc()) {
					logger.info("订单号：" + orderEntity.getOrderId() + "付款成功,请勿重复提交");
					return BaseResult.fail(OrderResultCode.DB_0011);
				}
				if (outMoneyDTO.isPaying() || outMoneyDTO.isToPay()) {
					// 处理中-->直接返回付款单信息，进行支付（先同步支付状态）
					DirectCash cashDTO = new DirectCash();
					cashDTO.setOrderId(orderEntity.getId());
					cashDTO.setAccno(outMoneyDTO.getAccno());
					cashDTO.setAccountName(outMoneyDTO.getAccountName());
					cashDTO.setAmt(outMoneyDTO.getAmount());
					cashDTO.setBankFlg(outMoneyDTO.getBankFlg());
					cashDTO.setDirectStatus(outMoneyDTO.getDirectStatus());
					cashDTO.setMainOrderNo(outMoneyDTO.getMainOrderNo());
					cashDTO.setReceiveBankAddr(outMoneyDTO.getReceiveBankAddr());
					cashDTO.setReceiveBankName(outMoneyDTO.getReceiveBankAddr());
					cashDTO.setSerialNumber(outMoneyDTO.getSerialNumber());
					cashDTO.setTriggerTime(new Date());
					cashDTO.setType(outMoneyDTO.getType());
					cashDTO.setNeedAmt(outMoneyDTO.getTransAmt());
					cashDTO.setHandCharge(outMoneyDTO.getFee());
					return BaseResult.success(cashDTO);
				}
			}

			// 不存在付款单-->制付款单
			String ruleConfig = getHandChargeConfig(payChannel);
			double chargeFee = calcPayCharge(payChannel, ruleConfig,orderEntity.getAmt().doubleValue());
			double receiveFee = MathUtils.minusDouble(orderEntity.getAmt().doubleValue(),chargeFee);

			// 计算可用余额=入金支付总额-已发生额
			HnpMainOrderEntity mainOrderEntity = mainOrderReadDAO.selectByMainOrderNo(mainOrderNo);
			if (null == mainOrderEntity) {
				logger.info("[mainOrderNo:"+mainOrderNo+"],订单信息不存在");
				return BaseResult.fail(OrderResultCode.DB_0005);
			}
			
			double lastBalance = mainOrderEntity.getTotalAmt().doubleValue();
			double occur = outInMoneyReadDAO.calcOccurByMainOrderNo(mainOrderNo);
			double nextBalance = lastBalance - occur;
			// 当前付款金额必须小于或等于当前余额
			if (receiveFee > nextBalance) {
				logger.info("当前付款金额大于平台可用余额,amt:" + amt + ",balace:"+ nextBalance);
				return BaseResult.fail(OrderResultCode.DB_0013);
			}

			// 付款流水号
			String paySerialNumber = SerialGenFactory.getInstance().genOutPaySerialNo();
			// 付款记录DTO
			OutInMoneyEntity payRecordDTO = new OutInMoneyEntity();
			payRecordDTO.setMainOrderNo(mainOrderNo);
			payRecordDTO.setOrderNo(orderNo);
			payRecordDTO.setOrderSerialNumber(orderSerialNumber);
			payRecordDTO.setAmount(amt);
			payRecordDTO.setFee(chargeFee);
			payRecordDTO.setFeeRule(ruleConfig);
			payRecordDTO.setTransAmt(receiveFee);
			payRecordDTO.setReceiveUserid(appPayeeId);
			payRecordDTO.setReceiveUserType(CmbPayShopEnum.CMB_PAY_SHOP_SELLER.val);
			payRecordDTO.setAccno(payeeAccount);
			payRecordDTO.setAccountName(payeeName);
			payRecordDTO.setReceiveBankAddr(payeeBankAddress);
			payRecordDTO.setReceiveBankName(payeeBank);
			payRecordDTO.setSerialNumber(paySerialNumber);
			payRecordDTO.setPayChannel(payChannel);
			payRecordDTO.setPayTime(new Date());
			payRecordDTO.setDirectStatus(DirectStateEnum.INITIAL.val);
			payRecordDTO.setType(DirectEventEnum.DIRECT_PAY.val);
			payRecordDTO.setBankFlg(bankFLG);
			
			int i = outInMoneyWriteDAO.addOneOutInMoney(payRecordDTO);
			if (i == 0) {
				return BaseResult.fail(OrderResultCode.DB_0013);
			}

			// 组付款单-->调用付款接口
			DirectCash cashDTO = new DirectCash();
			cashDTO.setOrderId(orderEntity.getId());
			cashDTO.setAccno(payeeAccount);
			cashDTO.setAccountName(payeeName);
			cashDTO.setAmt(amt);
			cashDTO.setBankFlg(bankFLG);
			cashDTO.setDirectStatus(DirectStateEnum.INITIAL.val);
			cashDTO.setMainOrderNo(mainOrderNo);
			cashDTO.setReceiveBankAddr(payeeBankAddress);
			cashDTO.setReceiveBankName(payeeBank);
			cashDTO.setSerialNumber(paySerialNumber);
			cashDTO.setTriggerTime(new Date());
			cashDTO.setType(DirectEventEnum.DIRECT_PAY.val);
			cashDTO.setNeedAmt(receiveFee);
			cashDTO.setHandCharge(chargeFee);
			
			return BaseResult.success(cashDTO);
		} catch (Exception e) {
			e.printStackTrace();
			throw e ;
		} finally {
			if (lock != null) {
				lock.unlock();
			}
		}
	}
	
	
	/**
	 * 获取需计算的手续费配置规则
	 */
	private String getHandChargeConfig(String payChannel) {
		String config = "";
		if (payChannel.equals(String.valueOf(PayChannelEnum.PAYCHANEL_ALI.val.intValue()))) {
			config = orderAppConf.getAlifee();
		} else if (payChannel.equals(String.valueOf(PayChannelEnum.PAYCHANEL_WX.val.intValue()))) {
			config = orderAppConf.getWxfee();
		} else if (payChannel.equals(String.valueOf(PayChannelEnum.PAYCHANEL_QUICK.val.intValue()))) {
			config = orderAppConf.getQuickfee();
		}
		return config;
	}
	
	/**
	 * 验证确认收货入参参数
	 *
	 * @param map
	 * @throws Exception
	 */
	private BaseResult<Void> checkReceiptReq(HnpSetlDetail reqDTO) {
		if(StringUtils.isBlank(reqDTO.getMainOrderNo())){
			logger.info("丢失关键字{订单号}");
			return BaseResult.fail(OrderResultCode.PARAM_0006);
		}
		if(null == reqDTO.getAppPayerId()){
			logger.info("丢失关键字{买家ID}");
			return BaseResult.fail(OrderResultCode.PARAM_0007);
		}

		if(null == reqDTO.getAmt()){
			logger.info("丢失关键字{结算总额}");
			return BaseResult.fail(OrderResultCode.PARAM_0008);
		}

		if(StringUtils.isBlank(reqDTO.getOrderNo())){
			logger.info("丢失关键字{子订单编号}");
			return BaseResult.fail(OrderResultCode.PARAM_0009);
		}
		
		if(null == reqDTO.getAppPayeeId()){
			logger.info("丢失关键字{卖家ID}");
			return BaseResult.fail(OrderResultCode.PARAM_0010);
		}
		// 验证收款人信息
		if(StringUtils.isBlank(reqDTO.getPayeeAccount())){
			logger.info("丢失关键字{收款人账号}");
			return BaseResult.fail(OrderResultCode.PARAM_0011);
		}

		if(StringUtils.isBlank(reqDTO.getPayeeName())){
			logger.info("丢失关键字{收款人名称}");
			return BaseResult.fail(OrderResultCode.PARAM_0012);
		}
		
		if(StringUtils.isBlank(reqDTO.getBankFLG())){
			logger.info("丢失关键字{招商卡标志}");
			return BaseResult.fail(OrderResultCode.PARAM_0013);
		}

		if(StringUtils.isBlank(reqDTO.getPayeeBankAddress())){
			logger.info("丢失关键字{收款银行开户行地址}");
			return BaseResult.fail(OrderResultCode.PARAM_0014);
		}

		if(StringUtils.isBlank(reqDTO.getPayeeBank())){
			logger.info("丢失关键字{收款银行开户银行}");
			return BaseResult.fail(OrderResultCode.PARAM_0015);
		}

		// 追加关联字段数据冗余
		if(null == reqDTO.getChargeFee()){
			logger.info("丢失关键字{手续费}");
			return BaseResult.fail(OrderResultCode.PARAM_0016);
		}

		if(StringUtils.isBlank(reqDTO.getType())){
			logger.info("丢失关键字{结算方向}");
			return BaseResult.fail(OrderResultCode.PARAM_0017);
		}

		if(StringUtils.isBlank(reqDTO.getPayChannel())){
			logger.info("丢失关键字{支付渠道}");
			return BaseResult.fail(OrderResultCode.PARAM_0018);
		}
		return BaseResult.success();
	}	
	
	/**
	 * 根据渠道、规则 和 订单金额 计算手续费
	 */
	private double calcPayCharge(String payChannel, String ruleVal,double orderAmt) {
		logger.info("支付渠道[payChannel]:" + payChannel + ",[ruleVal]:" + ruleVal+ ",[orderAmt]:" + orderAmt);
		double chargeFee = 0.0d;
		if (StringUtils.isBlank(payChannel) || StringUtils.isBlank(ruleVal)) {
			return chargeFee;
		}
		// 非快捷支付
		if (!payChannel.equals(String.valueOf(PayChannelEnum.PAYCHANEL_QUICK.val.intValue()))) {
			chargeFee = new Double(ruleVal.substring(0, ruleVal.indexOf("%"))) / 100;
			chargeFee = MathUtils.mulDouble(orderAmt, chargeFee);
			return chargeFee;
		}
		// 渠道为快捷支付
		Map<Point, String> retMap = gson.fromJson(ruleVal,
				new TypeToken<Map<Point, String>>() {
				}.getType());
		for (Point p : retMap.keySet()) {
			logger.debug("key:" + p + " values:" + retMap.get(p));
			if (orderAmt >= p.getX() && orderAmt < p.getY()) {
				chargeFee = Double.valueOf(retMap.get(p));
				logger.debug("渠道为快捷支付的手续费：" + chargeFee);
				break;
			}
		}
		return chargeFee;
	}
}
