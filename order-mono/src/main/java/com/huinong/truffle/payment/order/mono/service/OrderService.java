package com.huinong.truffle.payment.order.mono.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huinong.truffle.component.base.constants.BaseResult;
import com.huinong.truffle.component.base.constants.ResultCode;
import com.huinong.truffle.payment.order.mono.component.redis.RedisLock;
import com.huinong.truffle.payment.order.mono.component.redis.client.DefRedisClient;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants.DeleteState;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants.OrderStateEnum;
import com.huinong.truffle.payment.order.mono.constant.OrderResultCode;
import com.huinong.truffle.payment.order.mono.dao.read.MainOrderReadDAO;
import com.huinong.truffle.payment.order.mono.dao.read.OrderReadDAO;
import com.huinong.truffle.payment.order.mono.dao.write.MainOrderWriteDAO;
import com.huinong.truffle.payment.order.mono.dao.write.OrderWriteDAO;
import com.huinong.truffle.payment.order.mono.domain.HnpMainOrder;
import com.huinong.truffle.payment.order.mono.domain.HnpOrder;
import com.huinong.truffle.payment.order.mono.entity.HnpMainOrderEntity;
import com.huinong.truffle.payment.order.mono.entity.HnpOrderEntity;
import com.huinong.truffle.payment.order.mono.util.CopyBeanUtil;

@Service("orderService")
public class OrderService {

	private static Logger logger = LoggerFactory.getLogger(OrderService.class);
	
	public Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

	@Autowired
	private MainOrderWriteDAO mainOrderWriteDAO ;
	@Autowired
	private MainOrderReadDAO mainOrderReadDAO ;
	@Autowired
	private OrderReadDAO orderReadDAO ;
	@Autowired
	private OrderWriteDAO orderWriteDAO ;
	@Autowired
	private DefRedisClient defRedisClient;

	@Transactional
	public BaseResult<HnpMainOrder> createOrder(HnpMainOrder mainOrder) {
		if (null == mainOrder) {
			return BaseResult.fail(OrderResultCode.PARAM_0004);
		}
		if(null == mainOrder.getData()){
			return BaseResult.fail(OrderResultCode.PARAM_0002);
		}

		RedisLock lock = null;
		try {
			// 0 对每一笔预支付订单进行锁处理，防止重复提交
			String mainOrderNo = mainOrder.getMainOrderNo();/* (String) orderMap.get(ReqParamEnum.REQ_PARAM_ORDER_NO.val);*/
			lock = new RedisLock(defRedisClient,OrderConstants.RedisKey.ORDER_REPAY_KEY.value.concat(mainOrderNo));
			if (!lock.lock()) {
				logger.info("订单：" + mainOrderNo + "预支付超时...");
				return BaseResult.fail(OrderResultCode.DB_0020);
			}
			// 1 检测
			HnpMainOrderEntity mainOrderEntity = mainOrderReadDAO.selectByMainOrderNo(mainOrderNo);
			/*HnpOrderEntity qOrderDTO = mainOrderReadDAO.selectByMainOrderNo(mainOrderNo);*/
			if (null != mainOrderEntity) {
				// 判断订单状态
				if (!mainOrderEntity.isWaitingConfirm()) {
					logger.info("订单：" + mainOrderNo + "已支付，请勿重新支付");
					return BaseResult.fail(OrderResultCode.DB_0002);
				}
				// 重新预支付订单在订单存在情况下需要确认订单数据是否一致
				/*HnpOrderEntity hnpOrderDTO = RespResultParser.parse2Obj(orderMap.toString(), new TypeToken<HnpOrderEntity>() {});*/
				// 校验参数
				BaseResult<Void> checkMsgResp = checkReqMsg(mainOrder);
				if(checkMsgResp.getCode() != ResultCode.SUCCESS.getCode()){
					return new BaseResult<HnpMainOrder>(checkMsgResp.getCode(),checkMsgResp.getMsg());
				}
				
				// 删除以前的主订单和明细数据 加入新的订单信息
				mainOrderWriteDAO.delMainOrder(mainOrderEntity.getId());
				orderWriteDAO.deleteByMainOrderNo(mainOrderNo);
				// 新增订单信息
				BaseResult<HnpMainOrder> mainOrderResult = addOrderRecords(mainOrder);
				if(null == mainOrderResult || mainOrderResult.getCode() != ResultCode.SUCCESS.getCode()){
					return new BaseResult<HnpMainOrder>(mainOrderResult.getCode(),mainOrderResult.getMsg());
				}
				return BaseResult.success(mainOrderResult.getData());
			} else {// 新增
				//HnpOrderEntity hnpOrderDTO = RespResultParser.parse2Obj(orderMap.toString(), new TypeToken<HnpOrderEntity>() {});
				BaseResult<Void> checkMsgResp = checkReqMsg(mainOrder);
				if(checkMsgResp.getCode() != ResultCode.SUCCESS.getCode()){
					return new BaseResult<HnpMainOrder>(checkMsgResp.getCode(),checkMsgResp.getMsg());
				}
				// 新增订单信息
				BaseResult<HnpMainOrder> mainOrderResult = addOrderRecords(mainOrder);
				if(null == mainOrderResult || mainOrderResult.getCode() != ResultCode.SUCCESS.getCode()){
					return new BaseResult<HnpMainOrder>(mainOrderResult.getCode(),mainOrderResult.getMsg());
				}
				return BaseResult.success(mainOrderResult.getData());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("创建订单异常："+e);
			return BaseResult.fail(OrderResultCode.SYS_0001);
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
	private BaseResult<HnpMainOrder> addOrderRecords(HnpMainOrder mainOrder) throws Exception {
		/*HnpOrderEntity hnpOrderDTO = new HnpOrderEntity() ;
		CopyBeanUtil.getInstance().copyBeanProperties(mainOrder, hnpOrderDTO);*/
		// 3、验证子订单金额之和 是否等于 订单总金额
		boolean isEqual = compareTwoAmtIsEqual(mainOrder.getData(),mainOrder.getTotalAmt().doubleValue());
		if (!isEqual) {
			logger.info("子订单金额之和不等于主订单总金额...");
			return BaseResult.fail(OrderResultCode.DB_0019);
		}
		HnpMainOrderEntity record = new HnpMainOrderEntity();
		record.setCreateTime(new Date());
		record.setHnchannel(mainOrder.getHnchannel());
		record.setMainOrderNo(mainOrder.getMainOrderNo());
		record.setModifyTime(new Date());
		record.setOrderState(mainOrder.getOrderState());
		record.setOutUid(mainOrder.getOutUid());
		record.setSourceSys(mainOrder.getSourceSys());
		record.setTotalAmt(mainOrder.getTotalAmt());
		int i = mainOrderWriteDAO.insert(record);
		if(i <= 0){
			logger.info("添加订单信息失败"); 
			return BaseResult.fail(OrderResultCode.DB_0014);
		}
		addBatchItem(mainOrder.getData(),mainOrder.getSourceSys());
		return BaseResult.success(mainOrder);
		/*Long order_id = mainOrderWriteDAO.addMainOrder(hnpOrderDTO) ;
		Long item_id = addBatchItem(hnpOrderDTO.getData(),hnpOrderDTO.getReq_from());
		if (order_id > 0 && item_id > 0) {
			return BaseResult.success(hnpOrderDTO);
		} else {
			logger.info("添加订单信息失败"); 
			return BaseResult.fail(OrderResultCode.DB_0014);
		}*/
	}
	
	/**
	 * 批量添加订单明细
	 */
	private int addBatchItem(List<HnpOrder> orderItem,String orderFromSystem) throws Exception{
        if(null == orderItem || orderItem.size() == 0) {
            logger.info("批量插的入参对象为空...");
            throw new Exception("批量插的入参对象为空...");
        }
        String serialNumber = "" ;
        HnpOrderEntity orderEntity = null ;
        List<HnpOrderEntity> list = new ArrayList<HnpOrderEntity>();
        for(int i=0 ; i<orderItem.size();i++){
        	HnpOrder order = orderItem.get(i);
        	
        	serialNumber = order.getGroupOrderNo() + (++i);
        	
        	orderEntity = new HnpOrderEntity();
        	orderEntity.setAppId(order.getAppId());
        	orderEntity.setAmt(order.getAmt());
        	orderEntity.setGroupOrderNo(order.getGroupOrderNo());
        	orderEntity.setInUid(order.getInUid());
        	orderEntity.setInUname(order.getInUname());
        	orderEntity.setMerchantOffAmt(new BigDecimal("0.0"));
        	orderEntity.setPayOffAmt(new BigDecimal("0.0"));
        	orderEntity.setSysOffAmt(new BigDecimal("0.0"));
        	orderEntity.setNeedPay(order.getAmt());
        	orderEntity.setOrderId(order.getOrderId());
        	orderEntity.setOrderSummary(order.getOrderSummary());
        	orderEntity.setOrderTimestamp(new Date());
        	orderEntity.setOutUid(order.getOutUid());
        	orderEntity.setPayState(String.valueOf(OrderStateEnum.ORDER_0.val));
        	orderEntity.setShopName(order.getShopName());
        	orderEntity.setOrderFromSystem(orderFromSystem);
        	orderEntity.setSerialNumber(serialNumber);
        	orderEntity.setHash("abc");
        	orderEntity.setDeleted(DeleteState.DELETE_TYPE_T.val);
        	list.add(orderEntity);
        }
        int i = orderWriteDAO.batchInsertOrder(list);
        return i ;
    }

	/**
	 * 校验参数
	 */
	private BaseResult<Void> checkReqMsg(HnpMainOrder mainOrder) {
		/*if (mainOrder.getAppId() == null){
			logger.info("订单确认失败,平台标识为空");
			return BaseResult.fail(OrderResultCode.PARAM_0026);
		}*/
		if (StringUtils.isBlank(mainOrder.getSourceSys())){
			logger.info("订单确认失败,平台来源为空");
			return BaseResult.fail(OrderResultCode.PARAM_0027);
		}

		if (StringUtils.isBlank(mainOrder.getMainOrderNo())){
			logger.info("订单确认失败,主订单号为空");
			return BaseResult.fail(OrderResultCode.PARAM_0006);
		}

		if (null == mainOrder.getTotalAmt()){
			logger.info("订单确认失败,订单金额为空");
			return BaseResult.fail(OrderResultCode.PARAM_0008);
		}

		if (null == mainOrder.getOutUid()){
			logger.info("订单确认失败,买家账号为空");
			return BaseResult.fail(OrderResultCode.PARAM_0028);
		}

		if (StringUtils.isBlank(mainOrder.getHnchannel())){
			logger.info("订单确认失败,下单渠道为空");
			return BaseResult.fail(OrderResultCode.PARAM_0029);
		}

		if (null == mainOrder.getData() || mainOrder.getData().size() == 0){
			logger.info("订单确认失败,订单明细为空");
			return BaseResult.fail(OrderResultCode.PARAM_0029);
		}
		return BaseResult.success();
	}

	public BaseResult<HnpMainOrder> queryMainOrder(String mainOrderNo) {
		if(StringUtils.isBlank(mainOrderNo)){
			logger.info("无法识别{主订单编号}");
			return BaseResult.fail(OrderResultCode.PARAM_0006);
		}
		try {
			HnpMainOrderEntity mainOrderEntity = mainOrderReadDAO.selectByMainOrderNo(mainOrderNo);
			if (null == mainOrderEntity) {
				logger.info("参数异常：主订单信息为空");
				return BaseResult.fail(OrderResultCode.PARAM_0004);
			}
			HnpMainOrder returnDTO = new HnpMainOrder();
			CopyBeanUtil.getInstance().copyBeanProperties(mainOrderEntity,returnDTO);
			return BaseResult.success(returnDTO);
		} catch (Exception e) {
			e.printStackTrace();
			return BaseResult.fail(OrderResultCode.DB_0021);
		}
	}

	/**
	 * 比较子订单金额之和与主订单金额
	 */
	private boolean compareTwoAmtIsEqual(List<HnpOrder> item,
			Double totalAmount) {
		Double itemAmt = 0.0d;
		for (HnpOrder dto : item) {
			itemAmt = itemAmt + dto.getAmt().doubleValue();
		}
		if (null == totalAmount) {
			totalAmount = 0.0d;
		}
		return (itemAmt.doubleValue() - totalAmount.doubleValue() == 0) ? true: false;
	}

	
	public BaseResult<List<HnpOrder>> queryDetail(String mainOrderNo) {
		// 0 校验参数
		if (StringUtils.isBlank(mainOrderNo)) {
			logger.info("查询订单参数{主订单号}为空");
			return BaseResult.fail(OrderResultCode.PARAM_0004);
		}
		List<HnpOrderEntity> orderlist = orderReadDAO.listByMainOrderNo(mainOrderNo);
		if (null == orderlist || orderlist.size() == 0) {
			return BaseResult.success(new ArrayList<HnpOrder>());
		}
		List<HnpOrder> returnList = new ArrayList<HnpOrder>();
		try {
			HnpOrder returnbean = null ;
			for(HnpOrderEntity entity : orderlist){
				returnbean = new HnpOrder();
				CopyBeanUtil.getInstance().copyBeanProperties(entity, returnbean);
				returnList.add(returnbean);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return  BaseResult.fail(OrderResultCode.DB_0021);
		}
		return BaseResult.success(returnList);
	}

	
	public BaseResult<HnpMainOrder> updateOrder(String mainOrderNo,Integer orderStatus) throws Exception {
		if (StringUtils.isBlank(mainOrderNo)) {
			logger.info("参数{订单号}为空");
			return BaseResult.fail(OrderResultCode.PARAM_0004);
		}
		if (null == orderStatus) {
			logger.info("参数{订单状态}为空");
			return BaseResult.fail(OrderResultCode.PARAM_0004);
		}
		HnpMainOrderEntity mainOrderEntity = mainOrderReadDAO.selectByMainOrderNo(mainOrderNo);
		/*HnpOrderEntity hnpOrderDTO = mainOrderReadDAO.getDTOByUniqueValue(mainOrderNo);*/
		if (null == mainOrderEntity) {
			logger.info("查询订单不存在");
			return BaseResult.fail(OrderResultCode.DB_0005);
		}
		HnpMainOrderEntity record = new HnpMainOrderEntity();
		record.setMainOrderNo(mainOrderNo);
		record.setOrderState(orderStatus);;
		int i = mainOrderWriteDAO.updateByMainOrderNo(record);
		if(i ==0){
			return BaseResult.fail(OrderResultCode.DB_0006);
		}
		
		HnpMainOrder mainOrder = new HnpMainOrder();
		CopyBeanUtil.getInstance().copyBeanProperties(mainOrderEntity, mainOrder);
		return BaseResult.success(mainOrder);
	}

	@Transactional
	public BaseResult<HnpMainOrder> finishOrder(String mainOrderNo,Integer orderStatus) {
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
			
			HnpMainOrderEntity mainRecord = new HnpMainOrderEntity();
			mainRecord.setMainOrderNo(mainOrderNo);
			mainRecord.setOrderState(orderStatus);
			int i = mainOrderWriteDAO.updateByMainOrderNo(mainRecord);
			
			HnpOrderEntity orderRecord = new HnpOrderEntity();
			orderRecord.setGroupOrderNo(mainOrderNo);
			orderRecord.setPayState(String.valueOf(orderStatus));
			int j = orderWriteDAO.updateByMainOrderNoSelective(orderRecord);
			if (i > 0 && j > 0) {
				HnpMainOrderEntity mainOrderEntity = mainOrderReadDAO.selectByMainOrderNo(mainOrderNo);
				
				HnpMainOrder mainOrder = new HnpMainOrder();
				CopyBeanUtil.getInstance().copyBeanProperties(mainOrderEntity, mainOrder);
				return BaseResult.success(mainOrder);
			} else {
				logger.info("更新订单状态失败");
				return BaseResult.fail(OrderResultCode.DB_0006);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return BaseResult.fail(OrderResultCode.DB_0022);
		} finally {
			if (lock != null) {
				lock.unlock();
			}
		}
	}

	/*public BaseResult<Integer> updateOrderItem(HnpDetailEntity hnpDetailDTO)throws Exception {
		int i = orderItemWriteDAO.updateOrderStatus(hnpDetailDTO);
		if(i == 0) return BaseResult.fail(OrderResultCode.DB_0006);
		return BaseResult.success(i);
	}*/

}
