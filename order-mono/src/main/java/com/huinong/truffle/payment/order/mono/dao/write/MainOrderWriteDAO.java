package com.huinong.truffle.payment.order.mono.dao.write;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.huinong.truffle.payment.order.mono.entity.HnpOrderEntity;

/**
 * @author peng
 * 主订单写DAO
 *
 */
@Repository
public interface MainOrderWriteDAO {
	
	/**
	 * 添加主订单
	 * @param dto
	 * @return
	 * @throws Exception
	 */
	public Long addMainOrder(HnpOrderEntity dto) throws Exception ;
	
	
	/**
	 * 更新主订单
	 * @param mainOrderNo
	 * @param orderStatus
	 * @return
	 * @throws Exception
	 */
	public int update (@Param("mainOrderNo") String mainOrderNo,@Param("orderStatus") Integer orderStatus) throws Exception ;
	
	
	/**
	 * 删除订单信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public int delMainOrder(@Param("id") Long id) throws Exception;

}
