package com.huinong.truffle.payment.order.mono.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huinong.truffle.component.base.constants.BaseResult;
import com.huinong.truffle.component.base.constants.ResultCode;
import com.huinong.truffle.component.base.constants.ResultCodeIntf;
import com.huinong.truffle.payment.order.mono.component.redis.RedisLock;
import com.huinong.truffle.payment.order.mono.component.redis.client.DefRedisClient;
import com.huinong.truffle.payment.order.mono.component.zk.SerialGenZkImpl;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants.CmbPayShopEnum;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants.DirectEventEnum;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants.DirectStateEnum;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants.RedisKey;
import com.huinong.truffle.payment.order.mono.constant.OrderResultCode;
import com.huinong.truffle.payment.order.mono.dao.read.MainOrderReadDAO;
import com.huinong.truffle.payment.order.mono.dao.read.OrderReadDAO;
import com.huinong.truffle.payment.order.mono.dao.read.OutInMoneyReadDAO;
import com.huinong.truffle.payment.order.mono.dao.write.OrderWriteDAO;
import com.huinong.truffle.payment.order.mono.dao.write.OutInMoneyWriteDAO;
import com.huinong.truffle.payment.order.mono.domain.HnpRefund;
import com.huinong.truffle.payment.order.mono.domain.HnpOutInMoney;
import com.huinong.truffle.payment.order.mono.domain.ReceiptCard;
import com.huinong.truffle.payment.order.mono.entity.HnpMainOrderEntity;
import com.huinong.truffle.payment.order.mono.entity.HnpOrderEntity;
import com.huinong.truffle.payment.order.mono.entity.OutInMoneyEntity;
import com.huinong.truffle.payment.order.mono.util.CopyBeanUtil;
import com.huinong.truffle.payment.order.mono.util.ParamHandler;

/**
 * 退款-结算订单
 * @author peng
 *
 */
@Service("refundService")
public class RefundService {
	private static Logger logger = LoggerFactory.getLogger(RefundService.class);
	public Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

	@Autowired
	private MainOrderReadDAO mainOrderReadDAO ;
	@Autowired
	private OutInMoneyReadDAO outInMoneyReadDAO ;
	@Autowired
	private OutInMoneyWriteDAO outInMoneyWriteDAO ;
	@Autowired
	private OrderReadDAO orderReadDAO ;
	@Autowired
	private OrderWriteDAO orderWriteDAO ;
	@Autowired
	private SerialGenZkImpl serialGenZkImpl ;
	

	@Autowired
	private DefRedisClient defRedisClient;
	
	public BaseResult<List<HnpOutInMoney>> confirmRefund(HnpRefund reqDTO) throws Exception {
		String mainOrderNo = reqDTO.getMainOrderNo();
		if (StringUtils.isBlank(mainOrderNo)) {
			return BaseResult.fail(OrderResultCode.PARAM_0006);
		}
		Double payerAmt = reqDTO.getPayerAmt();
		if (payerAmt == null) {
			return BaseResult.fail(OrderResultCode.PARAM_0019);
		}
		String orderSerialNumber = reqDTO.getSerialNumber();
		RedisLock lock = null;
		try {
			// 0 对每一笔预支付订单进行锁处理，防止重复提交
			lock = new RedisLock(defRedisClient,RedisKey.ORDER_REFUND_KEY.value.concat(orderSerialNumber));
			if (!lock.lock()) {
				logger.info("订单：" + mainOrderNo + "退款超时...");
				return BaseResult.fail(OrderResultCode.DB_0016);
			}
			
			// 1 校验子订单信息 （单笔订单信息）
			HnpOrderEntity orderEntity = orderReadDAO.selectBySerialNumber(orderSerialNumber);
			if(null == orderEntity){
				logger.info("订单：" + mainOrderNo + "在库中信息不存在");
				return BaseResult.fail(OrderResultCode.DB_0023);
			}
			if (!orderEntity.isSettled()) {
				logger.info("订单号:{" + reqDTO.getOrderNo() + "},流水号:{"+ orderSerialNumber + "} 还未结算，请核实");
				return BaseResult.fail(OrderResultCode.DB_0009);
			}
			// 计算可用余额=入金支付总额-已发生额
			// 校验退款买家的金额应小于或等于支付订单金额
			HnpMainOrderEntity mainOrderEntity = mainOrderReadDAO.selectByMainOrderNo(mainOrderNo);
			if (null == mainOrderEntity) {
				return BaseResult.fail(OrderResultCode.DB_0005);
			}
			double lastBalance = mainOrderEntity.getTotalAmt().doubleValue();
			double occur = outInMoneyReadDAO.calcOccurByMainOrderNo(mainOrderNo);
			double nextBalance = lastBalance - occur;
			double receiveFee = orderEntity.getAmt().doubleValue();
			// 当前付款金额必须小于或等于当前余额
			if (receiveFee > nextBalance) {
				logger.info("当前付款金额大于平台可用余额,amt:" + receiveFee + ",balace:"+ nextBalance);
				return BaseResult.fail(OrderResultCode.DB_0013);
			}

			// 退款给买家
			List<HnpOutInMoney> directCashlist = new ArrayList<HnpOutInMoney>();
			//组买家信息
			Map<String,Object> payerMap = new HashMap<String,Object>();
			payerMap.put("amt",reqDTO.getPayerAmt());
			payerMap.put("transAmt", reqDTO.getPayerAmt());
			payerMap.put("mainOrderNo", reqDTO.getMainOrderNo());
			payerMap.put("orderNo", reqDTO.getOrderNo());
			payerMap.put("orderSerialNumber", orderSerialNumber);
			payerMap.put("payChannel", reqDTO.getPayChannel());
			payerMap.put("userId", reqDTO.getAppPayerId());
			payerMap.put("userType", CmbPayShopEnum.CMB_PAY_SHOP_BUYER.val);
			//验证买家信息
			BaseResult<Void> payerCheckResult = checkReqMsg(payerMap, reqDTO.getPayerReceiptCard());
			if(null == payerCheckResult || payerCheckResult.getCode()  != ResultCode.SUCCESS.getCode()){
				return new BaseResult<List<HnpOutInMoney>>(payerCheckResult.getCode(),payerCheckResult.getMsg());
			}
			BaseResult<HnpOutInMoney> payerResult = saveOutMoney(payerMap, reqDTO.getPayerReceiptCard());
			if (null == payerResult || payerResult.getData() == null || payerResult.getCode() != ResultCode.SUCCESS.getCode()) {
				return new BaseResult<List<HnpOutInMoney>>(payerResult.getCode(),payerResult.getMsg());
			}
			directCashlist.add(payerResult.getData());
			
			// 判断是否为部分退款 退款给卖家
			if (reqDTO.isPartRefund()) {
				//组卖家信息
				Map<String,Object> payeeMap = new HashMap<String,Object>();
				payeeMap.put("amt",reqDTO.getPayeeAmt());
				payeeMap.put("transAmt", reqDTO.getPayeeAmt());
				payeeMap.put("mainOrderNo", reqDTO.getMainOrderNo());
				payeeMap.put("orderNo", reqDTO.getOrderNo());
				payeeMap.put("orderSerialNumber", orderSerialNumber);
				payeeMap.put("payChannel", reqDTO.getPayChannel());
				payeeMap.put("userId", reqDTO.getAppPayeeId());
				payeeMap.put("userType", CmbPayShopEnum.CMB_PAY_SHOP_SELLER.val);
				//验证买家信息
				BaseResult<Void> payeeCheckResult = checkReqMsg(payerMap, reqDTO.getPayerReceiptCard());
				if(null == payeeCheckResult || payeeCheckResult.getCode()  != ResultCode.SUCCESS.getCode()){
					return new BaseResult<List<HnpOutInMoney>>(payeeCheckResult.getCode(),payeeCheckResult.getMsg());
				}
				BaseResult<HnpOutInMoney> payeeResult = saveOutMoney(payeeMap, reqDTO.getPayeeReceiptCard());
				if (null == payeeResult || payeeResult.getData() == null || payeeResult.getCode() != ResultCode.SUCCESS.getCode()) {
					return new BaseResult<List<HnpOutInMoney>>(payeeResult.getCode(),payeeResult.getMsg());
				}
				directCashlist.add(payeeResult.getData());
			}
			return BaseResult.success(directCashlist);
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
	 * 验证入参
	 * @param orderMap
	 * @param receiptCard
	 * @return
	 */
	private BaseResult<Void> checkReqMsg(Map<String,Object> orderMap, ReceiptCard receiptCard){
		ParamHandler param = new ParamHandler(orderMap);
		//买家类型
		String userType = param.getString("userType");
		if(StringUtils.isBlank(userType)){
			logger.info("无法识别{用户类型:买卖家}");
			return BaseResult.fail(OrderResultCode.PARAM_0034);
		}
		//错误码
		ResultCodeIntf errCode = null ;
		boolean payerFlag = userType.equals(CmbPayShopEnum.CMB_PAY_SHOP_BUYER.val) ? true:false ;
		
		if(null == param.getDouble("amt")){
			 logger.info("丢失关键字{金额}");
			 errCode = payerFlag ? OrderResultCode.PARAM_0019 : OrderResultCode.PARAM_0033 ;
			 return BaseResult.fail(errCode);
		}
		if(StringUtils.isBlank(param.getString("mainOrderNo"))){
			logger.info("丢失关键字{结算订单号}");
            return BaseResult.fail(OrderResultCode.PARAM_0035);
		}
		if (StringUtils.isBlank(param.getString("orderNo"))) {
            logger.info("丢失关键字{订单编号}");
            return BaseResult.fail(OrderResultCode.PARAM_0006);
		}
		if(null == param.getLong("userId")){
			 logger.info("丢失关键字{收款人账户ID}");
			 errCode = payerFlag ? OrderResultCode.PARAM_0007 : OrderResultCode.PARAM_0010;
             return BaseResult.fail(errCode);
		}
		if(receiptCard == null){
			 logger.info("丢失关键字{收款卡信息(true-买家,false-卖家)}:"+payerFlag);
             return BaseResult.fail(OrderResultCode.PARAM_0031);
		}
		//收款卡账号
		if(StringUtils.isBlank(receiptCard.getReceiptAccount())){
			 logger.info("丢失关键字{卖家收款卡账号(true-买家,false-卖家)}:"+payerFlag);
             return BaseResult.fail(OrderResultCode.PARAM_0011);
		}
		//收款人名称
		if(StringUtils.isBlank(receiptCard.getReceiptName())){
			 logger.info("丢失关键字{卖家收款卡人姓名(true-买家,false-卖家)}:"+payerFlag);
             return BaseResult.fail(OrderResultCode.PARAM_0012);
		}
		//招商卡标志
		if(StringUtils.isBlank(receiptCard.getBankFLG())){
			logger.info("丢失关键字{招商银行标志(true-买家,false-卖家)}:"+payerFlag);
            return BaseResult.fail(OrderResultCode.PARAM_0013);
		}
		//收款卡地址
		if(StringUtils.isBlank(receiptCard.getReceiptBankAddress())){
			 logger.info("丢失关键字{卖家收款行开户地址(true-买家,false-卖家)}:"+payerFlag);
             return BaseResult.fail(OrderResultCode.PARAM_0014);
		}
		//收款卡银行
		if(StringUtils.isBlank(receiptCard.getReceiptBank())){
			 logger.info("丢失关键字{卖家收款行银行}(true-买家,false-卖家)}:"+payerFlag);
             return BaseResult.fail(OrderResultCode.PARAM_0015);
		}
		return BaseResult.success();
	}
	
	/**
	 * 保存付款单信息
	 * @param orderMap
	 * @param receiptCard
	 * @return
	 * @throws Exception
	 */
	private BaseResult<HnpOutInMoney> saveOutMoney(Map<String,Object> orderMap, ReceiptCard receiptCard) throws Exception { 
		ParamHandler param = new ParamHandler(orderMap);
		//订单金额 
		Double amt = param.getDouble("amt") ;
		//付款金额
		Double transAmt = param.getDouble("transAmt") ;
		//主订单号
		String mainOrderNo = param.getString("mainOrderNo");
		//子订单号
		String orderNo = param.getString("orderNo");
		//子订单流水号
		String orderSerialNumber = param.getString("orderSerialNumber");
		//支付渠道
		String payChannel = param.getString("payChannel");
		//收款人账户ID
		Long userId = param.getLong("userId");
		//收款人类型 买家还是卖家  CmbPayShopEnum.CMB_PAY_SHOP_SELLER.val
		String userType = param.getString("userType");
		//付款方式 付款给卖家 还是 退款  DirectEventEnum.DIRECT_REFUND.val
		String type = DirectEventEnum.DIRECT_REFUND.val;
		//付款流水号
		String refundSerialNumber = serialGenZkImpl.genRefundSerialNo();
		//查询付款单信息
		OutInMoneyEntity outMoneyEntity = outInMoneyReadDAO.getByOrderSerialNumber(orderSerialNumber,userType);
		if (null != outMoneyEntity) {
			if (outMoneyEntity.isPayFail()) {
				logger.info("订单号：" + orderNo + "付款失败,msg："+ outMoneyEntity.getResMessage());
				logger.info("付款失败");
				return BaseResult.fail(OrderResultCode.DB_0010);
			}
			if (outMoneyEntity.isPaySuc()) {
				logger.info("订单号：" + orderNo + "付款成功,请勿重复提交");
				logger.info("付款成功，请勿重复提交");
				return BaseResult.fail(OrderResultCode.DB_0011);
			}
			if (outMoneyEntity.isPaying() || outMoneyEntity.isToPay()) {
				// 处理中-->直接返回付款单信息，进行支付（先同步支付状态）
				HnpOutInMoney returnbean = new HnpOutInMoney();
				CopyBeanUtil.getInstance().copyBeanProperties(outMoneyEntity, returnbean);
				return BaseResult.success(returnbean);
			}
		}
		// 出金明细表
		OutInMoneyEntity outInMoneyDTO = new OutInMoneyEntity();
		outInMoneyDTO.setAccno(receiptCard.getReceiptAccount());
		outInMoneyDTO.setAccountName(receiptCard.getReceiptName());
		outInMoneyDTO.setAmount(amt);
		outInMoneyDTO.setBankFlg(receiptCard.getBankFLG());
		outInMoneyDTO.setDirectStatus(DirectStateEnum.INITIAL.val);
		outInMoneyDTO.setMainOrderNo(mainOrderNo);
		outInMoneyDTO.setOrderNo(orderNo);
		outInMoneyDTO.setOrderSerialNumber(orderSerialNumber);
		outInMoneyDTO.setPayChannel(payChannel);
		outInMoneyDTO.setPayTime(new Date());
		outInMoneyDTO.setReceiveBankAddr(receiptCard.getReceiptBankAddress());
		outInMoneyDTO.setReceiveBankName(receiptCard.getReceiptBank());
		outInMoneyDTO.setReceiveUserid(userId);
		outInMoneyDTO.setReceiveUserType(userType); /*CmbPayShopEnum.CMB_PAY_SHOP_SELLER.val*/
		outInMoneyDTO.setSerialNumber(refundSerialNumber);
		outInMoneyDTO.setTransAmt(transAmt);
		outInMoneyDTO.setType(type);/*DirectEventEnum.DIRECT_REFUND.val*/
		outInMoneyDTO.setFee(0.0d);
		outInMoneyDTO.setFeeRule("");
		int i = outInMoneyWriteDAO.addOneOutInMoney(outInMoneyDTO);
		if (i <= 0) {
			logger.info("制付款单入库异常");
			return BaseResult.fail(OrderResultCode.DB_0014);
		}
		// 组付款制表单
		HnpOutInMoney outInMoney = new HnpOutInMoney();
		CopyBeanUtil.getInstance().copyBeanProperties(outInMoneyDTO, outInMoney);
		return BaseResult.success(outInMoney);
	}
}
