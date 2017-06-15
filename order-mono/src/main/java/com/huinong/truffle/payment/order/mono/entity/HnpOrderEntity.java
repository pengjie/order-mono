package com.huinong.truffle.payment.order.mono.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.huinong.truffle.payment.order.mono.constant.OrderConstants;

/**
 * 订单明细实体
 * @author peng
 *
 */
public class HnpOrderEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
    private String orderId;
    private Integer appId;
    private String orderFromSystem;
    private String orderSummary;
    private Long outUid;
    private Long inUid;
    private BigDecimal amt;
    private BigDecimal needPay;
    private BigDecimal sysOffAmt;
    private BigDecimal payOffAmt;
    private Date orderTimestamp;
    private String payState;
    private Integer deleted;
    private String hash;
    private String refundState;
    private Date payedTimestamp;
    private Date finishedTimestamp;
    private Date closedTimestamp;
    private Date refundTimestamp;
    private String groupOrderNo;
    private BigDecimal merchantOffAmt;
    private String shopName;
    private String inUname;
    private String serialNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getOrderFromSystem() {
        return orderFromSystem;
    }

    public void setOrderFromSystem(String orderFromSystem) {
        this.orderFromSystem = orderFromSystem == null ? null : orderFromSystem.trim();
    }

    public String getOrderSummary() {
        return orderSummary;
    }

    public void setOrderSummary(String orderSummary) {
        this.orderSummary = orderSummary == null ? null : orderSummary.trim();
    }

    public Long getOutUid() {
        return outUid;
    }

    public void setOutUid(Long outUid) {
        this.outUid = outUid;
    }

    public Long getInUid() {
        return inUid;
    }

    public void setInUid(Long inUid) {
        this.inUid = inUid;
    }

    public BigDecimal getAmt() {
        return amt;
    }

    public void setAmt(BigDecimal amt) {
        this.amt = amt;
    }

    public BigDecimal getNeedPay() {
        return needPay;
    }

    public void setNeedPay(BigDecimal needPay) {
        this.needPay = needPay;
    }

    public BigDecimal getSysOffAmt() {
        return sysOffAmt;
    }

    public void setSysOffAmt(BigDecimal sysOffAmt) {
        this.sysOffAmt = sysOffAmt;
    }

    public BigDecimal getPayOffAmt() {
        return payOffAmt;
    }

    public void setPayOffAmt(BigDecimal payOffAmt) {
        this.payOffAmt = payOffAmt;
    }

    public Date getOrderTimestamp() {
        return orderTimestamp;
    }

    public void setOrderTimestamp(Date orderTimestamp) {
        this.orderTimestamp = orderTimestamp;
    }

    public String getPayState() {
        return payState;
    }

    public void setPayState(String payState) {
        this.payState = payState == null ? null : payState.trim();
    }

    public Integer getDeleted() {
		return deleted;
	}

	public void setDeleted(Integer deleted) {
		this.deleted = deleted;
	}

	public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash == null ? null : hash.trim();
    }

    public String getRefundState() {
        return refundState;
    }

    public void setRefundState(String refundState) {
        this.refundState = refundState == null ? null : refundState.trim();
    }

    public Date getPayedTimestamp() {
        return payedTimestamp;
    }

    public void setPayedTimestamp(Date payedTimestamp) {
        this.payedTimestamp = payedTimestamp;
    }

    public Date getFinishedTimestamp() {
        return finishedTimestamp;
    }

    public void setFinishedTimestamp(Date finishedTimestamp) {
        this.finishedTimestamp = finishedTimestamp;
    }

    public Date getClosedTimestamp() {
        return closedTimestamp;
    }

    public void setClosedTimestamp(Date closedTimestamp) {
        this.closedTimestamp = closedTimestamp;
    }

    public Date getRefundTimestamp() {
        return refundTimestamp;
    }

    public void setRefundTimestamp(Date refundTimestamp) {
        this.refundTimestamp = refundTimestamp;
    }

    public String getGroupOrderNo() {
        return groupOrderNo;
    }

    public void setGroupOrderNo(String groupOrderNo) {
        this.groupOrderNo = groupOrderNo == null ? null : groupOrderNo.trim();
    }

    public BigDecimal getMerchantOffAmt() {
        return merchantOffAmt;
    }

    public void setMerchantOffAmt(BigDecimal merchantOffAmt) {
        this.merchantOffAmt = merchantOffAmt;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName == null ? null : shopName.trim();
    }

    public String getInUname() {
        return inUname;
    }

    public void setInUname(String inUname) {
        this.inUname = inUname == null ? null : inUname.trim();
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber == null ? null : serialNumber.trim();
    }
    
    /**
     * 获取订单是否已结算(待买家确认)
     * @return
     */
    public boolean isSettled(){
        boolean isflag = false ;
        if(null != payState){
            if(payState.equals(String.valueOf(OrderConstants.OrderStateEnum.ORDER_5.val.intValue()))){
                isflag = true ;
            }
        }
        return isflag;
    }
}