package com.huinong.truffle.payment.order.mono.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.huinong.payment.idcenter.SerialGenFactory;
import com.huinong.truffle.component.base.constants.BaseResult;
import com.huinong.truffle.component.base.constants.ResultCode;
import com.huinong.truffle.payment.order.mono.component.redis.RedisLock;
import com.huinong.truffle.payment.order.mono.component.redis.client.DefRedisClient;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants.DeleteState;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants.OrderStateEnum;
import com.huinong.truffle.payment.order.mono.constant.OrderResultCode;
import com.huinong.truffle.payment.order.mono.dao.read.MainOrderReadDAO;
import com.huinong.truffle.payment.order.mono.dao.read.OrderItemReadDAO;
import com.huinong.truffle.payment.order.mono.dao.write.MainOrderWriteDAO;
import com.huinong.truffle.payment.order.mono.dao.write.OrderItemWriteDAO;
import com.huinong.truffle.payment.order.mono.domain.HnpDetail;
import com.huinong.truffle.payment.order.mono.domain.HnpOrder;
import com.huinong.truffle.payment.order.mono.entity.HnpDetailEntity;
import com.huinong.truffle.payment.order.mono.entity.HnpOrderEntity;
import com.huinong.truffle.payment.order.mono.util.CopyBeanUtil;
import com.huinong.truffle.payment.order.mono.util.RespResultParser;

@Service("orderService")
public class OrderService {

	private static Logger logger = LoggerFactory.getLogger(OrderService.class);
	
	public Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

	@Autowired
	private MainOrderWriteDAO mainOrderWriteDAO ;
	
	@Autowired
	private MainOrderReadDAO mainOrderReadDAO ;
	
	@Autowired
	private OrderItemWriteDAO orderItemWriteDAO;
	
	@Autowired
	private OrderItemReadDAO orderItemReadDAO;
	
	@Autowired
	private DefRedisClient defRedisClient;

	@Transactional
	public BaseResult<HnpOrder> createOrder(HnpOrder reqDTO) throws Exception {
		if (null == reqDTO) {
			return BaseResult.fail(OrderResultCode.PARAM_0004);
		}
		if(null == reqDTO.getData()){
			return BaseResult.fail(OrderResultCode.PARAM_0002);
		}
		/*if (!orderMap.containsKey(OrderConstants.ReqParamEnum.REQ_PARAM_ORDER_ITEM.val)) {
			return BaseResult.fail(OrderResultCode.PARAM_0002);
		}*/

		RedisLock lock = null;
		try {
			// 0 对每一笔预支付订单进行锁处理，防止重复提交
			String mainOrderNo = reqDTO.getMainOrderNo();/* (String) orderMap.get(ReqParamEnum.REQ_PARAM_ORDER_NO.val);*/
			lock = new RedisLock(defRedisClient,OrderConstants.RedisKey.ORDER_REPAY_KEY.value.concat(mainOrderNo));
			if (!lock.lock()) {
				logger.info("订单：" + mainOrderNo + "预支付超时...");
				return BaseResult.fail(OrderResultCode.DB_0020);
			}
			// 1 检测
			HnpOrderEntity qOrderDTO = mainOrderReadDAO.getDTOByUniqueValue(mainOrderNo);
			if (null != qOrderDTO) {
				// 判断订单状态
				if (!qOrderDTO.isWaitingConfirm()) {
					logger.info("订单：" + mainOrderNo + "已支付，请勿重新支付");
					return BaseResult.fail(OrderResultCode.DB_0002);
				}
				// 重新预支付订单在订单存在情况下需要确认订单数据是否一致
				/*HnpOrderEntity hnpOrderDTO = RespResultParser.parse2Obj(orderMap.toString(), new TypeToken<HnpOrderEntity>() {});*/
				// 校验参数
				BaseResult<Void> checkMsgResp = checkReqMsg(reqDTO);
				if(checkMsgResp.getCode() != ResultCode.SUCCESS.getCode()){
					return new BaseResult<HnpOrder>(checkMsgResp.getCode(),checkMsgResp.getMsg());
				}
				
				if (qOrderDTO.getObjectUUID().equals(reqDTO.getObjectUUID())) {
					HnpOrder returnDTO = new HnpOrder(); 
					CopyBeanUtil.getInstance().copyBeanProperties(qOrderDTO, returnDTO);
					return BaseResult.success(returnDTO);
				} else {
					// 删除以前的主订单和明细数据 加入新的订单信息
					int i = mainOrderWriteDAO.delMainOrder(qOrderDTO.getId());
					int m = orderItemWriteDAO.delete(qOrderDTO.getMainOrderNo());
					if (i > 0 && m > 0) {
						// 新增订单信息
						BaseResult<HnpOrderEntity> resultDTO = addOrderRecords(reqDTO);
						if(null == resultDTO || resultDTO.getCode() != ResultCode.SUCCESS.getCode()){
							return new BaseResult<HnpOrder>(resultDTO.getCode(),resultDTO.getMsg());
						}
						
						HnpOrder returnDTO = new HnpOrder(); 
						CopyBeanUtil.getInstance().copyBeanProperties(resultDTO.getData(), returnDTO);
						return BaseResult.success(returnDTO);
					} else {
						return BaseResult.fail(OrderResultCode.DB_0004);
					}
				}
			} else {// 新增
				//HnpOrderEntity hnpOrderDTO = RespResultParser.parse2Obj(orderMap.toString(), new TypeToken<HnpOrderEntity>() {});
				BaseResult<Void> checkMsgResp = checkReqMsg(reqDTO);
				if(checkMsgResp.getCode() != ResultCode.SUCCESS.getCode()){
					return new BaseResult<HnpOrder>(checkMsgResp.getCode(),checkMsgResp.getMsg());
				}
				// 新增订单信息
				BaseResult<HnpOrderEntity> resultDTO = addOrderRecords(reqDTO);
				if(null == resultDTO || resultDTO.getCode() != ResultCode.SUCCESS.getCode()){
					return new BaseResult<HnpOrder>(resultDTO.getCode(),resultDTO.getMsg());
				}
				HnpOrder returnDTO = new HnpOrder(); 
				CopyBeanUtil.getInstance().copyBeanProperties(resultDTO.getData(), returnDTO);
				return BaseResult.success(returnDTO);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (lock != null) {
				lock.unlock();
			}
		}
	}

	/**
	 * 新增订单信息记录
	 * 
	 * @param hnpOrderDTO
	 * @return
	 * @throws Exception
	 */
	private BaseResult<HnpOrderEntity> addOrderRecords(HnpOrder hnpOrder) throws Exception {
		HnpOrderEntity hnpOrderDTO = new HnpOrderEntity() ;
		CopyBeanUtil.getInstance().copyBeanProperties(hnpOrder, hnpOrderDTO);
		// 3、验证子订单金额之和 是否等于 订单总金额
		boolean isEqual = compareTwoAmtIsEqual(hnpOrderDTO.getData(),hnpOrderDTO.getTotalAmount());
		if (!isEqual) {
			logger.info("子订单金额之和不等于主订单总金额...");
			return BaseResult.fail(OrderResultCode.DB_0019);
		}
		Long order_id =mainOrderWriteDAO.addMainOrder(hnpOrderDTO) ;
		Long item_id = addBatchItem(hnpOrderDTO.getData(),hnpOrderDTO.getReq_from());
		if (order_id > 0 && item_id > 0) {
			return BaseResult.success(hnpOrderDTO);
		} else {
			logger.info("添加订单信息失败"); 
			return BaseResult.fail(OrderResultCode.DB_0014);
		}
	}
	
	/**
	 * 批量添加订单明细
	 */
	private Long addBatchItem(List<HnpDetailEntity> orderItem,String orderFromSystem) throws Exception{
        if(null == orderItem || orderItem.size() == 0) {
            logger.info("批量插的入参对象为空...");
            throw new Exception("批量插的入参对象为空...");
        }
        String serialNumber = "" ;
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        for(HnpDetailEntity itemDTO : orderItem){
        	serialNumber = SerialGenFactory.getInstance().genOrderSerialNo();
        	 Map<String, Object> itemMap = new HashMap<String,Object>();
             itemMap.put("amt", itemDTO.getAmt());
             itemMap.put("appId", itemDTO.getAppId());
             itemMap.put("deleted", DeleteState.DELETE_TYPE_T.val);
             itemMap.put("groupOrderNo", itemDTO.getMainOrderNo());
             itemMap.put("inUid", itemDTO.getAppPayeeId());
             itemMap.put("inUname", itemDTO.getPayeeUserName());
             itemMap.put("merchantOffAmt", 0.0d);
             itemMap.put("payOffAmt", 0.0d);
             itemMap.put("sysOffAmt", 0.0d);
             itemMap.put("needPay", itemDTO.getAmt());
             itemMap.put("orderId", itemDTO.getOrderNo());
             itemMap.put("orderSummary", itemDTO.getOrderDesc());
             itemMap.put("orderTimestamp", new Date());
             itemMap.put("outUid", itemDTO.getAppPayerId());
             itemMap.put("payState", String.valueOf(OrderStateEnum.ORDER_0.val));
             itemMap.put("shopName", itemDTO.getShopName());
             itemMap.put("orderFromSystem", orderFromSystem);
             itemMap.put("serialNumber", serialNumber);
             itemMap.put("hash", "abc");
             list.add(itemMap);
        }
        Long id = orderItemWriteDAO.addBatchItem(list);
        return id ;
    }

	/**
	 * 校验参数
	 */
	private BaseResult<Void> checkReqMsg(HnpOrder orderDTO) {
		if (orderDTO.getAppId() == null){
			logger.info("订单确认失败,平台标识为空");
			return BaseResult.fail(OrderResultCode.PARAM_0026);
		}
		if (StringUtils.isBlank(orderDTO.getReq_from())){
			logger.info("订单确认失败,平台来源为空");
			return BaseResult.fail(OrderResultCode.PARAM_0027);
		}

		if (StringUtils.isBlank(orderDTO.getMainOrderNo())){
			logger.info("订单确认失败,主订单号为空");
			return BaseResult.fail(OrderResultCode.PARAM_0006);
		}

		if (null == orderDTO.getTotalAmount()){
			logger.info("订单确认失败,订单金额为空");
			return BaseResult.fail(OrderResultCode.PARAM_0008);
		}

		if (null == orderDTO.getAppPayerId()){
			logger.info("订单确认失败,买家账号为空");
			return BaseResult.fail(OrderResultCode.PARAM_0028);
		}

		if (StringUtils.isBlank(orderDTO.getHnchannel())){
			logger.info("订单确认失败,下单渠道为空");
			return BaseResult.fail(OrderResultCode.PARAM_0029);
		}

		if (null == orderDTO.getData() || orderDTO.getData().size() == 0){
			logger.info("订单确认失败,订单明细为空");
			return BaseResult.fail(OrderResultCode.PARAM_0029);
		}
		return BaseResult.success();
	}

	public BaseResult<HnpOrder> queryOrder(String mainOrderNo) throws Exception {
		if(StringUtils.isBlank(mainOrderNo)){
			logger.info("无法识别{主订单编号}");
			return BaseResult.fail(OrderResultCode.PARAM_0006);
		}
		HnpOrderEntity mainOrderDTO = mainOrderReadDAO.getDTOByUniqueValue(mainOrderNo);
		if (null == mainOrderDTO) {
			logger.info("参数异常：主订单信息为空");
			return BaseResult.fail(OrderResultCode.PARAM_0004);
		}
		HnpOrder returnDTO = new HnpOrder(); 
		CopyBeanUtil.getInstance().copyBeanProperties(mainOrderDTO, returnDTO);
		return BaseResult.success(returnDTO);
	}

	/**
	 * 比较子订单金额之和与主订单金额
	 */
	private boolean compareTwoAmtIsEqual(List<HnpDetailEntity> item,
			Double totalAmount) {
		Double itemAmt = 0.0d;
		for (HnpDetailEntity dto : item) {
			itemAmt = itemAmt + dto.getAmt();
		}
		if (null == totalAmount) {
			totalAmount = 0.0d;
		}
		return (itemAmt.doubleValue() - totalAmount.doubleValue() == 0) ? true: false;
	}

	
	public BaseResult<List<HnpDetail>> queryDetail(String mainOrderNo) throws Exception {
		// 0 校验参数
		if (StringUtils.isBlank(mainOrderNo)) {
			logger.info("查询订单参数{主订单号}为空");
			return BaseResult.fail(OrderResultCode.PARAM_0004);
		}
		List<HnpDetailEntity> orderList = orderItemReadDAO.getOrderItemList(mainOrderNo);
		if (null == orderList || orderList.size() == 0) {
			return BaseResult.success(new ArrayList<HnpDetail>());
		}
		List<HnpDetail> returnList = new ArrayList<HnpDetail>();
		HnpDetail dto = null ;
		for(HnpDetailEntity entity : orderList){
			dto = new HnpDetail();
			CopyBeanUtil.getInstance().copyBeanProperties(entity, dto);
			returnList.add(dto);
		}
		return BaseResult.success(returnList);
	}

	
	public BaseResult<HnpOrderEntity> updateOrder(String mainOrderNo,Integer orderStatus) throws Exception {
		if (StringUtils.isBlank(mainOrderNo)) {
			logger.info("参数{订单号}为空");
			return BaseResult.fail(OrderResultCode.PARAM_0004);
		}
		if (null == orderStatus) {
			logger.info("参数{订单状态}为空");
			return BaseResult.fail(OrderResultCode.PARAM_0004);
		}
		HnpOrderEntity hnpOrderDTO = mainOrderReadDAO.getDTOByUniqueValue(mainOrderNo);
		if (null == hnpOrderDTO) {
			logger.info("查询订单不存在");
			return BaseResult.fail(OrderResultCode.DB_0005);
		}
		int i = mainOrderWriteDAO.update(mainOrderNo, orderStatus);
		if(i ==0){
			return BaseResult.fail(OrderResultCode.DB_0006);
		}
		return BaseResult.success(hnpOrderDTO);
	}

	@Transactional
	public BaseResult<HnpOrder> finishOrder(String mainOrderNo,Integer orderStatus) throws Exception {
		if (StringUtils.isBlank(mainOrderNo)) {
			logger.info("参数{订单号}为空");
			return BaseResult.fail(OrderResultCode.PARAM_0004);
		}
		if (null == orderStatus) {
			logger.info("参数{订单状态}为空");
			return BaseResult.fail(OrderResultCode.PARAM_0004);
		}

		RedisLock lock = null;
		try {
			// 0 对每一笔预支付订单进行锁处理，防止重复提交
			lock = new RedisLock(defRedisClient,
					OrderConstants.RedisKey.ORDER_REPAY_KEY.value.concat(mainOrderNo));
			if (!lock.lock()) {
				logger.info("订单：" + mainOrderNo + "更新状态超时");		
				return BaseResult.fail(OrderResultCode.DB_0020);
			}
			int i = mainOrderWriteDAO.update(mainOrderNo, orderStatus);
			int j = orderItemWriteDAO.update(mainOrderNo, orderStatus);
			if (i > 0 && j > 0) {
				HnpOrderEntity hnpOrderDTO = mainOrderReadDAO.getDTOByUniqueValue(mainOrderNo);
				HnpOrder dto = new HnpOrder();
				CopyBeanUtil.getInstance().copyBeanProperties(hnpOrderDTO, dto);
				return BaseResult.success(dto);
			} else {
				logger.info("更新订单状态失败");
				return BaseResult.fail(OrderResultCode.DB_0006);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (lock != null) {
				lock.unlock();
			}
		}
	}

	public BaseResult<Integer> updateOrderItem(HnpDetailEntity hnpDetailDTO)throws Exception {
		int i = orderItemWriteDAO.updateOrderStatus(hnpDetailDTO);
		if(i == 0) return BaseResult.fail(OrderResultCode.DB_0006);
		return BaseResult.success(i);
	}

}
