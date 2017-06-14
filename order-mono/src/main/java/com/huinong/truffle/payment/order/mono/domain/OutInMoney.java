package com.huinong.truffle.payment.order.mono.domain;

import java.io.Serializable;
import java.util.Date;

import com.huinong.truffle.payment.order.mono.constant.OrderConstants;

/**
 * 付款记录表
 * @author peng
 *
 */
public class OutInMoney implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//主订单
	private String mainOrderNo ;
	
	//子订单
	private String orderNo ;
	
	//子订单流水号
	private String orderSerialNumber ;
	
	//子订单ID
	private Long orderId ;
	
	//子订单金额
	private Double amount ;
	
	//手续费
	private Double fee ;
	
	//手续费规则
	private String feeRule ;
	
	//需支付金额
	private Double transAmt ;
	
	//收款方用户ID
	private Long receiveUserid ;
	
	//收款方用户类型：1-买家 2-卖家
	private String receiveUserType ;
	
	//收款方账号
	private String accno ;
	
	//收款方账户名称
	private String accountName ;
	
	//收款方地址
	private String receiveBankAddr ;
	
	//收款方银行名称
	private String receiveBankName ;
	
	//付款流水号
	private String serialNumber ;
	
	//来源渠道
	private String payChannel ;
	
	//付款时间
	private Date payTime ;
	
	//付款状态：0:待付款，1:付款成功，2:付款失败,3:处理中
	private String directStatus ;
	
	//是否招商卡：Y:是  N:不是
	private String bankFlg;
	
	//付款方向 0-付款给卖家 1-付款给买家
	private String type ;
	
	//查询库，返回错误信息
	private String resMessage;
	
	private String resCode ;

	public String getMainOrderNo() {
		return mainOrderNo;
	}

	public void setMainOrderNo(String mainOrderNo) {
		this.mainOrderNo = mainOrderNo;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderSerialNumber() {
		return orderSerialNumber;
	}

	public void setOrderSerialNumber(String orderSerialNumber) {
		this.orderSerialNumber = orderSerialNumber;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

	public String getFeeRule() {
		return feeRule;
	}

	public void setFeeRule(String feeRule) {
		this.feeRule = feeRule;
	}

	public Double getTransAmt() {
		return transAmt;
	}

	public void setTransAmt(Double transAmt) {
		this.transAmt = transAmt;
	}

	public Long getReceiveUserid() {
		return receiveUserid;
	}

	public void setReceiveUserid(Long receiveUserid) {
		this.receiveUserid = receiveUserid;
	}

	public String getReceiveUserType() {
		return receiveUserType;
	}

	public void setReceiveUserType(String receiveUserType) {
		this.receiveUserType = receiveUserType;
	}

	public String getAccno() {
		return accno;
	}

	public void setAccno(String accno) {
		this.accno = accno;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getReceiveBankAddr() {
		return receiveBankAddr;
	}

	public void setReceiveBankAddr(String receiveBankAddr) {
		this.receiveBankAddr = receiveBankAddr;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public String getDirectStatus() {
		return directStatus;
	}

	public void setDirectStatus(String directStatus) {
		this.directStatus = directStatus;
	}

	public String getBankFlg() {
		return bankFlg;
	}

	public void setBankFlg(String bankFlg) {
		this.bankFlg = bankFlg;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getResMessage() {
		return resMessage;
	}

	public void setResMessage(String resMessage) {
		this.resMessage = resMessage;
	}

	public String getReceiveBankName() {
		return receiveBankName;
	}

	public void setReceiveBankName(String receiveBankName) {
		this.receiveBankName = receiveBankName;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getResCode() {
		return resCode;
	}

	public void setResCode(String resCode) {
		this.resCode = resCode;
	}

	/**
	 * 处在付款中
	 * @return
	 */
	public boolean isPaying(){
		boolean isFlag = false ;
		if(null != directStatus && directStatus.equals(OrderConstants.DirectStateEnum.PROCESSING.val)){
			isFlag = true ;
		}
		return isFlag ;
	}
	
	/**
	 * 处在付款失败
	 * @return
	 */
	public boolean isPayFail(){
		boolean isFlag = false ;
		if(null != directStatus && directStatus.equals(OrderConstants.DirectStateEnum.FAIL.val)){
			isFlag = true ;
		}
		return isFlag ;
	}
	
	/**
	 * 处在付款成功
	 * @return
	 */
	public boolean isPaySuc(){
		boolean isFlag = false ;
		if(null != directStatus && directStatus.equals(OrderConstants.DirectStateEnum.SUCCESS.val)){
			isFlag = true ;
		}
		return isFlag ;
	}
	
	/**
	 * 处在付款成功
	 * @return
	 */
	public boolean isToPay(){
		boolean isFlag = false ;
		if(null != directStatus && directStatus.equals(OrderConstants.DirectStateEnum.INITIAL.val)){
			isFlag = true ;
		}
		return isFlag ;
	}
}
