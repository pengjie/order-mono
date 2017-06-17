package com.huinong.truffle.payment.order.mono.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huinong.truffle.component.base.constants.BaseResult;
import com.huinong.truffle.component.base.constants.ResultCode;
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
import com.huinong.truffle.payment.order.mono.domain.DirectCash;
import com.huinong.truffle.payment.order.mono.domain.HnpRefund;
import com.huinong.truffle.payment.order.mono.domain.ReceiptCard;
import com.huinong.truffle.payment.order.mono.entity.HnpMainOrderEntity;
import com.huinong.truffle.payment.order.mono.entity.HnpOrderEntity;
import com.huinong.truffle.payment.order.mono.entity.OutInMoneyEntity;

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
	
	public BaseResult<List<DirectCash>> confirmRefund(HnpRefund reqDTO) throws Exception {
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
			List<DirectCash> directCashlist = new ArrayList<DirectCash>();
			BaseResult<DirectCash> payerCash = savePayerDirect(orderEntity.getId(), reqDTO);
			if (null == payerCash || payerCash.getData() == null || payerCash.getCode() != ResultCode.SUCCESS.getCode()) {
				return new BaseResult<List<DirectCash>>(payerCash.getCode(),payerCash.getMsg());
			}
			DirectCash cashDTO = payerCash.getData();
			directCashlist.add(cashDTO);

			// 判断是否为部分退款 退款给卖家
			if (reqDTO.isPartRefund()) {
				BaseResult<DirectCash> payeeCash = savePayeeDirect(orderEntity.getId(), reqDTO);
				if (null == payeeCash || payeeCash.getData() == null || payeeCash.getCode() != ResultCode.SUCCESS.getCode()) {
					return new BaseResult<List<DirectCash>>(payeeCash.getCode(),payeeCash.getMsg());
				}
				cashDTO = payeeCash.getData() ;
				directCashlist.add(cashDTO);
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
	 * 退款-保存买家出金记录
	 * 
	 * @param orderId
	 * @param mainOrderNo
	 * @param payeeAmt
	 * @param receiptCard
	 * @return
	 */
	private BaseResult<DirectCash> savePayerDirect(Long orderId,HnpRefund reqDTO) throws Exception {
		ReceiptCard receiptCard = reqDTO.getPayerReceiptCard();
		Double payerAmt = reqDTO.getPayerAmt();
		if (null == payerAmt) {
			logger.info("丢失关键字{买家金额}");
			return BaseResult.fail(OrderResultCode.PARAM_0019);
		}
		if (null == receiptCard) {
			logger.info("丢失关键字{买家收款卡信息}"); 
			return BaseResult.fail(OrderResultCode.PARAM_0031);
		}
		String payerAccount = receiptCard.getReceiptAccount();
		if (StringUtils.isBlank(payerAccount)) {
			logger.info("丢失关键字{买家收款卡账号}");
			return BaseResult.fail(OrderResultCode.PARAM_0031);
		}
		String payerName = receiptCard.getReceiptName();
		if (StringUtils.isBlank(payerName)) {
			logger.info("丢失关键字{买家收款卡人姓名}"); 
			return BaseResult.fail(OrderResultCode.PARAM_0012);
		}
		String bankFLG = receiptCard.getBankFLG();
		if (StringUtils.isBlank(bankFLG)) {
			logger.info("丢失关键字{招商银行标志}");
			return BaseResult.fail(OrderResultCode.PARAM_0013);
		}
		String payerBankAddress = receiptCard.getReceiptBankAddress();
		if (StringUtils.isBlank(payerBankAddress)) {
			logger.info("丢失关键字{买家收款行开户地址}");
			return BaseResult.fail(OrderResultCode.PARAM_0013);
		}
		String payerBank = receiptCard.getReceiptBank();
		if (StringUtils.isBlank(payerBankAddress)) {
			logger.info("丢失关键字{买家收款行开户地址}");
			return BaseResult.fail(OrderResultCode.PARAM_0014);
		}
		String mainOrderNo = reqDTO.getMainOrderNo();
		if (StringUtils.isBlank(mainOrderNo)) {
			logger.info("丢失关键字{结算订单号}");
			return BaseResult.fail(OrderResultCode.PARAM_0014);
		}
		Long appPayeeId = reqDTO.getAppPayeeId();
		if (null == appPayeeId) {
			logger.info("丢失关键字{卖家ID}");
			return BaseResult.fail(OrderResultCode.PARAM_0010);
		}
		Long appPayerId = reqDTO.getAppPayerId();
		String orderNo = reqDTO.getOrderNo();
		if (StringUtils.isBlank(orderNo)) {
			logger.info("丢失关键字{订单编号}");
			return BaseResult.fail(OrderResultCode.PARAM_0006);
		}
		String paymentNo = reqDTO.getPaymentNo();
		if (StringUtils.isBlank(paymentNo)) {
			logger.info("丢失关键字{支付单号}");
			return BaseResult.fail(OrderResultCode.PARAM_0032);
		}
		String orderSerialNumber = reqDTO.getSerialNumber();
		// 2校验子订单流水在付款记录表中的状态
		OutInMoneyEntity outMoneyDTO = outInMoneyReadDAO.getByOrderSerialNumber(orderSerialNumber,CmbPayShopEnum.CMB_PAY_SHOP_BUYER.val);
		if (null != outMoneyDTO) {
			if (outMoneyDTO.isPayFail()) {
				logger.info("订单号：" + orderNo + "付款失败,msg："+ outMoneyDTO.getResMessage());
				return BaseResult.fail(OrderResultCode.DB_0010);
			}
			if (outMoneyDTO.isPaySuc()) {
				logger.info("订单号：" + orderNo + "付款成功,请勿重复提交");
				return BaseResult.fail(OrderResultCode.DB_0011);
			}
			if (outMoneyDTO.isPaying() || outMoneyDTO.isToPay()) {
				// 处理中-->直接返回付款单信息，进行支付（先同步支付状态）
				DirectCash cashDTO = new DirectCash();
				cashDTO.setOrderId(orderId);
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
		
		String serialNumber = serialGenZkImpl.genRefundSerialNo();
		OutInMoneyEntity outInMoneyDTO = new OutInMoneyEntity();
		outInMoneyDTO.setAccno(payerAccount);
		outInMoneyDTO.setAccountName(payerName);
		outInMoneyDTO.setAmount(payerAmt);
		outInMoneyDTO.setBankFlg(bankFLG);
		outInMoneyDTO.setDirectStatus(DirectStateEnum.INITIAL.val);
		outInMoneyDTO.setMainOrderNo(mainOrderNo);
		outInMoneyDTO.setOrderNo(orderNo);
		outInMoneyDTO.setOrderSerialNumber(reqDTO.getSerialNumber());
		outInMoneyDTO.setPayChannel(reqDTO.getPayChannel());
		outInMoneyDTO.setPayTime(new Date());
		outInMoneyDTO.setReceiveBankAddr(payerBankAddress);
		outInMoneyDTO.setReceiveBankName(payerBank);
		outInMoneyDTO.setReceiveUserid(appPayerId);
		outInMoneyDTO.setReceiveUserType(CmbPayShopEnum.CMB_PAY_SHOP_BUYER.val);
		outInMoneyDTO.setSerialNumber(serialNumber);
		outInMoneyDTO.setTransAmt(payerAmt);
		outInMoneyDTO.setType(DirectEventEnum.DIRECT_REFUND.val);
		outInMoneyDTO.setFee(0.0d);
		outInMoneyDTO.setFeeRule("");
		int i = outInMoneyWriteDAO.addOneOutInMoney(outInMoneyDTO);
		if (i == 0) {
			logger.info("制付款单入库异常");
			return BaseResult.fail(OrderResultCode.DB_0014);
		}
		// 组付款参数
		DirectCash cashDTO = new DirectCash();
		cashDTO.setOrderId(orderId);
		cashDTO.setAccno(payerAccount);
		cashDTO.setAccountName(payerName);
		cashDTO.setAmt(payerAmt);
		cashDTO.setBankFlg(bankFLG);
		cashDTO.setDirectStatus(DirectStateEnum.INITIAL.val);
		cashDTO.setMainOrderNo(mainOrderNo);
		cashDTO.setReceiveBankAddr(payerBankAddress);
		cashDTO.setReceiveBankName(payerBank);
		cashDTO.setSerialNumber(serialNumber);
		cashDTO.setTriggerTime(new Date());
		cashDTO.setType(DirectEventEnum.DIRECT_REFUND.val);
		cashDTO.setNeedAmt(payerAmt);
		cashDTO.setHandCharge(0.0D);
		return BaseResult.success(cashDTO);
	}
	
	/**
	 * 退款-保存卖家出金记录
	 * 
	 * @param orderId
	 * @param mainOrderNo
	 * @param payeeAmt
	 * @param receiptCard
	 * @return
	 */
	private BaseResult<DirectCash> savePayeeDirect(Long orderId,HnpRefund reqDTO) throws Exception {
		ReceiptCard receiptCard = reqDTO.getPayeeReceiptCard();
		Double payeeAmt = reqDTO.getPayeeAmt();
		if (null == payeeAmt) {
			logger.info("丢失关键字{卖家金额}");
			return BaseResult.fail(OrderResultCode.PARAM_0033);
		}
		if (null == receiptCard) {
			logger.info("丢失关键字{卖家收款卡信息}");
			return BaseResult.fail(OrderResultCode.PARAM_0031);
		}
		String payeeAccount = receiptCard.getReceiptAccount();
		if (StringUtils.isBlank(payeeAccount)) {
			logger.info("丢失关键字{卖家收款卡账号}");
			return BaseResult.fail(OrderResultCode.PARAM_0011);
		}
		String payeeName = receiptCard.getReceiptName();
		if (StringUtils.isBlank(payeeName)) {
			logger.info("丢失关键字{卖家收款卡人姓名}");
			return BaseResult.fail(OrderResultCode.PARAM_0012);
		}
		String bankFLG = receiptCard.getBankFLG();
		if (StringUtils.isBlank(bankFLG)) {
			logger.info("丢失关键字{招商银行标志}");
			return BaseResult.fail(OrderResultCode.PARAM_0013);
		}
		String payeeBankAddress = receiptCard.getReceiptBankAddress();
		if (StringUtils.isBlank(payeeBankAddress)) {
			logger.info("丢失关键字{卖家收款行开户地址}");
			return BaseResult.fail(OrderResultCode.PARAM_0014);
		}
		String payeeBank = receiptCard.getReceiptBank();
		if (StringUtils.isBlank(payeeBankAddress)) {
			logger.info("丢失关键字{卖家收款行银行}");
			return BaseResult.fail(OrderResultCode.PARAM_0015);
		}

		String mainOrderNo = reqDTO.getMainOrderNo();
		if (StringUtils.isBlank(mainOrderNo)) {
			logger.info("丢失关键字{结算订单号}");
			return BaseResult.fail(OrderResultCode.PARAM_0006);
		}
		Long appPayeeId = reqDTO.getAppPayeeId();
		if (null == appPayeeId) {
			logger.info("丢失关键字{卖家ID}");
			return BaseResult.fail(OrderResultCode.PARAM_0010);
		}
		String orderNo = reqDTO.getOrderNo();
		if (StringUtils.isBlank(orderNo)) {
			logger.info("丢失关键字{订单编号}");
			return BaseResult.fail(OrderResultCode.PARAM_0006);
		}
		String paymentNo = reqDTO.getPaymentNo();
		if (StringUtils.isBlank(paymentNo)) {
			logger.info("丢失关键字{支付单号}");
			return BaseResult.fail(OrderResultCode.PARAM_0006);
		}
		String orderSerialNumber = reqDTO.getSerialNumber();
		// 2校验子订单流水在付款记录表中的状态
		OutInMoneyEntity outMoneyDTO = outInMoneyReadDAO.getByOrderSerialNumber(orderSerialNumber,CmbPayShopEnum.CMB_PAY_SHOP_SELLER.val);
		if (null != outMoneyDTO) {
			if (outMoneyDTO.isPayFail()) {
				logger.info("订单号：" + orderNo + "付款失败,msg："+ outMoneyDTO.getResMessage());
				logger.info("付款失败");
				return BaseResult.fail(OrderResultCode.DB_0010);
			}
			if (outMoneyDTO.isPaySuc()) {
				logger.info("订单号：" + orderNo + "付款成功,请勿重复提交");
				logger.info("付款成功，请勿重复提交");
				return BaseResult.fail(OrderResultCode.DB_0011);
			}
			if (outMoneyDTO.isPaying() || outMoneyDTO.isToPay()) {
				// 处理中-->直接返回付款单信息，进行支付（先同步支付状态）
				DirectCash cashDTO = new DirectCash();
				cashDTO.setOrderId(orderId);
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
		// 出金主表
		String serialNumber = serialGenZkImpl.genRefundSerialNo();
		// 出金明细表
		OutInMoneyEntity outInMoneyDTO = new OutInMoneyEntity();
		outInMoneyDTO.setAccno(payeeAccount);
		outInMoneyDTO.setAccountName(payeeName);
		outInMoneyDTO.setAmount(payeeAmt);
		outInMoneyDTO.setBankFlg(bankFLG);
		outInMoneyDTO.setDirectStatus(DirectStateEnum.INITIAL.val);
		outInMoneyDTO.setMainOrderNo(mainOrderNo);
		outInMoneyDTO.setOrderNo(orderNo);
		outInMoneyDTO.setOrderSerialNumber(reqDTO.getSerialNumber());
		outInMoneyDTO.setPayChannel(reqDTO.getPayChannel());
		outInMoneyDTO.setPayTime(new Date());
		outInMoneyDTO.setReceiveBankAddr(payeeBankAddress);
		outInMoneyDTO.setReceiveBankName(payeeBank);
		outInMoneyDTO.setReceiveUserid(appPayeeId);
		outInMoneyDTO.setReceiveUserType(CmbPayShopEnum.CMB_PAY_SHOP_SELLER.val);
		outInMoneyDTO.setSerialNumber(serialNumber);
		outInMoneyDTO.setTransAmt(payeeAmt);
		outInMoneyDTO.setType(DirectEventEnum.DIRECT_REFUND.val);
		outInMoneyDTO.setFee(0.0d);
		outInMoneyDTO.setFeeRule("");
		int i = outInMoneyWriteDAO.addOneOutInMoney(outInMoneyDTO);
		if (i == 0) {
			logger.info("制付款单入库异常");
			return BaseResult.fail(OrderResultCode.DB_0014);
		}
		// 组付款制表单
		DirectCash cashDTO = new DirectCash();
		cashDTO.setOrderId(orderId);
		cashDTO.setAccno(payeeAccount);
		cashDTO.setAccountName(payeeName);
		cashDTO.setAmt(payeeAmt);
		cashDTO.setBankFlg(bankFLG);
		cashDTO.setDirectStatus(DirectStateEnum.INITIAL.val);
		cashDTO.setMainOrderNo(mainOrderNo);
		cashDTO.setReceiveBankAddr(payeeBankAddress);
		cashDTO.setReceiveBankName(payeeBank);
		cashDTO.setSerialNumber(serialNumber);
		cashDTO.setTriggerTime(new Date());
		cashDTO.setType(DirectEventEnum.DIRECT_REFUND.val);
		cashDTO.setNeedAmt(payeeAmt);
		cashDTO.setHandCharge(0.0D);
		return BaseResult.success(cashDTO);
	}
}
