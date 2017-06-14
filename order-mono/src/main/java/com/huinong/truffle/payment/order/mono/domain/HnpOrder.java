package com.huinong.truffle.payment.order.mono.domain;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.huinong.truffle.payment.order.mono.constant.OrderConstants;
import com.huinong.truffle.payment.order.mono.util.MD5Util;


/**
 * 订单DTO
 * @author zhou
 *
 */
public class HnpOrder implements Serializable {

    private static final long serialVersionUID = 5045267857288366377L;

    //查询主订单ID
    private Long id ;
    
    //平台标识 默认为0
    private Integer appId ;
    
    //订单来源 HNW | HNYX
    private String req_from ;

    //主订单号
    private String mainOrderNo ;

    //订单总额
    private Double totalAmount ;
    
    //买家账号
    private Long appPayerId ; 
    
    //下单渠道 PC IOS AD H5
    private String hnchannel="H5" ;
    
    //支付流水号
//    private String paySerialNumber;
    
    //订单状态
    private Integer orderStatus;
    
    //支付渠道 0：卡, 2:快捷支付 ,6:支付宝,7:微信
    private String payChannel ;
    
    
    //订单明细
    private List<HnpDetail> data ;
    

    public HnpOrder(){
        
    }

    public HnpOrder(String req_from, String mainOrderNo, Double totalAmount) {
        this.req_from = req_from;
        this.mainOrderNo = mainOrderNo;
        this.totalAmount = totalAmount;
    }

    public String getReq_from() {
        return req_from;
    }

    public void setReq_from(String req_from) {
        this.req_from = req_from;
    }

    public String getMainOrderNo() {
        return mainOrderNo;
    }

    public void setMainOrderNo(String mainOrderNo) {
        this.mainOrderNo = mainOrderNo;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<HnpDetail> getData() {
        return data;
    }

    public void setData(List<HnpDetail> data) {
        this.data = data;
    }

    public Long getAppPayerId() {
        return appPayerId;
    }

    public void setAppPayerId(Long appPayerId) {
        this.appPayerId = appPayerId;
    }

    public String getHnchannel() {
        return hnchannel;
    }

    public void setHnchannel(String hnchannel) {
        this.hnchannel = hnchannel;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }
    

    /**
     * 订单状态是否为待支付状态
     * @return
     */
    public boolean isWaitingConfirm(){
        boolean isTrue = false;
        if(null != orderStatus && (orderStatus.intValue() == OrderConstants.OrderStateEnum.ORDER_0.val.intValue())){
            isTrue = true ;
        }
        return isTrue;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
    /**
     * 获取对象内容的UUID 用于前后两次交互比较
     */
    public String getObjectUUID(){
        StringBuilder buffer = new StringBuilder();
        buffer.append(appId == null ? "appId":appId);
        buffer.append(StringUtils.isBlank(req_from) ? "req_from":req_from);
        buffer.append(StringUtils.isBlank(mainOrderNo) ? "mainOrderNo":mainOrderNo);
        buffer.append(null == totalAmount ? "totalAmount":totalAmount);
        buffer.append(null == appPayerId ? "appPayerId":appPayerId);
        buffer.append(StringUtils.isBlank(hnchannel) ? "hnchannel":hnchannel);
        buffer.append((data == null || data.size()==0) ? "data":data);
        return MD5Util.MD5Encode(buffer.toString(), "UTF-8").toUpperCase();
    }
}
