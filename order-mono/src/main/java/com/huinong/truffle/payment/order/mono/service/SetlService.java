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
import com.huinong.truffle.component.base.constants.BaseResult;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants.DirectEventEnum;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants.DirectStateEnum;
import com.huinong.truffle.payment.order.mono.constant.OrderConstants.OrderStateEnum;
import com.huinong.truffle.payment.order.mono.constant.OrderResultCode;
import com.huinong.truffle.payment.order.mono.dao.read.OutInMoneyReadDAO;
import com.huinong.truffle.payment.order.mono.dao.write.OrderWriteDAO;
import com.huinong.truffle.payment.order.mono.dao.write.OutInMoneyWriteDAO;
import com.huinong.truffle.payment.order.mono.domain.OutInMoney;
import com.huinong.truffle.payment.order.mono.entity.HnpOrderEntity;
import com.huinong.truffle.payment.order.mono.entity.OutInMoneyEntity;
import com.huinong.truffle.payment.order.mono.util.CopyBeanUtil;

/**
 * 订单结算
 * 
 * @author peng
 *
 */
@Service("setlService")
public class SetlService {

	private static Logger logger = LoggerFactory.getLogger(SetlService.class);
	public Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	@Autowired
	private OutInMoneyReadDAO outInMoneyReadDAO ;
	
	@Autowired
	private OutInMoneyWriteDAO outInMoneyWriteDAO ;
	
	@Autowired
	private OrderWriteDAO orderWriteDAO ;


	public BaseResult<List<OutInMoney>> listProcess() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("directStatus", DirectStateEnum.PROCESSING.val);
		List<OutInMoneyEntity> list = outInMoneyReadDAO.listOutInMoney(param);
		if(null == list || list.size() == 0){
			return BaseResult.success(new ArrayList<OutInMoney>());
		}else{
			List<OutInMoney> returnList = new ArrayList<OutInMoney>();
			OutInMoney dto = null ;
			for(OutInMoneyEntity entity : list){
				dto = new OutInMoney();
				CopyBeanUtil.getInstance().copyBeanProperties(entity, dto);
				returnList.add(dto);
			}
			return BaseResult.success(returnList);
		}
	}

	@Transactional
	public BaseResult<Boolean> stateChange(OutInMoney reqDTO) throws Exception {
		String type = reqDTO.getType();
		if (StringUtils.isBlank(type)) {
			logger.info("无法识别关键字{付款方向}");
			return BaseResult.fail(OrderResultCode.PARAM_0017);
		}
		if (!(type.equals(DirectEventEnum.DIRECT_PAY.val) || type.equals(DirectEventEnum.DIRECT_REFUND.val))) {
			logger.info("付款方向不在指定范围值内(0-付款 1-退款)");
			return BaseResult.fail(OrderResultCode.PARAM_0020);
		}
		Long orderId = reqDTO.getOrderId();
		if (null == orderId) {
			logger.info("无法识别关键字{订单ID}");
			return BaseResult.fail(OrderResultCode.PARAM_0021);
		}
		String serialNumber = reqDTO.getSerialNumber();
		if (StringUtils.isBlank(serialNumber)) {
			logger.info("无法识别关键字{结算流水号}");
			return BaseResult.fail(OrderResultCode.PARAM_0022);
		}
		String state = reqDTO.getDirectStatus();
		if (StringUtils.isBlank(state)) {
			logger.info("无法识别关键字{付款状态}");
			return BaseResult.fail(OrderResultCode.PARAM_0023);
		}
		String resCode = reqDTO.getResCode();
		if (StringUtils.isBlank(state)) {
			logger.info("无法识别关键字{付款返回码}");
			return BaseResult.fail(OrderResultCode.PARAM_0024);
		}
		String resMessage = reqDTO.getResMessage();
		if (StringUtils.isBlank(resMessage)) {
			logger.info("无法识别关键字{付款返回消息}");
			return BaseResult.fail(OrderResultCode.PARAM_0025);
		}
		// 更新结算付款记录状态
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("directStatus", state);
		map.put("serialNumber", serialNumber);
		map.put("resCode", resCode);
		map.put("resMessage", resMessage);
		map.put("backTime", new Date());
		int i = outInMoneyWriteDAO.updateOneOutInMoney(map);
		if (i <= 0) {
			logger.info("更新付款状态失败");
			return BaseResult.fail(OrderResultCode.DB_0017);
		}
		
		// 更新单笔订单状态
		HnpOrderEntity record = new HnpOrderEntity();
		record.setId(orderId);
		record.setFinishedTimestamp(new Date());
		if (state.equals(DirectStateEnum.PROCESSING.val)) {
			// 处理中
			record.setPayState(String.valueOf(OrderStateEnum.ORDER_6.val));
		} else {
			if (type.equals(DirectEventEnum.DIRECT_PAY.val)) {
				// 确认收货-付款
				record.setPayState(String.valueOf(OrderStateEnum.ORDER_3.val));
			} else {
				// 确认退货-退款
				record.setPayState(String.valueOf(OrderStateEnum.ORDER_4.val));
			}
		}
		int m = orderWriteDAO.updateByPrimaryKeySelective(record);
		if(m <=0){
			logger.info("更新付款明细订单状态失败");
			return BaseResult.fail(OrderResultCode.DB_0018);
		}
		return BaseResult.success(true);
	}



}
