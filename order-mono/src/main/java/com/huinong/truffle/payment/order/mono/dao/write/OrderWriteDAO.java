package com.huinong.truffle.payment.order.mono.dao.write;

import java.util.List;

import com.huinong.truffle.payment.order.mono.entity.HnpOrderEntity;

/**
 * 子订单DAO
 * @author peng
 *
 */
public interface OrderWriteDAO {
	
	/**
	 * 根据主订单号删除子订单列表
	 * @param mainOrderNo
	 * @return
	 */
	public int deleteByMainOrderNo(String mainOrderNo) ;

	/**
	 * 新增子订单信息
	 * @param record
	 * @return
	 */
    public int insert(HnpOrderEntity record);
    
    /**
     * 批量插入子订单
     * @param data
     * @return
     */
    public int batchInsertOrder(List<HnpOrderEntity> data) ;
    
    /**
     * 更新子订单信息
     * @param record
     * @return
     */
    public int updateByPrimaryKeySelective(HnpOrderEntity record);
    
    /**
     * 根据主订单号更新子订单信息
     * @param record
     * @return
     */
    public int updateByMainOrderNoSelective(HnpOrderEntity record);
    
    /**
     * 根据订单流水号更新订单信息
     * @param record
     * @return
     */
    public int updateBySerialNumberSelective(HnpOrderEntity record);
    
    

}