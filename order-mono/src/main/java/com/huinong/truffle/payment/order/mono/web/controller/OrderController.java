package com.huinong.truffle.payment.order.mono.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.huinong.truffle.component.base.component.version.anno.ApiVersion;
import com.huinong.truffle.component.base.constants.BaseResult;
import com.huinong.truffle.component.base.constants.ResultCode;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants.OrderStateEnum;
import com.huinong.truffle.payment.order.mono.domain.HnpDetail;
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
     * 测试
     */
    @RequestMapping(value = { "/test" }, method = {RequestMethod.POST, RequestMethod.GET })
    public BaseResult<String> test(HttpServletRequest request){
        logger.info("测试日志------------------");
        BaseResult<String>  resultDTO = new BaseResult<String>();
        resultDTO.setData("operater sucess");
        return resultDTO ;
    }

    
	/**
	 * 预支付(产生支付流水信息) 
	 * @param request
	 * @return
	 */
    @ApiVersion(1)
    @ApiOperation(value="创建订单", produces = MediaType.APPLICATION_JSON_VALUE,notes="返回订单信息")
//	@RequestMapping(value = "/create", method = {RequestMethod.GET,RequestMethod.POST})
//   /* @ApiImplicitParams({
//    	@ApiImplicitParam(name = "appId", value = "平台标识", required = true, paramType="query", dataType = "Integer"),
//    	@ApiImplicitParam(name = "req_from", value = "平台来源", required = true, paramType="query", dataType = "String"),
//    	@ApiImplicitParam(name = "mainOrderNo", value = "主订单号", required = true, paramType="query", dataType = "String"),
//    	@ApiImplicitParam(name = "appPayerId", value = "买家ID", required = true, paramType="query", dataType = "Long"),
//    	@ApiImplicitParam(name = "hnchannel", value = "下单渠道", required = true, paramType="query", dataType = "String"),
//    	@ApiImplicitParam(name = "data", value = "订单明细json", required = true, paramType="query", dataType = "String")
//    })*/
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "reqDTO", value = "预支付订单对象", required = true, paramType="body", dataType = "HnpOrder")
    })
    public BaseResult<HnpOrder> create(HnpOrder reqDTO){
	    logger.info("[订单服务][预支付]请求参数:"+gson.toJson(reqDTO));
	    BaseResult<HnpOrder> resultDTO = new BaseResult<HnpOrder>();
		try {
			resultDTO = orderService.createOrder(reqDTO);
			logger.info("[订单服务][预支付]返回参数:"+gson.toJson(resultDTO));
			return resultDTO;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("订单服务异常："+e);
			return BaseResult.fail(ResultCode.INNER_ERROR);
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
    @RequestMapping(value = "/query", method= {RequestMethod.POST,RequestMethod.GET})
	public BaseResult<HnpOrder> query(String mainOrderNo){
	    logger.info("[订单服务][查询]请求参数:mainOrderNo="+mainOrderNo);
	    BaseResult<HnpOrder> resultDTO = new BaseResult<HnpOrder>();
	    try {
	    	resultDTO = orderService.queryOrder(mainOrderNo);
	    	logger.info("[订单服务][查询]返回参数:"+gson.toJson(resultDTO));
	    	return resultDTO ;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[订单服务][查询]异常："+e);
			return BaseResult.fail(ResultCode.INNER_ERROR);
		}
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
    public BaseResult<List<HnpDetail>> querylist(String mainOrderNo){
        logger.info("[订单服务][查询]请求参数:mainOrderNo="+mainOrderNo);
        BaseResult<List<HnpDetail>> resultDTO = new BaseResult<List<HnpDetail>>();
        try {
        	resultDTO = orderService.queryDetail(mainOrderNo);
        	logger.info("[订单服务][查询]返回参数:"+gson.toJson(resultDTO));
        	return resultDTO ;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[订单服务][查询]异常："+e);
			return BaseResult.fail(ResultCode.INNER_ERROR);
		}
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
    public BaseResult<HnpOrder> finish(String mainOrderNo){
        logger.info("[订单服务][完结订单]请求参数:mainOrderNo="+mainOrderNo);
        BaseResult<HnpOrder> resultDTO = new BaseResult<HnpOrder>();
		try {
			Integer orderState = OrderStateEnum.ORDER_2.val ;
			resultDTO = orderService.finishOrder(mainOrderNo,orderState);
			logger.info("[订单服务][完结订单]返回参数:"+gson.toJson(resultDTO));
			return resultDTO;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("订单服务异常："+e);
			return BaseResult.fail(ResultCode.INNER_ERROR);
		}
    }
    
    
    /**
     * 修改订单详情
     * @param request
     * @return
    @RequestMapping(value = "/updateOrderItem", method= RequestMethod.POST)
    public String updateOrderItem(HttpServletRequest request){
        ParamHandler paramHandler = new ParamHandler(request);
        logger.info("[订单服务][修改订单详情]请求参数:"+gson.toJson(paramHandler.getMap()));
        HnpDetailDTO hnpDetailDTO = gson.fromJson(paramHandler.getString("dto"), HnpDetailDTO.class);
        BizResponse<Integer> resultDTO = new BizResponse<Integer>();
		try {
			resultDTO = orderService.updateOrderItem(hnpDetailDTO);
		} catch (Exception e) {
			e.printStackTrace();
			resultDTO.setMessage("系统异常，请稍后再试");
		}
        logger.info("[订单服务][修改订单详情]返回参数:"+gson.toJson(resultDTO));
        return gson.toJson(resultDTO);
    }*/
}
