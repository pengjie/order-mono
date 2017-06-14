package com.huinong.truffle.payment.order.mono.component.zk;

/**
 * @author peng
 *
 */
public class SerialGenFactory {
	private static SerialGen serialGen = null;

    private static final Object object = new Object();
    
    public static SerialGen getInstance() {
        if (serialGen == null) {
        	synchronized (object) {
        		if(serialGen == null){
        			serialGen = new SerialGenZkImpl();
        		}
			}
        }
        return serialGen;
    }
}
