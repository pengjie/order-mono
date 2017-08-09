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

import com.github.pagehelper.PageHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huinong.truffle.component.base.constants.BaseResult;
import com.huinong.truffle.component.base.constants.PageValue;
import com.huinong.truffle.component.base.constants.ResultCode;
import com.huinong.truffle.component.base.util.object.ObjectUtils;
import com.huinong.truffle.payment.order.mono.component.redis.RedisLock;
import com.huinong.truffle.payment.order.mono.component.redis.client.DefRedisClient;
import com.huinong.truffle.payment.order.mono.component.sys.config.OrderAppConf;
import com.huinong.truffle.payment.order.mono.component.zk.IDGeneratorClient;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants.ClientChannelEnum;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants.DeleteState;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants.OrderStateEnum;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants.PayResultEnum;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants.SourceFromSysEnum;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants.VerifyAmtSwitchEnum;
import com.huinong.truffle.payment.order.mono.constant.OrderResultCode;
import com.huinong.truffle.payment.order.mono.dao.read.MainOrderReadDAO;
import com.huinong.truffle.payment.order.mono.dao.read.OrderReadDAO;
import com.huinong.truffle.payment.order.mono.dao.write.MainOrderWriteDAO;
import com.huinong.truffle.payment.order.mono.dao.write.OrderWriteDAO;
import com.huinong.truffle.payment.order.mono.domain.HnpMainOrder;
import com.huinong.truffle.payment.order.mono.domain.HnpOrder;
import com.huinong.truffle.payment.order.mono.domain.OrderQuery;
import com.huinong.truffle.payment.order.mono.entity.HnpMainOrderEntity;
import com.huinong.truffle.payment.order.mono.entity.HnpOrderEntity;

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
	@Autowired
	private OrderAppConf orderAppConf ;
    @Autowired
    private IDGeneratorClient idGeneratorClient ;

	@Transactional
	public BaseResult<HnpMainOrder> createOrder(HnpMainOrder mainOrder) throws Exception {
		if (null == mainOrder) {
			return BaseResult.fail(OrderResultCode.PARAM_0002);
		}
		if(null == mainOrder.getData()){
			return BaseResult.fail(OrderResultCode.PARAM_0001);
		}
		String orderFromSystem = mainOrder.getOrderFromSystem() ;
		if(StringUtils.isBlank(orderFromSystem)){
			return BaseResult.fail(OrderResultCode.PARAM_0017);
		}
		if(!SourceFromSysEnum.isDefinition(orderFromSystem)){
			return BaseResult.fail(OrderResultCode.PARAM_0025);
		}
		String hnpChannel = mainOrder.getHnchannel() ;
		if(StringUtils.isBlank(hnpChannel)){
			return BaseResult.fail(OrderResultCode.PARAM_0019);
		}
		if(!ClientChannelEnum.isDefinition(hnpChannel)){
			return BaseResult.fail(OrderResultCode.PARAM_0026);
		}
		RedisLock lock = null;
		try {
			// 0 对每一笔预支付订单进行锁处理，防止重复提交
			String mainOrderNo = mainOrder.getMainOrderNo();
			lock = new RedisLock(defRedisClient,OrderConstants.RedisKey.ORDER_REPAY_KEY.value.concat(mainOrderNo));
			if (!lock.lock()) {
				logger.info("订单：" + mainOrderNo + "预支付超时...");
				return BaseResult.fail(OrderResultCode.DB_0013);
			}
			// 1 检测
			HnpMainOrderEntity mainOrderEntity = mainOrderReadDAO.selectByMainOrderNo(mainOrderNo);
			if (null != mainOrderEntity) {
				// 判断订单状态
				if (!mainOrderEntity.isWaitingConfirm()) {
					logger.info("订单：" + mainOrderNo + "已支付，请勿重新支付");
					return BaseResult.fail(OrderResultCode.DB_0001);
				}
				// 重新预支付订单在订单存在情况下需要确认订单数据是否一致
				if(mainOrderEntity.getMsgUUID().equals(mainOrder.genObjectUUID())){
					//数据一致 直接返回订单信息
					HnpMainOrder returnbean = new HnpMainOrder();
					ObjectUtils.mergeProperties(returnbean, mainOrderEntity);
					return BaseResult.success(returnbean);
				}else{
					// 删除以前的主订单和明细数据 加入新的订单信息
					mainOrderWriteDAO.delMainOrder(mainOrderEntity.getId());
					orderWriteDAO.deleteByMainOrderNo(mainOrderNo);
					return persistOrder(mainOrder);
				}
			} else {
				return persistOrder(mainOrder);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("创建订单异常："+e);
			throw new RuntimeException(e);
		} finally {
			if (lock != null) {
				lock.unlock();
			}
		}
	}
	
	
	
	private BaseResult<HnpMainOrder> persistOrder(HnpMainOrder mainOrder) throws Exception{
		// 新增
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
	

	/**
	 * 新增订单信息记录
	 * 
	 * @param hnpOrder
	 * @return
	 * @throws Exception
	 */
	private BaseResult<HnpMainOrder> addOrderRecords(HnpMainOrder mainOrder) throws Exception {
		// 1是否开启验证
		String verifySwitch = orderAppConf.getVerifyAmtSwitch();
		boolean switchFlag = verifySwitch.equals(VerifyAmtSwitchEnum.VERIFY_AMT_OPEN.val)? true : false ;
		// 2 验证子订单金额之和 是否等于 订单总金额
		boolean isEqual = compareTwoAmtIsEqual(mainOrder.getData(),mainOrder.getTotalAmt());
		if (switchFlag && !isEqual) {
			logger.info("子订单金额之和不等于主订单总金额...");
			return BaseResult.fail(OrderResultCode.DB_0012);
		}
		HnpMainOrderEntity record = new HnpMainOrderEntity();
		record.setCreateTime(new Date());
		record.setHnchannel(mainOrder.getHnchannel());
		record.setMainOrderNo(mainOrder.getMainOrderNo());
		record.setModifyTime(new Date());
		record.setOrderState(mainOrder.getOrderState());
		record.setOutUid(mainOrder.getOutUid());
		record.setOrderFromSystem(mainOrder.getOrderFromSystem());
		record.setTotalAmt(mainOrder.getTotalAmt());
		record.setOrderState(OrderStateEnum.ORDER_0.val.intValue());
		record.setMsgUUID(mainOrder.genObjectUUID());
		int i = mainOrderWriteDAO.insert(record);
		if(i <= 0){
			logger.info("添加订单信息失败"); 
			return BaseResult.fail(OrderResultCode.DB_0010);
		}
		addBatchItem(mainOrder.getData(),mainOrder.getMainOrderNo(),mainOrder.getOrderFromSystem());
		HnpMainOrder result = new HnpMainOrder();
		ObjectUtils.mergeProperties(result, record);
		return BaseResult.success(result);
	}
	
	/**
	 * 批量添加订单明细
	 */
	private int addBatchItem(List<HnpOrder> orderItem,String mainOrderNo,String orderFromSystem) throws Exception{
        if(null == orderItem || orderItem.size() == 0) {
            logger.info("批量插的入参对象为空...");
            throw new Exception("批量插的入参对象为空...");
        }
        //订单流水号
        String orderSerialNumber = idGeneratorClient.genOrderSerialNo();
        if(StringUtils.isBlank(orderSerialNumber)){
           orderSerialNumber = "HNOD" + (System.currentTimeMillis() * 100 + new Double( Math.random() * 100).intValue());
        }
        HnpOrderEntity orderEntity = null ;
        List<HnpOrderEntity> list = new ArrayList<HnpOrderEntity>();
        int size=orderItem.size();
        int number=0;
        for(int i=0 ; i<size;i++){
        	HnpOrder order = orderItem.get(i);
        	number=i;
        	orderSerialNumber = orderSerialNumber + String.format("%04d", (++number));
        	orderEntity = new HnpOrderEntity();
        	orderEntity.setAppId(order.getAppId());
        	orderEntity.setAmt(order.getAmt());
        	orderEntity.setGroupOrderNo(mainOrderNo);
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
        	orderEntity.setSerialNumber(orderSerialNumber);
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
		if (StringUtils.isBlank(mainOrder.getOrderFromSystem())){
			logger.info("订单确认失败,平台来源为空");
			return BaseResult.fail(OrderResultCode.PARAM_0017);
		}

		if (StringUtils.isBlank(mainOrder.getMainOrderNo())){
			logger.info("订单确认失败,主订单号为空");
			return BaseResult.fail(OrderResultCode.PARAM_0003);
		}

		if (null == mainOrder.getTotalAmt()){
			logger.info("订单确认失败,订单金额为空");
			return BaseResult.fail(OrderResultCode.PARAM_0005);
		}

		if (null == mainOrder.getOutUid()){
			logger.info("订单确认失败,买家账号为空");
			return BaseResult.fail(OrderResultCode.PARAM_0018);
		}

		if (StringUtils.isBlank(mainOrder.getHnchannel())){
			logger.info("订单确认失败,下单渠道为空");
			return BaseResult.fail(OrderResultCode.PARAM_0019);
		}

		if (null == mainOrder.getData() || mainOrder.getData().size() == 0){
			logger.info("订单确认失败,订单明细为空");
			return BaseResult.fail(OrderResultCode.PARAM_0019);
		}
		return BaseResult.success();
	}

	public BaseResult<HnpMainOrder> queryMainOrder(String mainOrderNo) {
		if(StringUtils.isBlank(mainOrderNo)){
			logger.info("无法识别{主订单编号}");
			return BaseResult.fail(OrderResultCode.PARAM_0003);
		}
		try {
			HnpMainOrderEntity mainOrderEntity = mainOrderReadDAO.selectByMainOrderNo(mainOrderNo);
			if (null == mainOrderEntity) {
				logger.info("参数异常：主订单信息为空");
				return BaseResult.fail(OrderResultCode.DB_0016);
			}
			HnpMainOrder result = new HnpMainOrder();
			ObjectUtils.mergeProperties(result, mainOrderEntity);
			return BaseResult.success(result);
		} catch (Exception e) {
			e.printStackTrace();
			return BaseResult.fail(OrderResultCode.DB_0014);
		}
	}

	/**
	 * 比较子订单金额之和与主订单金额
	 */
	private boolean compareTwoAmtIsEqual(List<HnpOrder> item,
			BigDecimal totalAmount) {
		BigDecimal itemAmt = BigDecimal.ZERO;
		for (HnpOrder bean : item) {
			itemAmt=itemAmt.add(bean.getAmt());
		}
		if (null == totalAmount) {
			totalAmount = BigDecimal.ZERO;
		}
		return itemAmt.doubleValue() == totalAmount.doubleValue();
	}
	
	public BaseResult<List<HnpOrder>> queryDetail(String mainOrderNo) {
		// 0 校验参数
		if (StringUtils.isBlank(mainOrderNo)) {
			logger.info("查询订单参数{主订单号}为空");
			return BaseResult.fail(OrderResultCode.PARAM_0002);
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
				ObjectUtils.mergeProperties(returnbean, entity);
				returnList.add(returnbean);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return  BaseResult.fail(OrderResultCode.DB_0014);
		}
		return BaseResult.success(returnList);
	}

	
	/*@Transactional
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
		if (null == mainOrderEntity) {
			logger.info("查询订单不存在");
			return BaseResult.fail(OrderResultCode.DB_0005);
		}
		//主订单
		HnpMainOrderEntity mainRecord = new HnpMainOrderEntity();
		mainRecord.setMainOrderNo(mainOrderNo);
		mainRecord.setOrderState(orderStatus);
		int i = mainOrderWriteDAO.updateByMainOrderNo(mainRecord);
		//子订单
		HnpOrderEntity orderRecord = new HnpOrderEntity();
		orderRecord.setGroupOrderNo(mainOrderNo);
		orderRecord.setPayState(String.valueOf(orderStatus));
		int j = orderWriteDAO.updateByMainOrderNoSelective(orderRecord);
		if (i > 0 && j > 0) {
			HnpMainOrder mainOrder = new HnpMainOrder();
			ObjectUtils.mergeProperties(mainOrder, mainOrderEntity);
			return BaseResult.success(mainOrder);
		} else {
			logger.info("更新订单状态失败");
			return BaseResult.fail(OrderResultCode.DB_0006);
		}
	}*/

	@Transactional
	public BaseResult<HnpMainOrder> finishOrder(String mainOrderNo,String payStatus) throws Exception {
		if (StringUtils.isBlank(mainOrderNo)) {
			logger.info("参数{订单号}为空");
			return BaseResult.fail(OrderResultCode.PARAM_0002);
		}
		if (StringUtils.isBlank(payStatus)) {
			logger.info("参数{订单状态}为空");
			return BaseResult.fail(OrderResultCode.PARAM_0002);
		}
		
		if(!PayResultEnum.isDefinition(payStatus)){
			logger.info("参数{订单状态}不在自定值范围内(PROCESSING-支付中,SUCCESS-支付成功)");
			return BaseResult.fail(OrderResultCode.PARAM_0023);
		}
		
		//订单状态
		Integer orderStatus = PayResultEnum.getTypeByKey(payStatus).val ;
		RedisLock lock = null;
		try {
			// 0 对每一笔预支付订单进行锁处理，防止重复提交
			lock = new RedisLock(defRedisClient,OrderConstants.RedisKey.ORDER_FINISH_KEY.value.concat(mainOrderNo));
			if (!lock.lock()) {
				logger.info("订单：" + mainOrderNo + "更新状态超时");		
				return BaseResult.fail(OrderResultCode.DB_0013);
			}
			
			HnpMainOrderEntity mainOrderEntity = mainOrderReadDAO.selectByMainOrderNo(mainOrderNo);
			if(null == mainOrderEntity){
				logger.info("订单：" + mainOrderNo + "检索订单结果为空");
				return BaseResult.fail(OrderResultCode.DB_0016);
			}
			
			HnpMainOrderEntity mainRecord = new HnpMainOrderEntity();
			mainRecord.setMainOrderNo(mainOrderNo);
			mainRecord.setOrderState(orderStatus);
			int i = mainOrderWriteDAO.updateByMainOrderNo(mainRecord);
			
			HnpOrderEntity orderRecord = new HnpOrderEntity();
			orderRecord.setGroupOrderNo(mainOrderNo);
			orderRecord.setPayState(String.valueOf(orderStatus));
			orderRecord.setPayedTimestamp(new Date());
			int j = orderWriteDAO.updateByMainOrderNoSelective(orderRecord);
			if (i > 0 && j > 0) {
				HnpMainOrder mainOrder = new HnpMainOrder();
				ObjectUtils.mergeProperties(mainOrder, mainOrderEntity);
				return BaseResult.success(mainOrder);
			} else {
				logger.info("更新订单状态失败");
				throw new RuntimeException(OrderResultCode.DB_0005.getMsg());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if (lock != null) {
				lock.unlock();
			}
		}
	}
	
	/**
	 * 根据子订单流水号查询订单信息
	 * @param orderSerialNumber
	 * @return
	 */
	public BaseResult<HnpOrder> queryBySerialNumber(String serialNumber) {
		// 0 校验参数
		if (StringUtils.isBlank(serialNumber)) {
			logger.info("查询订单参数{子订单流水号}为空");
			return BaseResult.fail(OrderResultCode.PARAM_0002);
		}
		try {
			HnpOrderEntity orderEntity = orderReadDAO.selectBySerialNumber(serialNumber);
			if (null == orderEntity) {
				logger.info("检索订单结果为空");
				return BaseResult.fail(OrderResultCode.DB_0004);
			}
			HnpOrder returnbean = new HnpOrder();
			ObjectUtils.mergeProperties(returnbean, orderEntity);
			return BaseResult.success(returnbean);
		} catch (Exception e) {
			e.printStackTrace();
			return BaseResult.fail(OrderResultCode.DB_0014);
		}
	}
	
	/**
	 * 根据流水号更新订单状态
	 * @param serialNumber 订单流水号
	 * @param state  订单状态
	 * @return
	 */
	public BaseResult<Void> updateHnpDetailBySerialNumber(String serialNumber,Integer state) {
		if(StringUtils.isBlank(serialNumber)){
			logger.info("无法识别{订单流水号}");
			return BaseResult.fail(OrderResultCode.PARAM_0020);
		}
		if(null == state){
			logger.info("无法识别{订单状态}");
			return BaseResult.fail(OrderResultCode.PARAM_0021);
		}
		
		if(!OrderStateEnum.isDefinition(state)){
			logger.info("无法识别{订单状态}");
			return BaseResult.fail(OrderResultCode.PARAM_0022);
		}
		
		HnpOrderEntity record = new HnpOrderEntity();
		record.setSerialNumber(serialNumber);
		record.setPayState(String.valueOf(state));
		record.setPayedTimestamp(new Date());
		int i = orderWriteDAO.updateBySerialNumberSelective(record);
		if(i <= 0){
			logger.info("更新订单状态失败");
			return BaseResult.fail(OrderResultCode.DB_0017);
		}
		return BaseResult.success();
	}
	
	/**
	 * 分页查询订单列表
	 * @param query
	 * @return
	 */
	public BaseResult<PageValue<HnpOrder>> queryPageOrderData(OrderQuery query){
		try {
			Integer pageNum = query.getPageNum() == null?1:query.getPageNum() ;
            Integer pageSize = query.getPageSize() == null?10: query.getPageSize();
            PageHelper.startPage(pageNum, pageSize);
            List<HnpOrderEntity> orderlist = orderReadDAO.listBySelective(query);
            PageValue<HnpOrderEntity> orderEntity = new PageValue<HnpOrderEntity>(orderlist);
            PageValue<HnpOrder> orderValue = ObjectUtils.transfer(orderEntity);
            return new BaseResult<PageValue<HnpOrder>>(orderValue);
		} catch (Exception e) {
			e.printStackTrace();
			return BaseResult.fail(OrderResultCode.DB_0014);
		}
	}
	
}
