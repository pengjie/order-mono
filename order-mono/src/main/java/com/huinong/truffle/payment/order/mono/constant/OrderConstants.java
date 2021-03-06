package com.huinong.truffle.payment.order.mono.constant;
/**
 * 订单常量
 * @author peng
 *
 */
public class OrderConstants {
	
	 //订单状态:0-待买家付款，1-买家付款中，2-买家已付款，5-待买家确认（已结算）,3-买家确认收货（交易成功）,4-买家退款（交易结束）,6-平台付款中
    public static enum OrderStateEnum{
        ORDER_0(0),ORDER_1(1),ORDER_2(2),
        ORDER_3(3),ORDER_4(4),ORDER_5(5),
        ORDER_6(6);
        public Integer val ;
        OrderStateEnum(Integer val){
            this.val = val ;
        }
        public static boolean isDefinition(Integer value) {
            for (OrderStateEnum type : OrderStateEnum.values()) {
                if (type.val.intValue() == value.intValue() )
                    return true;
            }
            return false;
        }
    }
    
    public static enum RedisKey{
        ORDER_REPAY_PARAM_KEY("ORDER_REPAY_PARAM_KEY_"),
        ORDER_REPAY_KEY("ORDER_REPAY_KEY_"),
        ORDER_FINISH_KEY("ORDER_FINISH_KEY_"),
        ORDER_SETL_KEY("ORDER_SETL_KEY_"),
        ORDER_REFUND_KEY("ORDER_REFUND_KEY_");
        public String value;
        RedisKey(String value) {
            this.value = value;
        }
    }
    
    //删除状态 0-有效 1-删除
    public static enum DeleteState{
        DELETE_TYPE_T(0),DELETE_TYPE_F(1);
        public Integer val ;
        DeleteState(Integer val){
            this.val = val ;
        }
    }
    
    //结算触发事件 0-手动 1-自动
    public static enum TriggerTypeEnum{
        TRIGGER_MANUAL("0"),TRIGGER_AUTO("1");
        public String val ;
        TriggerTypeEnum(String val){
            this.val = val ;
        }
    }
    
    //直连付款初始状态
    public static enum DirectStateEnum{
        /**初始状态**/
        INITIAL("0"),
        /**成功**/
        SUCCESS("1"),
        /**失败**/
        FAIL("2"),
        /**处理中**/
        PROCESSING("3");
        public String val;
        DirectStateEnum(String val){
            this.val = val ;
        }
    }
    
    //请求参数
    public static enum ReqParamEnum{
        REQ_PARAM_ORDER_NO("mainOrderNo"),
        REQ_PARAM_ORDER_ITEM("data");
        public String val ;
        ReqParamEnum(String val){
            this.val = val ;
        }
    }

    //付款动作 0-付款 1-退款
    public static enum DirectEventEnum{
    	DIRECT_PAY("0"),DIRECT_REFUND("1");
    	public String val ;
    	DirectEventEnum(String val){
    		this.val = val ;
    	}
    }
    
    //付款商家標志 1买家 2卖家
    public static enum CmbPayShopEnum{
    	CMB_PAY_SHOP_BUYER("1"),CMB_PAY_SHOP_SELLER("2");
    	public String val ;
    	CmbPayShopEnum(String val){
    		this.val = val ;
    	}
    }
    
    //支付状态 2-快捷 6-支付宝 7-微信
    public static enum PayChannelEnum{
        PAYCHANEL_QUICK(2),PAYCHANEL_WX(7),PAYCHANEL_ALI(6);
        public Integer val ;
        PayChannelEnum(Integer val){
            this.val = val ;
        }
    }
    
    //订单聚合服务传入订单状态 - Integer orderState = OrderStateEnum.ORDER_2.val ;
    public static enum PayResultEnum{
        /**成功**/
    	SUCCESS("SUCCESS",2),
        /**支付中**/
    	PROCESSING("PROCESSING",1);
        public String key;
        public Integer val ;
        PayResultEnum(String key,Integer val){
            this.val = val ;
            this.key = key ;
        }
        public static boolean isDefinition(String key) {
            for (PayResultEnum type : PayResultEnum.values()) {
                if (type.key.equals(key))
                    return true;
            }
            return false;
        }
        
        public static PayResultEnum getTypeByKey(String key) {
            for (PayResultEnum type : PayResultEnum.values()) {
            	 if (type.key.equals(key))
                    return type;
            }
            return null;
        }
    }
    
    //日志（redis key）
    public static enum Payment_Log{
    	PAYMENT_LOG_ERROR("payment_log_error");
    	public String val;
    	Payment_Log(String val){
    		this.val=val;
    	}
    }
    
    //是否验证 预支付 验证订单总额与子订单列表金额之和
    public static enum VerifyAmtSwitchEnum{
    	VERIFY_AMT_OPEN("Y"),VERIFY_AMT_DOWN("N");
    	public String val ;
    	VerifyAmtSwitchEnum(String val){
    		this.val = val ;
    	}
    }
    
    //订单来源
    public static enum SourceFromSysEnum{
		SYS_FROM_HNW("HNW"),SYS_FROM_HNYX("HNYX");
		public String val ;
		SourceFromSysEnum(String val){
			this.val = val;
		}
        public static boolean isDefinition(String val) {
            for (SourceFromSysEnum type : SourceFromSysEnum.values()) {
                if (type.val.equals(val))
                    return true;
            }
            return false;
        }
    }
    
    //下单渠道  H5|IOS|ANDROID|PC
    public static enum ClientChannelEnum{
    	CLIENT_PC("PC"),CLIENT_H5("H5"),CLIENT_IOS("IOS"),CLIENT_ANDROID("ANDROID");
    	public String val ;
    	ClientChannelEnum(String val){
    		this.val = val ;
    	}
        public static boolean isDefinition(String val) {
            for (ClientChannelEnum type : ClientChannelEnum.values()) {
                if (type.val.equals(val))
                    return true;
            }
            return false;
        }
    }
    
}
