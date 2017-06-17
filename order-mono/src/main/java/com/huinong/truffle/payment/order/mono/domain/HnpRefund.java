package com.huinong.truffle.payment.order.mono.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.huinong.truffle.payment.order.mono.util.MD5Util;

/**
 * 惠农网退货(退款)
 * @author peng
 *
 */
@ApiModel(value="HnpRefund" ,description="确认退款订单对象")
public class HnpRefund implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//结算订单编号
	@ApiModelProperty(value="主订单编号",required=false)
	private String mainOrderNo ;
    //订单流水号
	@ApiModelProperty(value="订单流水号",required=false)
    private String serialNumber;
    //追加订单编号
	@ApiModelProperty(value="订单编号",required=false)
    private String orderNo ;
	//买家金额
	@ApiModelProperty(value="买家金额",required=false)
	private Double payerAmt ;
	//卖家金额
	@ApiModelProperty(value="卖家金额",required=false)
	private Double payeeAmt ;
	//追加支付方式
	@ApiModelProperty(value="付款方式(CMB-银企直连WX-微信ALI-支付宝)",required=false)
    private String payChannel ;
	
    //订单来源 HNWSE | HNYX
	@ApiModelProperty(value="订单来源(HNW|HNYX)",hidden=true)
    private String req_from ;
    //平台标识
	@ApiModelProperty(value="平台标识",hidden=true)
    private Integer appId = 0 ;
    //追加原支付单号
	@ApiModelProperty(value="支付流水号",hidden=true)
    private String paymentNo ;
    
    //追加订单ID
    /*private Long orderId ;*/
    
    //买家惠农网用户ID
	@ApiModelProperty(value="买家ID",required=false)
    private String appPayerId ;
    //卖家惠农网用户ID
	@ApiModelProperty(value="卖家ID",required=false)
    private String appPayeeId ;
    //订单描叙
	@ApiModelProperty(value="订单描叙",hidden=true)
    private String orderDesc ;
    //商品名称
	@ApiModelProperty(value="买家名称",hidden=true)
    private String shopName ;
    
	//买家收款卡信息
	@ApiModelProperty(value="买家收款卡信息",required=false)
	private ReceiptCard payerReceiptCard ;
	
	//卖家收款卡信息
	@ApiModelProperty(value="卖家收款卡信息",required=true)
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

	
	public String getAppPayerId() {
		return appPayerId;
	}

	public void setAppPayerId(String appPayerId) {
		this.appPayerId = appPayerId;
	}

	public String getAppPayeeId() {
		return appPayeeId;
	}

	public void setAppPayeeId(String appPayeeId) {
		this.appPayeeId = appPayeeId;
	}

	//是否为部分退款
	public boolean isPartRefund(){
		boolean isflag = false ;
		if(null != payeeAmt && payeeAmt > 0){
			isflag = true ;
		}
		return  isflag ;
	}
}
