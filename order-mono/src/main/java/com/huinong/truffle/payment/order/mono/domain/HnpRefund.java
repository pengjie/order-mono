package com.huinong.truffle.payment.order.mono.domain;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.huinong.truffle.payment.order.mono.util.MD5Util;

/**
 * 惠农网退货(退款)
 * @author peng
 *
 */
public class HnpRefund implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//结算订单编号
	private String mainOrderNo ;
	
	//买家金额
	private Double payerAmt ;
	
	//卖家金额
	private Double payeeAmt ;
	
    //订单来源 HNWSE | HNYX
    private String req_from ;
	
    //平台标识
    private Integer appId ;
    
    //订单流水号
    private String serialNumber;
    
    
	//追加支付方式
    private String payChannel ;
    
    //追加原支付单号
    private String paymentNo ;
    
    //追加订单编号
    private String orderNo ;
    
    //追加订单ID
    /*private Long orderId ;*/
    
    //买家惠农网用户ID
    private Long appPayerId ;
    //卖家惠农网用户ID
    private Long appPayeeId ;
    //订单描叙
    private String orderDesc ;
    //商品名称
    private String shopName ;
    
	//买家收款卡信息
	private ReceiptCard payerReceiptCard ;
	
	//卖家收款卡信息
	private ReceiptCard payeeReceiptCard ;

	public String getMainOrderNo() {
		return mainOrderNo;
	}

	public void setMainOrderNo(String mainOrderNo) {
		this.mainOrderNo = mainOrderNo;
	}

	public Double getPayerAmt() {
		return payerAmt;
	}

	public void setPayerAmt(Double payerAmt) {
		this.payerAmt = payerAmt;
	}

	public Double getPayeeAmt() {
		return payeeAmt;
	}

	public void setPayeeAmt(Double payeeAmt) {
		this.payeeAmt = payeeAmt;
	}

	public String getReq_from() {
		return req_from;
	}

	public void setReq_from(String req_from) {
		this.req_from = req_from;
	}

	public String getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}

	public String getPaymentNo() {
		return paymentNo;
	}

	public void setPaymentNo(String paymentNo) {
		this.paymentNo = paymentNo;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public ReceiptCard getPayerReceiptCard() {
		return payerReceiptCard;
	}

	public void setPayerReceiptCard(ReceiptCard payerReceiptCard) {
		this.payerReceiptCard = payerReceiptCard;
	}

	public ReceiptCard getPayeeReceiptCard() {
		return payeeReceiptCard;
	}

	public void setPayeeReceiptCard(ReceiptCard payeeReceiptCard) {
		this.payeeReceiptCard = payeeReceiptCard;
	}
	
	public Integer getAppId() {
		return appId;
	}

	public void setAppId(Integer appId) {
		this.appId = appId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Long getAppPayerId() {
		return appPayerId;
	}

	public void setAppPayerId(Long appPayerId) {
		this.appPayerId = appPayerId;
	}

	public Long getAppPayeeId() {
		return appPayeeId;
	}

	public void setAppPayeeId(Long appPayeeId) {
		this.appPayeeId = appPayeeId;
	}
	
	public String getOrderDesc() {
		return orderDesc;
	}

	public void setOrderDesc(String orderDesc) {
		this.orderDesc = orderDesc;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	//是否为部分退款
	public boolean isPartRefund(){
		boolean isflag = false ;
		if(null != payeeAmt && payeeAmt > 0){
			isflag = true ;
		}
		return  isflag ;
	}
	
	 /**
     * 获取对象内容的UUID 用于前后两次交互比较
     */
    public String getObjectUUID(){
    	if(null == payerAmt){
    		payerAmt = 0.0d;
    	}
    	if(null == payeeAmt){
    		payeeAmt = 0.0d;
    	}
    	
        StringBuilder buffer = new StringBuilder();
        buffer.append(appId == null ? "appId":appId);
        buffer.append(null == appPayerId ? "appPayerId":appPayerId);
        buffer.append(null == appPayeeId ? "appPayeeId":appPayeeId);
        buffer.append(payerAmt+payeeAmt);
        buffer.append(StringUtils.isBlank(mainOrderNo) ? "mainOrderNo":mainOrderNo);
        buffer.append(StringUtils.isBlank(orderNo) ? "orderNo":orderNo);
        return MD5Util.MD5Encode(buffer.toString(), "UTF-8").toUpperCase();
    }
}
