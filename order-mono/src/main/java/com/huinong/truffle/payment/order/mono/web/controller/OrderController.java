package com.huinong.truffle.payment.order.mono.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperation.Status;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.huinong.truffle.component.base.component.version.anno.ApiVersion;
import com.huinong.truffle.component.base.constants.BaseResult;
import com.huinong.truffle.component.base.constants.PageValue;
import com.huinong.truffle.payment.order.mono.constant.OrderResultCode;
import com.huinong.truffle.payment.order.mono.domain.HnpMainOrder;
import com.huinong.truffle.payment.order.mono.domain.HnpOrder;
import com.huinong.truffle.payment.order.mono.domain.OrderQuery;
import com.huinong.truffle.payment.order.mono.service.OrderService;


/**
 * 订单Controller
 * @author peng
 *
 */
@RestController
@Api(description="支付订单服务")
@RequestMapping("/order/v1")
public class OrderController extends BaseController{
    private static Logger logger = LoggerFactory.getLogger(OrderController.class);
    
    @Autowired 
    private OrderService orderService ;
    
	/**
	 * 预支付(产生支付流水信息) 
	 * @param request
	 * @return
	 */
    @ApiVersion(1)
    @ApiOperation(author="彭杰",status=Status.COMPLETE,value="创建订单", produces = MediaType.APPLICATION_JSON_VALUE,notes="返回订单信息")
	@RequestMapping(value = "/create", method = {RequestMethod.POST})
    public BaseResult<HnpMainOrder> create(HnpMainOrder mainOrder){
	    logger.info("[订单服务][预支付]请求参数:"+gson.toJson(mainOrder));
	    BaseResult<HnpMainOrder> result = new BaseResult<HnpMainOrder>();
	    try {
	    	result = orderService.createOrder(mainOrder);
	    	logger.info("[订单服务][预支付]返回参数:"+gson.toJson(result));
	    	return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("预支付创建订单异常:"+e);
			return BaseResult.fail(OrderResultCode.SYS_0001);
		}
	}
	
	/**
	 * 预支付（查询订单）
	 * @param request
	 * @return
	 */
    @ApiVersion(1)
    @ApiOperation(author="彭杰",status=Status.COMPLETE,value="查询订单", produces = MediaType.APPLICATION_JSON_VALUE,notes="获取订单信息")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "mainOrderNo", value = "主订单号", required = true, paramType="query", dataType = "String")
    })
    @RequestMapping(value = "/query", method= {RequestMethod.POST})
	public BaseResult<HnpMainOrder> query(String mainOrderNo){
	    logger.info("[订单服务][查询]请求参数:mainOrderNo="+mainOrderNo);
	    BaseResult<HnpMainOrder> result  = orderService.queryMainOrder(mainOrderNo);
	    logger.info("[订单服务][查询]返回参数:"+gson.toJson(result));
    	return result ;
	}
	
	/**
     * 预支付（查询子订单列表）
     * @param request
     * @return
     */
    @ApiVersion(1)
    @ApiOperation(author="彭杰",status=Status.COMPLETE,value="查询订单列表", produces = MediaType.APPLICATION_JSON_VALUE,notes="获取订单列表信息")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "mainOrderNo", value = "主订单号", required = true, paramType="query", dataType = "String")
    })
    @RequestMapping(value = "/querylist", method= RequestMethod.POST)
    public BaseResult<List<HnpOrder>> querylist(String mainOrderNo){
        logger.info("[订单服务][查询]请求参数:mainOrderNo="+mainOrderNo);
        BaseResult<List<HnpOrder>> result = orderService.queryDetail(mainOrderNo);
        logger.info("[订单服务][查询]返回参数:"+gson.toJson(result));
    	return result ;
    }
    
    /**
     * 完结订单
     * @param request
     * @return
     */
    @ApiVersion(1)
    @ApiOperation(author="彭杰",status=Status.COMPLETE,value="完结订单", produces = MediaType.APPLICATION_JSON_VALUE,notes="返回订单信息")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "mainOrderNo", value = "主订单号", required = true, paramType="query", dataType = "String"),
    	@ApiImplicitParam(name = "payStatus", value = "订单状态(SUCCESS-成功,PROCESSING-支付中)", required = true, paramType="query", dataType = "String")
    })
    @RequestMapping(value = "/finish", method= RequestMethod.POST)
    public BaseResult<HnpMainOrder> finish(String mainOrderNo,String payStatus){
        logger.info("[订单服务][完结订单]请求参数:mainOrderNo="+mainOrderNo+",payStatus="+payStatus);
        BaseResult<HnpMainOrder> result = new BaseResult<HnpMainOrder>();
		try {
			result = orderService.finishOrder(mainOrderNo,payStatus);
			logger.info("[订单服务][完结订单]返回参数:"+gson.toJson(result));
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("完结订单异常:"+e);
			return BaseResult.fail(OrderResultCode.SYS_0001);
		}
    }
    
    /**
     * 根据子订单流水号查询订单信息
     * @param serialNumber
     * @return
     */
    @ApiVersion(1)
    @ApiOperation(author="彭杰",status=Status.COMPLETE,value="查询订单详情-流水号", produces = MediaType.APPLICATION_JSON_VALUE,notes="返回订单信息")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "serialNumber", value = "流水号", required = true, paramType="query", dataType = "String")
    })
    @RequestMapping(value = "/getDetail", method= RequestMethod.POST)
    public BaseResult<HnpOrder> getHnpDetailBySerialNumber(String serialNumber){
    	logger.info("[订单服务][根据流水号查询子订单信息]请求参数:serialNumber="+serialNumber);
    	BaseResult<HnpOrder> result = orderService.queryBySerialNumber(serialNumber);
    	logger.info("[订单服务][根据流水号查询子订单信息]返回参数:"+gson.toJson(result));
    	return result;
    }
    
    /**
     * 平台付款完成-更新订单状态
     * @param serialNumber
     * @param state
     * @return
     */
    @ApiVersion(1)
    @ApiOperation(author="彭杰",status=Status.COMPLETE,value="平台付款完成-订单详情-更新状态", produces = MediaType.APPLICATION_JSON_VALUE,notes="返回<Void>")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "serialNumber", value = "流水号", required = true, paramType="query", dataType = "String"),
    	@ApiImplicitParam(name = "state", value = "订单状态（6-付款中 3-交易成功 4-交易关闭）", required = true, paramType="query", dataType = "Integer")
    })
    @RequestMapping(value = "/updateStateDetail", method= RequestMethod.POST)
    public BaseResult<Void> updateHnpDetailBySerialNumber(String serialNumber,Integer state){
    	logger.info("[订单服务][根据流水号更新子订单状态]请求参数:serialNumber="+serialNumber+",state="+state);
    	BaseResult<Void> result = orderService.updateHnpDetailBySerialNumber(serialNumber,state);
    	logger.info("[订单服务][根据流水号更新子订单状态]返回参数:"+gson.toJson(result));
    	return result;
    }
    
    
    /**
     * 分页查询订单列表
     * @param orderQuery
     * @return
     */
    @ApiVersion(1)
    @ApiOperation(author="彭杰",status=Status.UN_COMPLETE,value="分页查询订单列表", produces = MediaType.APPLICATION_JSON_VALUE,notes="返回订单列表")
    @RequestMapping(value = "/queryPageOrderData", method= RequestMethod.POST)
    public BaseResult<PageValue<HnpOrder>> queryPageOrderData(OrderQuery orderQuery){
    	logger.info("[订单服务][分页查询订单列表]请求参数:"+gson.toJson(orderQuery));
    	BaseResult<PageValue<HnpOrder>> result = orderService.queryPageOrderData(orderQuery);
    	logger.info("[订单服务][分页查询订单列表]请求返回:"+gson.toJson(result));
    	return result ;
    }
    
    
}
