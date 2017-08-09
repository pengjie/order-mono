package com.huinong.truffle.payment.order.mono.component.sys.config;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * 订单配置
 * @author peng
 *
 */
@Configuration
@RefreshScope
public class OrderAppConf implements Serializable {
	private static final long serialVersionUID = 1L;
	
    //是否验证预支付 校验主订单支付金额和子订单金额之和
    @Value("${order.verify.prepay.amt.switch}")
    private String verifyAmtSwitch ;
    
	public String getVerifyAmtSwitch() {
		return verifyAmtSwitch;
	}

	public void setVerifyAmtSwitch(String verifyAmtSwitch) {
		this.verifyAmtSwitch = verifyAmtSwitch;
	}

/*	@Override
	public String toString() {
		return "OrderAppConf [zkQuorurm=" + zkQuorurm + ", serialGenZnode="
				+ serialGenZnode + ", verifyAmtSwitch=" + verifyAmtSwitch + "]";
	}*/
	
}
