package com.huinong.truffle.payment.order.mono.dao.write;

import java.util.List;
import java.util.Map;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.huinong.truffle.payment.order.mono.entity.HnpDetailEntity;

/**
 * @author peng
 * 订单明细写DAO
 *
 */
@Repository
public interface OrderItemWriteDAO {
	
	/**
	 * 添加订单明细
	 * @param dto
	 * @throws Exception
	 */
	public int addOrderItem(HnpDetailEntity dto) throws Exception;
	
	
	/**
	 * 批量添加订单明细信息
	 * @param orderItem
	 * @param orderFromSystem
	 * @throws Exception
	 */
	public Long addBatchItem(List<Map<String,Object>> orderItem) throws Exception;
	
	/**
	 * 更新子订单信息
	 * @param mainOrderNo  订单号
	 * @param orderStatus  订单状态
	 * @return
	 * @throws Exception
	 */
	public int update(@Param("mainOrderNo") String mainOrderNo,@Param("orderStatus") Integer orderStatus) throws Exception ;
	
	public int updateOrderStatus(@Param("entity") HnpDetailEntity detailDTO) throws Exception ;
	
	
	/**
	 * 删除子订单
	 * @param mainOrderNo
	 * @return
	 * @throws Exception
	 */
	public int delete(@Param("mainOrderNo") String mainOrderNo) throws Exception ;
	
}
