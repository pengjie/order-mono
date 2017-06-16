package com.huinong.truffle.payment.order.mono.dao.read;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.huinong.truffle.payment.order.mono.entity.HnpMainOrderEntity;

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
	public HnpOrderEntity getDTOByUniqueValue(Object uniqueValue) throws Exception;
	*/
	
	/**
	 * 按主订单号查询订单信息
	 * @param mainOrderNo
	 * @return
	 */
	public HnpMainOrderEntity selectByMainOrderNo(@Param("mainOrderNo") String mainOrderNo) ;
	

}
