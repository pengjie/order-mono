package com.huinong.truffle.payment.order.mono.component.zk;

import java.util.Date;

/**
 * 流水号
 * @author peng
 *
 */
public interface SerialGen {
    //支付单号[入金流水号]
    public abstract String genIntoPaySerialNo();
    
    //订单流水号
    public abstract String genOrderSerialNo();
    
    //绑卡流水号
    public abstract String genTxSNBinding();
    
    //解除绑卡流水号
    public abstract String genTxSNUnBinding();
    
    //结算流水号
    public abstract String genSettleSerialNo();
    
    //对账流水号
    public abstract String genBillSerialNo();
    
    //付款流水号[出金流水号]
    public abstract String genOutPaySerialNo();
    
    //退款流水号
    public abstract String genRefundSerialNo();
    
    //结算交易流水号
	public abstract String genInMoneyNo();

	// 中金订单号
	public abstract String genOrderNo(Date date);
    

}