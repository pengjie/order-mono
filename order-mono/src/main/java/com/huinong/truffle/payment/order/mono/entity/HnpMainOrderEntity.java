package com.huinong.truffle.payment.order.mono.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.huinong.truffle.payment.order.mono.constant.OrderConstants;

/**
 * 主訂單
 * @author peng
 *
 */
public class HnpMainOrderEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//主键ID
	private Long id;
	//主订单编号
    private String mainOrderNo;
    //系统来源
    private String sourceSys;
    //订单金额
    private BigDecimal totalAmt;
    //买家账户ID
    private Long outUid;
    //支付渠道
    private String hnchannel;
    //订单状态
    private Integer orderState;
    //创建时间
    private Date createTime;
    //修改时间
    private Date modifyTime;
    //请求入参关键信息UUID
    private String msgUUID ;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMainOrderNo() {
		return mainOrderNo;
	}
	public void setMainOrderNo(String mainOrderNo) {
		this.mainOrderNo = mainOrderNo;
	}
	public String getSourceSys() {
		return sourceSys;
	}
	public void setSourceSys(String sourceSys) {
		this.sourceSys = sourceSys;
	}
	public BigDecimal getTotalAmt() {
		return totalAmt;
	}
	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}
	public Long getOutUid() {
		return outUid;
	}
	public void setOutUid(Long outUid) {
		this.outUid = outUid;
	}
	public String getHnchannel() {
		return hnchannel;
	}
	public void setHnchannel(String hnchannel) {
		this.hnchannel = hnchannel;
	}
	public Integer getOrderState() {
		return orderState;
	}
	public void setOrderState(Integer orderState) {
		this.orderState = orderState;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	
	public String getMsgUUID() {
		return msgUUID;
	}
	public void setMsgUUID(String msgUUID) {
		this.msgUUID = msgUUID;
	}
	/**
     * 订单状态是否为待支付状态
     * @return
     */
    public boolean isWaitingConfirm(){
        boolean isTrue = false;
        if(null != orderState && (orderState.intValue() == OrderConstants.OrderStateEnum.ORDER_0.val.intValue())){
            isTrue = true ;
        }
        return isTrue;
    }
}
