package com.huinong.truffle.payment.order.mono.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

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
import com.huinong.truffle.payment.order.mono.constant.OrderConstants.OrderStateEnum;
import com.huinong.truffle.payment.order.mono.constant.OrderResultCode;
import com.huinong.truffle.payment.order.mono.domain.HnpMainOrder;
import com.huinong.truffle.payment.order.mono.domain.HnpOrder;
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
    @ApiOperation(value="创建订单", produces = MediaType.APPLICATION_JSON_VALUE,notes="返回订单信息")
	@RequestMapping(value = "/create", method = {RequestMethod.POST})
    public BaseResult<HnpMainOrder> create(HnpMainOrder mainOrder){
	    logger.info("[订单服务][预支付]请求参数:"+gson.toJson(mainOrder));
	    BaseResult<HnpMainOrder> resultDTO = new BaseResult<HnpMainOrder>();
	    try {
	    	resultDTO = orderService.createOrder(mainOrder);
	    	logger.info("[订单服务][预支付]返回参数:"+gson.toJson(resultDTO));
	    	return resultDTO;
		} catch (Exception e) {
			e.printStackTrace();
			return BaseResult.fail(OrderResultCode.SYS_0001);
		}
	}
	
	/**
	 * 预支付（查询订单）
	 * @param request
	 * @return
	 */
    @ApiVersion(1)
    @ApiOperation(value="查询订单", produces = MediaType.APPLICATION_JSON_VALUE,notes="获取订单信息")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "mainOrderNo", value = "主订单号", required = true, paramType="query", dataType = "String")
    })
    @RequestMapping(value = "/query", method= {RequestMethod.POST})
	public BaseResult<HnpMainOrder> query(String mainOrderNo){
	    logger.info("[订单服务][查询]请求参数:mainOrderNo="+mainOrderNo);
	    BaseResult<HnpMainOrder> resultDTO  = orderService.queryMainOrder(mainOrderNo);
	    logger.info("[订单服务][查询]返回参数:"+gson.toJson(resultDTO));
    	return resultDTO ;
	}
	
	/**
     * 预支付（查询子订单列表）
     * @param request
     * @return
     */
    @ApiVersion(1)
    @ApiOperation(value="查询订单列表", produces = MediaType.APPLICATION_JSON_VALUE,notes="获取订单列表信息")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "mainOrderNo", value = "主订单号", required = true, paramType="query", dataType = "String")
    })
    @RequestMapping(value = "/querylist", method= RequestMethod.POST)
    public BaseResult<List<HnpOrder>> querylist(String mainOrderNo){
        logger.info("[订单服务][查询]请求参数:mainOrderNo="+mainOrderNo);
        BaseResult<List<HnpOrder>> resultDTO = orderService.queryDetail(mainOrderNo);
        logger.info("[订单服务][查询]返回参数:"+gson.toJson(resultDTO));
    	return resultDTO ;
    }
    
    /**
     * 完结订单
     * @param request
     * @return
     */
    @ApiVersion(1)
    @ApiOperation(value="完结订单", produces = MediaType.APPLICATION_JSON_VALUE,notes="返回订单信息")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "mainOrderNo", value = "主订单号", required = true, paramType="query", dataType = "String")
    })
    @RequestMapping(value = "/finish", method= RequestMethod.POST)
    public BaseResult<HnpMainOrder> finish(String mainOrderNo){
        logger.info("[订单服务][完结订单]请求参数:mainOrderNo="+mainOrderNo);
        Integer orderState = OrderStateEnum.ORDER_2.val ;
        BaseResult<HnpMainOrder> resultDTO = new BaseResult<HnpMainOrder>();
		try {
			resultDTO = orderService.finishOrder(mainOrderNo,orderState);
			logger.info("[订单服务][完结订单]返回参数:"+gson.toJson(resultDTO));
			return resultDTO;
		} catch (Exception e) {
			e.printStackTrace();
			return BaseResult.fail(OrderResultCode.SYS_0001);
		}
    }
    
    /**
     * 根据子订单流水号查询订单信息
     * @param serialNumber
     * @return
     */
    @ApiVersion(1)
    @ApiOperation(value="查询订单详情-流水号", produces = MediaType.APPLICATION_JSON_VALUE,notes="返回订单信息")
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
}
