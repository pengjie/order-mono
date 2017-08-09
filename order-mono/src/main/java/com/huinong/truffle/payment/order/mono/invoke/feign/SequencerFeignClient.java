/**
 * ID 发号器
 */
package com.huinong.truffle.payment.order.mono.invoke.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;

import com.huinong.truffle.component.base.constants.BaseResult;
import com.huinong.truffle.payment.order.mono.invoke.feign.fallover.SequencerFeignClientHystrixFactory;

/**
 * @author peng
 *
 */
@FeignClient(name = "sequencer", fallbackFactory = SequencerFeignClientHystrixFactory.class)
public interface SequencerFeignClient {
  // 通过Spring MVC的注解来配置compute-service服务下的具体实现。
  @RequestMapping(method = RequestMethod.GET, value = "/genid")
  public BaseResult<Long> genId();
}
