package com.huinong.truffle.payment.order.mono.dao.write;

import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.huinong.truffle.payment.order.mono.entity.OutInMoneyEntity;

/**
 * @author peng
 * 结算订单写DAO
 *
 */
@Repository
public interface OutInMoneyWriteDAO {
	
	/*public static void main(String[] args) {
		org.apache.ibatis.type.JdbcType.VARCHAR.name() ;
	}*/
	
	/**
	 * 添加结算制表单
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	public int addOneOutInMoney(@Param("entity") OutInMoneyEntity entity) throws Exception ;
	
	/**
	 * 修改结算付款单
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int updateOneOutInMoney(@Param("entity") Map<String, Object> param) throws Exception ;

}
