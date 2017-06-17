package com.huinong.truffle.payment.order.mono.component.zk;

import java.io.IOException;
import java.util.Date;

import org.lable.oss.uniqueid.GeneratorException;
import org.lable.oss.uniqueid.IDGenerator;
import org.lable.oss.uniqueid.zookeeper.SynchronizedUniqueIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huinong.truffle.payment.order.mono.component.sys.config.OrderAppConf;
import com.huinong.truffle.payment.order.mono.util.DateUtils;

@Component
public class SerialGenZkImpl implements SerialGen {
	
	@Autowired
	private OrderAppConf orderAppConf ;
	
	private static IDGenerator generator = null;
	
	 public SerialGenZkImpl() {}

	 private String gen() {
		 final String zookeeperQuorum = orderAppConf.getZkQuorurm();
		 final String znode = orderAppConf.getSerialGenZnode();
	        try {
	        	generator = SynchronizedUniqueIDGenerator.generatorFor(zookeeperQuorum, znode);
			} catch (IOException e) {
				 throw new RuntimeException("无法初始化序列号发生器,请检查zookeeper集群和调用网络");
			}
	        
	        byte[] ser = null;
	        try {
	            ser = generator.generate();
	            return String.valueOf(bytes8tolong(ser));
	        } catch (GeneratorException e) {
	            throw new RuntimeException(String.format("生成序列号失败,请检查zookeeper: {quorum: %s, znode: %s", zookeeperQuorum, znode));
	        }
	    }
	 
	 private long bytes8tolong(byte[] by) {
        long value = 0;
        for (int i = 0; i < by.length; i++) {
            value += ((long) by[i] & 0xffL) << (8 * i);
        }
        return value;
    }
	   

	@Override
    public String genOrderSerialNo(){
        return "HNOD" + gen();
    }
    
    @Override
    public String genIntoPaySerialNo() {
        return "HNIN" + gen();
    }

    @Override
    public String genTxSNBinding() {
        return "HNTX" + gen();
    }
    
    @Override
    public String genTxSNUnBinding(){
        return "HNUN" + gen();
    }

    @Override
    public String genSettleSerialNo() {
        return "HNST" + gen();
    }

    @Override
    public String genBillSerialNo() {
        return "HNBL" + gen();
    }

    @Override
    public String genOutPaySerialNo() {
        return "HNOT" + gen();
    }

    @Override
    public String genRefundSerialNo() {
        return "HNRF" + gen();
    }
    
    @Override
	public String genInMoneyNo() {
    	return "HNIN" + gen();
	}
    
    @Override
	public String genOrderNo(Date dt) {
    	return DateUtils.formatDateyyyymmdd(dt);
	}
}
