/*package com.huinong.truffle.payment.order.mono.domain;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.huinong.truffle.payment.order.mono.constant.OrderConstants;
import com.huinong.truffle.payment.order.mono.util.MD5Util;

public class HnpDetail implements Serializable{

    private static final long serialVersionUID = 1L;
    
    //卖家
    private String payeeUserName ;
    //买家ID
    private Long appPayerId;
    //订单金额
    private Double amt ;
    //子订单
    private String orderNo ;
    //平台标识
    private Integer appId ;
    //卖家ID
    private Long appPayeeId ;
    //订单描叙
    private String orderDesc ;
    //买家
    private String shopName ;
    
    private String mainOrderNo ;
    
    //订单编号[查询订单列表主键]
    private Long orderId ;
    
    //订单状态 0-待买家付款，1-买家付款中，2-买家已付款，5-待买家确认（已结算）,3-买家确认收货（交易成功）,4-买家退款（交易结束）
    private String payState ;
    
    //订单流水号
    private String serialNumber ;
    
    *//** 订单支付时间 *//*
    private Date payedTimestamp;
    
    *//** 交易完成时间 *//*
    private Date finishedTimestamp;
    
    *//** 交易关闭时间 *//*
    private Date closedTimestamp;
    
    *//** 退款时间 *//*
    private Date refundTimestamp;
    
    public String getPayeeUserName() {
        return payeeUserName;
    }

    public void setPayeeUserName(String payeeUserName) {
        this.payeeUserName = payeeUserName;
    }

    public Long getAppPayerId() {
        return appPayerId;
    }

    public void setAppPayerId(Long appPayerId) {
        this.appPayerId = appPayerId;
    }

    public Double getAmt() {
        return amt;
    }

    public void setAmt(Double amt) {
        this.amt = amt;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
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

    public String getMainOrderNo() {
        return mainOrderNo;
    }

    public void setMainOrderNo(String mainOrderNo) {
        this.mainOrderNo = mainOrderNo;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getPayState() {
        return payState;
    }

    public void setPayState(String payState) {
        this.payState = payState;
    }
    
	public Date getPayedTimestamp()
	{
		return payedTimestamp;
	}

	public void setPayedTimestamp(Date payedTimestamp)
	{
		this.payedTimestamp = payedTimestamp;
	}

	public Date getFinishedTimestamp()
	{
		return finishedTimestamp;
	}

	public void setFinishedTimestamp(Date finishedTimestamp)
	{
		this.finishedTimestamp = finishedTimestamp;
	}

	public Date getClosedTimestamp()
	{
		return closedTimestamp;
	}

	public void setClosedTimestamp(Date closedTimestamp)
	{
		this.closedTimestamp = closedTimestamp;
	}

	public Date getRefundTimestamp()
	{
		return refundTimestamp;
	}

	public void setRefundTimestamp(Date refundTimestamp)
	{
		this.refundTimestamp = refundTimestamp;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	  *//**
     * 获取订单是否已结算(待买家确认)
     * @return
     *//*
    public boolean isSettled(){
        boolean isflag = false ;
        if(null != payState){
            if(payState.equals(String.valueOf(OrderConstants.OrderStateEnum.ORDER_5.val.intValue()))){
                isflag = true ;
            }
        }
        return isflag;
    }
    
    *//**
     * 获取对象内容的UUID 用于前后两次交互比较
     *//*
    public String getObjectUUID(){
        StringBuilder buffer = new StringBuilder();
        buffer.append(appId == null ? "appId":appId);
        buffer.append(null == appPayerId ? "appPayerId":appPayerId);
        buffer.append(null == appPayerId ? "appPayerId":appPayerId);
        buffer.append(null == amt ? "amt":amt);
        buffer.append(StringUtils.isBlank(mainOrderNo) ? "mainOrderNo":mainOrderNo);
        buffer.append(StringUtils.isBlank(orderNo) ? "orderNo":orderNo);
        return MD5Util.MD5Encode(buffer.toString(), "UTF-8").toUpperCase();
    }

}
*/