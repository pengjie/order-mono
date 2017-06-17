package com.huinong.truffle.payment.order.mono.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.huinong.truffle.payment.order.mono.util.MD5Util;

/**
 * 主订单信息
 * @author peng
 *
 */
@ApiModel("预支付订单对象")
public class HnpMainOrder implements Serializable {

private static final long serialVersionUID = 1L;
	
	//主键ID
	@ApiModelProperty(value="主键ID",hidden=true)
	private Long id;
	//主订单编号
	@ApiModelProperty(value="主订单号",required=false)
    private String mainOrderNo;
    //系统来源
	@ApiModelProperty(value="系统来源(HNW|HNYX)",required=false)
    private String sourceSys;
    //订单金额
	@ApiModelProperty(value="订单金额",required=false)
    private BigDecimal totalAmt;
    //买家账户ID
	@ApiModelProperty(value="买家账户ID",required=false)
    private Long outUid;
    //支付渠道
	@ApiModelProperty(value="下单渠道(PC|IOS|AD|H5)",required=false)
    private String hnchannel;
    //订单状态
	@ApiModelProperty(value="主订单状态",hidden=true)
    private Integer orderState;
    //创建时间
	@ApiModelProperty(value="创建时间",hidden=true)
    private Date createTime;
    //修改时间
	@ApiModelProperty(value="修改时间",hidden=true)
    private Date modifyTime;
    
    //订单明细
	@ApiModelProperty(value="订单项",required=true)
    private List<HnpOrder> data ;
    
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
	public List<HnpOrder> getData() {
		return data;
	}
	public void setData(List<HnpOrder> data) {
		this.data = data;
	}
	
	/**
     * 获取对象内容的UUID 用于前后两次交互比较
     */
    public String getObjectUUID(){
        StringBuilder buffer = new StringBuilder();
        buffer.append(0);
        buffer.append(StringUtils.isBlank(sourceSys) ? "req_from":sourceSys);
        buffer.append(StringUtils.isBlank(mainOrderNo) ? "mainOrderNo":mainOrderNo);
        buffer.append(null == totalAmt ? "totalAmount":totalAmt);
        buffer.append(null == outUid ? "appPayerId":outUid);
        buffer.append(StringUtils.isBlank(hnchannel) ? "hnchannel":hnchannel);
        buffer.append((data == null || data.size()==0) ? "data":data);
        return MD5Util.MD5Encode(buffer.toString(), "UTF-8").toUpperCase();
    }
}
