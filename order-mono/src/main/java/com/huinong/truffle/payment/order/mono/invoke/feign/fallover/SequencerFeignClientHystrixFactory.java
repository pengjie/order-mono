/**
 * ID 发号器
 */
package com.huinong.truffle.payment.order.mono.invoke.feign.fallover;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.huinong.framework.autoconfigure.web.BaseResult;
import com.huinong.truffle.payment.order.mono.constant.OrderResultCode;
import com.huinong.truffle.payment.order.mono.invoke.feign.SequencerFeignClient;

import feign.hystrix.FallbackFactory;

/**
 * @author peng
 *
 */
@Component
public class SequencerFeignClientHystrixFactory implements FallbackFactory<SequencerFeignClient> {
  private static final Logger LOGGER = LoggerFactory.getLogger(SequencerFeignClientHystrixFactory.class);
  
  @Override
  public SequencerFeignClient create(Throwable cause){
      SequencerFeignClientHystrixFactory.LOGGER.error("fallback; reason was: {}", cause.getMessage());
      return new SequencerFeignClient() {
          @Override
          public BaseResult<Long> genId(){
              return new BaseResult<Long>(OrderResultCode.CODE_HYSTRIX_ERROR.getCode(),
                  OrderResultCode.CODE_HYSTRIX_ERROR.getMsg());
          }
      };
  }

}
