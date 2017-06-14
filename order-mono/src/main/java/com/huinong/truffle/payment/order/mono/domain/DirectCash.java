package com.huinong.truffle.payment.order.mono.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @author peng
 *
 */
public class DirectCash implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//付款账号
	private String accno ;
	//付款名称
	private String accountName ;
	//付款金额
	private Double amt ;
	//是否招商银行卡
	private String bankFlg ;
	//付款状态
	private String directStatus ;
	//结算订单号
	private String mainOrderNo ;
	//收款银行地址
	private String receiveBankAddr ;
	//收款银行名称
	private String receiveBankName ;
	//结算流水号
	private String serialNumber ;
	//调用时间
	private Date triggerTime ;
	//付款方式：付款给卖家  退款给买卖家
	private String type ;
	//需支付金额
	private Double needAmt ;
	//手续费
	private Double handCharge ;
	//订单ID
	private Long orderId ;
	//付款结果返回
	private String resCode ;
	private String resMessage ;

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

	public Double getAmt() {
		return amt;
	}

	public void setAmt(Double amt) {
		this.amt = amt;
	}

	public String getBankFlg() {
		return bankFlg;
	}

	public void setBankFlg(String bankFlg) {
		this.bankFlg = bankFlg;
	}

	public String getDirectStatus() {
		return directStatus;
	}

	public void setDirectStatus(String directStatus) {
		this.directStatus = directStatus;
	}

	public String getMainOrderNo() {
		return mainOrderNo;
	}

	public void setMainOrderNo(String mainOrderNo) {
		this.mainOrderNo = mainOrderNo;
	}

	public String getReceiveBankAddr() {
		return receiveBankAddr;
	}

	public void setReceiveBankAddr(String receiveBankAddr) {
		this.receiveBankAddr = receiveBankAddr;
	}

	public String getReceiveBankName() {
		return receiveBankName;
	}

	public void setReceiveBankName(String receiveBankName) {
		this.receiveBankName = receiveBankName;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Date getTriggerTime() {
		return triggerTime;
	}

	public void setTriggerTime(Date triggerTime) {
		this.triggerTime = triggerTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Double getNeedAmt() {
		return needAmt;
	}

	public void setNeedAmt(Double needAmt) {
		this.needAmt = needAmt;
	}

	public Double getHandCharge() {
		return handCharge;
	}

	public void setHandCharge(Double handCharge) {
		this.handCharge = handCharge;
	}

	public String getResCode() {
		return resCode;
	}

	public void setResCode(String resCode) {
		this.resCode = resCode;
	}

	public String getResMessage() {
		return resMessage;
	}

	public void setResMessage(String resMessage) {
		this.resMessage = resMessage;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

}
