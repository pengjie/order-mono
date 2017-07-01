package com.huinong.truffle.payment.order.mono.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单查询对象
 * @author peng
 *
 */
@ApiModel(value="OrderQuery" ,description="用户查询对象")
public class OrderQuery implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//分页查询条件
    @ApiModelProperty(value = "当前页码", required = false)
    private Integer pageNum = 1 ;
    @ApiModelProperty(value = "每页显示数", required = false)
    private Integer pageSize = 10 ;
    
    //订单创建开始日期
    @ApiModelProperty(value = "开始日期", required = true)
    private String startDate ;
    //订单创建结束日期
    @ApiModelProperty(value = "结束日期", required = true)
    private String endDate ;
    //订单编号
    @ApiModelProperty(value = "订单编号", required = false)
    private String orderNo ;
    //主订单编号
    @ApiModelProperty(value = "主订单编号", required = false)
    private String mainOrderNo ;
    //订单流水号
    @ApiModelProperty(value = "订单流水号", required = false)
    private String orderSerialNumber ;
	public Integer getPageNum() {
		return pageNum;
	}
	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getMainOrderNo() {
		return mainOrderNo;
	}
	public void setMainOrderNo(String mainOrderNo) {
		this.mainOrderNo = mainOrderNo;
	}
	public String getOrderSerialNumber() {
		return orderSerialNumber;
	}
	public void setOrderSerialNumber(String orderSerialNumber) {
		this.orderSerialNumber = orderSerialNumber;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
}
