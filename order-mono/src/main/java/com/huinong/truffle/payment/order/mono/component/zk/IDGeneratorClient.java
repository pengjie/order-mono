/**
 * 生成支付端流水号
 */
package com.huinong.truffle.payment.order.mono.component.zk;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huinong.truffle.component.base.constants.BaseResult;
import com.huinong.truffle.component.base.constants.ResultCode;
import com.huinong.truffle.payment.order.mono.invoke.feign.SequencerFeignClient;

/**
 * @author peng
 *
 */
@Component
public class IDGeneratorClient {
  private static Logger logger = LoggerFactory.getLogger(IDGeneratorClient.class);
  
  @Autowired
  private SequencerFeignClient sequencerFeignClient ;
  
  /**
   * 订单流水号
   * @return
   */
  public String genOrderSerialNo(){
    return "HNOD" + genUniqueidIdVal();
  }
  
  
  /**
   * 创建唯一流水ID
   * @return
   */
  private String genUniqueidIdVal(){
    String idVal = "" ;
    try {
      BaseResult<Long> idResult = sequencerFeignClient.genId() ;
      if(idResult.getCode() == ResultCode.SUCCESS.getCode()){
        idVal = idResult.getData() + "" ;
      }
    } catch (Exception e) {
      logger.error("调用发报器,生成ID异常",e);
      e.printStackTrace();
    }
    if(StringUtils.isBlank(idVal)){
      idVal = ( System.currentTimeMillis() + "" + new Double( Math.random() * 1000000).longValue());
    }
    return idVal ;
  }

}
