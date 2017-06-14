package com.huinong.truffle.payment.order.mono.dao.read;

import org.springframework.stereotype.Repository;

import com.huinong.truffle.payment.order.mono.entity.HnpOrderEntity;

/**
 * @author peng
 * 主订单读DAO
 *
 */
@Repository
public interface MainOrderReadDAO {
	
	/**
	 * 查询订单信息
	 * @param uniqueValue
	 * @return
	 * @throws Exception
	 */
	public HnpOrderEntity getDTOByUniqueValue(Object uniqueValue) throws Exception;

}
