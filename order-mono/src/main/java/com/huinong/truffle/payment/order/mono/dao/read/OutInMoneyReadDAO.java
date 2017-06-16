package com.huinong.truffle.payment.order.mono.dao.read;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.huinong.truffle.payment.order.mono.entity.OutInMoneyEntity;

/**
 * @author peng
 * 结算订单读DAO
 *
 */
@Repository
public interface OutInMoneyReadDAO {
	
	/**
	 * 根据订单流水号和用户类型确定一笔付款单信息
	 * @param orderSerialNumber
	 * @param userType
	 * @return
	 * @throws Exception
	 */
	public OutInMoneyEntity getByOrderSerialNumber(@Param("orderSerialNumber") String orderSerialNumber,@Param("userType") String userType) throws Exception;
	
	/**
	 * 根据结算流水号查询结算订单信息
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */
	public OutInMoneyEntity getBySerialNumber(@Param("serialNumber") String serialNumber) throws Exception ;
	
	
	/**
	 * 查询结算订单列表
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<OutInMoneyEntity> listOutInMoney(@Param("query") Map<String, Object> param) throws Exception;
	
	/**
	 * 根据主订单号统计总计发生额
	 * @param mainOrderNo
	 * @return
	 * @throws Exception
	 */
	public Double calcOccurByMainOrderNo(@Param("mainOrderNo") String mainOrderNo) throws Exception ;
	

}
