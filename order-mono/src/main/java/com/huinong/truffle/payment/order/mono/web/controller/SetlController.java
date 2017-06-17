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
import com.huinong.truffle.component.base.constants.ResultCode;
import com.huinong.truffle.payment.order.mono.domain.HnpRefund;
import com.huinong.truffle.payment.order.mono.domain.HnpSetlDetail;
import com.huinong.truffle.payment.order.mono.domain.HnpOutMoney;
import com.huinong.truffle.payment.order.mono.service.ConfirmService;
import com.huinong.truffle.payment.order.mono.service.RefundService;
import com.huinong.truffle.payment.order.mono.service.SetlService;

/**
 * 订单结算
 * @author peng
 *
 */
@RestController
@Api(description="结算订单服务")
@RequestMapping("/setl/v1")
public class SetlController extends BaseController{
	private static Logger logger = LoggerFactory.getLogger(SetlController.class);

    @Autowired
    private SetlService setlService ;
    
    @Autowired
    private ConfirmService confirmService ;
    
    @Autowired
    private RefundService refundService ;
	
	 /**
     * 订单-发起确认收货
     * @param request
     * @return
     */
    @ApiVersion(1)
    @ApiOperation(value="确认收货", produces = MediaType.APPLICATION_JSON_VALUE,notes="返回结算订单信息")
    @RequestMapping(value = "/receipt", method= RequestMethod.POST)
    public BaseResult<HnpOutMoney> confirmReceipt(HnpSetlDetail reqDTO){
        logger.info("[订单服务][确认收货]请求参数:"+gson.toJson(reqDTO));
        BaseResult<HnpOutMoney> resultDTO = new BaseResult<HnpOutMoney>();
		try {
			resultDTO = confirmService.confirmReceipt(reqDTO);
			logger.info("[订单服务][确认收货]返回参数:"+gson.toJson(resultDTO));
			return resultDTO ;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("确认收货异常："+e);
			return BaseResult.fail(ResultCode.INNER_ERROR);
		}
    }
    
    /**
     * 订单-发起确认退款
     * @param request
     * @return
     */
    @ApiVersion(1)
    @ApiOperation(value="确认退款", produces = MediaType.APPLICATION_JSON_VALUE,notes="返回退款订单信息")
    @RequestMapping(value = "/refund", method= RequestMethod.POST)
    /*@ApiImplicitParams({
    	@ApiImplicitParam(name = "reqDTO", value = "订单退款对象", required = true, paramType="body", dataType = "HnpRefund")
    })*/
    public BaseResult<List<HnpOutMoney>> confirmRefund(HnpRefund hnpRefund){
        logger.info("[订单服务][确认退款]请求参数:"+gson.toJson(hnpRefund));
        BaseResult<List<HnpOutMoney>> resultDTO;
		try {
			resultDTO = refundService.confirmRefund(hnpRefund);
			logger.info("[订单服务][确认退款]返回参数:"+gson.toJson(resultDTO));
			return resultDTO ;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("确认退款异常："+e);
			return BaseResult.fail(ResultCode.INNER_ERROR);
		}
    }
    
    /**
     * 订单-付款状态变更
     * @param request
     * @return
     */
    @ApiVersion(1)
    @ApiOperation(value="变更付款状态", produces = MediaType.APPLICATION_JSON_VALUE,notes="返回成功状态")
    @RequestMapping(value = "/statechange", method= RequestMethod.POST)
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "type", value = "结算类型(0-付款1-退款)", required = true, paramType="query", dataType = "String"),
    	@ApiImplicitParam(name = "orderId", value = "订单id", required = true, paramType="query", dataType = "Long"),
    	@ApiImplicitParam(name = "serialNumber", value = "订单流水号", required = true, paramType="query", dataType = "String"),
    	@ApiImplicitParam(name = "directStatus", value = "付款状态(0-待付款1-付款成功2-付款失败3-处理中)", required = true, paramType="query", dataType = "String"),
    	@ApiImplicitParam(name = "resCode", value = "付款码", required = false, paramType="query", dataType = "String"),
    	@ApiImplicitParam(name = "resMessage", value = "付款返回消息", required = false, paramType="query", dataType = "String")
    })
    public BaseResult<Boolean> statechange(HnpOutMoney reqDTO){
        logger.info("[订单服务][付款状态变更]请求参数:"+gson.toJson(reqDTO));
        BaseResult<Boolean> resultDTO = new BaseResult<Boolean>();
		try {
			resultDTO = setlService.stateChange(reqDTO);
			logger.info("[订单服务][付款状态变更]返回参数:"+gson.toJson(resultDTO));
			return resultDTO ;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("变更付款状态异常："+e);
			return BaseResult.fail(ResultCode.INNER_ERROR);
		}
    }
    
    /**
     * 查询付款处理中的订单
     * @param request
     * @return
     */
    @ApiVersion(1)
    @ApiOperation(value="查询付款中订单列表", produces = MediaType.APPLICATION_JSON_VALUE,notes="返回订单列表")
    @RequestMapping(value = "/listprocess", method= RequestMethod.POST)
    public BaseResult<List<HnpOutMoney>> listProcess(){
        logger.info("[订单服务][付款中订单列表]请求开始");
        BaseResult<List<HnpOutMoney>> resultDTO = new BaseResult<List<HnpOutMoney>>();
		try {
			resultDTO = setlService.listProcess();
			logger.info("[订单服务][付款中订单列表]返回参数:"+gson.toJson(resultDTO));
			return resultDTO ;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("查询付款处理中订单异常："+e);
			return BaseResult.fail(ResultCode.INNER_ERROR);
		}
    }
}
