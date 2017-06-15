package com.huinong.truffle.payment.order.mono.dao.read;

import java.util.List;

import com.huinong.truffle.payment.order.mono.entity.HnpOrderEntity;

/**
 * 订单明细DAO
 * @author peng
 *
 */
public interface OrderReadDAO {


	/**
	 * 根据主键ID查询子订单信息
	 * @param id
	 * @return
	 */
    public HnpOrderEntity selectByPrimaryKey(Long id);
    
    /**
     * 根据子订单流水号查询子订单信息
     * @param serialNumber
     * @return
     */
    public HnpOrderEntity selectBySerialNumber(String serialNumber) ;
    
    /**
     * 根据子订单编号查询子订单信息
     * @param orderNo
     * @return
     */
    public HnpOrderEntity selectByOrderNo(String orderNo) ;
    
    /**
     * 根据主订单号查询子订单列表
     * @param mainOrderNo
     * @return
     */
    public List<HnpOrderEntity> listByMainOrderNo(String mainOrderNo) ;


}