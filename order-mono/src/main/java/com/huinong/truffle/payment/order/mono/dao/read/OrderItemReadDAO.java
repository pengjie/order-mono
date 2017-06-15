/*package com.huinong.truffle.payment.order.mono.dao.read;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.huinong.truffle.payment.order.mono.entity.HnpDetailEntity;

*//**
 * @author peng
 * 订单明细读DAO
 *
 *//*
@Repository
public interface OrderItemReadDAO {
	

	*//**
	 * 查询子订单信息
	 * @param uniqueValue
	 * @return
	 * @throws Exception
	 *//*
	public HnpDetailEntity getDTOByUniqueValue(@Param("serialNumber") String serialNumber) throws Exception ;
	
	*//**
	 * 查询子订单列表
	 * @param mainOrderNo 主订单号
	 * @return
	 * @throws Exception
	 *//*
	public List<HnpDetailEntity> getOrderItemList(@Param("mainOrderNo") String mainOrderNo) throws Exception ;

	
}
*/