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
	
	//银行卡手续费
    @Value("${handcharge.cardpay}")
    private String bankCardFee;
    
    //微信手续费
    @Value("${handcharge.wxpay}")
    private String wxfee ;
    
    //支付宝手续费
    @Value("${handcharge.alipay}")
    private String alifee ;
    
    //快捷支付手续费
    @Value("${handcharge.quickpay}")
    private String quickfee ;
    
    //zk 
    @Value("${zk.zkQuorurm}")
    private String zkQuorurm ;
    
    @Value("${zk.serialGenZnode}")
    private String serialGenZnode ;
    

    public String getBankCardFee() {
        return bankCardFee;
    }

    public void setBankCardFee(String bankCardFee) {
        this.bankCardFee = bankCardFee;
    }

    public String getWxfee() {
        return wxfee;
    }

    public void setWxfee(String wxfee) {
        this.wxfee = wxfee;
    }

    public String getAlifee() {
        return alifee;
    }

    public void setAlifee(String alifee) {
        this.alifee = alifee;
    }

    public String getQuickfee() {
        return quickfee;
    }

    public void setQuickfee(String quickfee) {
        this.quickfee = quickfee;
    }

	public String getZkQuorurm() {
		return zkQuorurm;
	}

	public void setZkQuorurm(String zkQuorurm) {
		this.zkQuorurm = zkQuorurm;
	}

	public String getSerialGenZnode() {
		return serialGenZnode;
	}

	public void setSerialGenZnode(String serialGenZnode) {
		this.serialGenZnode = serialGenZnode;
	}

	@Override
	public String toString() {
		return "OrderAppConf [bankCardFee=" + bankCardFee + ", wxfee=" + wxfee
				+ ", alifee=" + alifee + ", quickfee=" + quickfee
				+ ", zkQuorurm=" + zkQuorurm + ", serialGenZnode="
				+ serialGenZnode + "]";
	}
}
