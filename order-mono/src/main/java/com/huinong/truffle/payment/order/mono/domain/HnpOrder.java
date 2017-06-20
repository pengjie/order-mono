package com.huinong.truffle.payment.order.mono.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.huinong.truffle.payment.order.mono.constant.OrderConstants;

/**
 * 订单明细实体
 * @author peng
 *
 */
@ApiModel(value="HnpOrder" ,description="订单项对象")
public class HnpOrder implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="订单编号",required=true)
	private String orderId;
	@ApiModelProperty(value="订单金额",required=true)
	private BigDecimal amt;
	@ApiModelProperty(value="主订单号",required=true)
	private String groupOrderNo;
	@ApiModelProperty(value="卖家账户ID",required=true)
    private Long inUid;
	@ApiModelProperty(value="卖家名称",required=true)
	private String inUname;
	@ApiModelProperty(value="买家账户ID",required=true)
    private Long outUid;
	@ApiModelProperty(value="买家名称",required=true)
	private String shopName;
	@ApiModelProperty(value="支付金额",required=true)
	private BigDecimal needPay;
	@ApiModelProperty(value="订单来源",required=true)
	private String orderFromSystem;
	@ApiModelProperty(value="支付描叙",required=true)
	private String orderSummary;
    
	@ApiModelProperty(value="子订单主键ID", hidden=true)
	private Long id;
	@ApiModelProperty(value="平台标识0" ,hidden=true)
    private Integer appId = 0;
	@ApiModelProperty(value="",hidden=true)
    private BigDecimal sysOffAmt;
	@ApiModelProperty(value="",hidden=true)
    private BigDecimal payOffAmt;
	@ApiModelProperty(value="",hidden=true)
    private Date orderTimestamp;
	@ApiModelProperty(value="订单状态",hidden=true)
    private String payState;
	@ApiModelProperty(value="删除状态",hidden=true)
    private Integer deleted;
	@ApiModelProperty(value="hash",hidden=true)
    private String hash;
	@ApiModelProperty(value="退款状态",hidden=true)
    private String refundState;
	@ApiModelProperty(value="付款时间",hidden=true)
    private Date payedTimestamp;
	@ApiModelProperty(value="付款完成时间",hidden=true)
    private Date finishedTimestamp;
	@ApiModelProperty(value="付款关闭时间",hidden=true)
    private Date closedTimestamp;
	@ApiModelProperty(value="退款时间",hidden=true)
    private Date refundTimestamp;
	@ApiModelProperty(value="商家优惠金额",hidden=true)
    private BigDecimal merchantOffAmt;
	@ApiModelProperty(value="子订单流水号",hidden=true)
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