package com.huinong.truffle.payment.order.mono.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import com.huinong.truffle.payment.order.mono.constant.OrderConstants;

/**
 * 结算确认订单明细
 * @author peng
 *
 */
@ApiModel(value="HnpSetlDetail" ,description="确认收货订单对象")
public class HnpSetlDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    
    //主订单号
	@ApiModelProperty(value="主订单编号",required=false)
    private String mainOrderNo ;
    //订单流水号
	@ApiModelProperty(value="订单流水号",required=false)
    private String serialNumber ;
    //子订单
	@ApiModelProperty(value="订单编号",required=false)
    private String orderNo ;
    //订单金额
	@ApiModelProperty(value="结算金额",required=false)
    private Double amt ;
    //买家ID
	@ApiModelProperty(value="买家ID",required=false)
    private String appPayerId;


    //平台标识
	@ApiModelProperty(value="平台标识0",hidden=true)
    private Integer appId = 0 ;
    //卖家ID
	@ApiModelProperty(value="卖家ID",hidden=true)
    private String appPayeeId ;
    //订单描叙
	@ApiModelProperty(value="订单描叙",hidden=true)
    private String orderDesc ;
    //买家
	@ApiModelProperty(value="买家名称",hidden=true)
    private String shopName ;
    
    //触发方式 0-手动 1-自动
	@ApiModelProperty(value="触发方式 0-手动 1-自动",hidden=true)
    private String triggerType ;
    //结算手动-验证支付密码
	@ApiModelProperty(value="结算手动-支付密码",hidden=true)
    private String password ;
    
    //追加卖家收款卡信息
    @ApiModelProperty(value="收款方账号",required=false)
    private String payeeAccount ;
    @ApiModelProperty(value="账户名称",required=false)
    private String payeeName; 
    @ApiModelProperty(value="收款行名",required=false)
    private String payeeBank ;
    @ApiModelProperty(value="开户行地址",required=false)
    private String payeeBankAddress ;
    @ApiModelProperty(value="招商卡（Y-是 N-否）",required=false)
    private String bankFLG ;
    
    //追加支付方式
    @ApiModelProperty(value="支付渠道",hidden=true)
    private String payChannel ;
    @ApiModelProperty(value="支付流水号",hidden=true)
    private String paymentNo ;
    
    //追加手续费
    @ApiModelProperty(value="手续费",hidden=true)
    private Double chargeFee ;
    @ApiModelProperty(value="需结算金额",hidden=true)
    private Double transAmt;
    @ApiModelProperty(value="手续费规则",hidden=true)
    private String ruleConfig;
    @ApiModelProperty(value="结算方式（0-确认收货1-退款）",hidden=true)
    private String type ;
    
    //追加方向  收款用户类型(1:买家，2：卖家)
    @ApiModelProperty(value="收款用户类型(1:买家，2：卖家)",hidden=true)
    private String receiveUserType;
    
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Double getAmt() {
        return amt;
    }

    public void setAmt(Double amt) {
        this.amt = amt;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getOrderDesc() {
        return orderDesc;
    }

    public void setOrderDesc(String orderDesc) {
        this.orderDesc = orderDesc;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getMainOrderNo() {
        return mainOrderNo;
    }

    public void setMainOrderNo(String mainOrderNo) {
        this.mainOrderNo = mainOrderNo;
    }

    public String getPayeeAccount() {
        return payeeAccount;
    }

    public void setPayeeAccount(String payeeAccount) {
        this.payeeAccount = payeeAccount;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public String getPayeeBank() {
        return payeeBank;
    }

    public void setPayeeBank(String payeeBank) {
        this.payeeBank = payeeBank;
    }

    public String getPayeeBankAddress() {
        return payeeBankAddress;
    }

    public void setPayeeBankAddress(String payeeBankAddress) {
        this.payeeBankAddress = payeeBankAddress;
    }

    public String getBankFLG() {
        return bankFLG;
    }

    public void setBankFLG(String bankFLG) {
        this.bankFLG = bankFLG;
    }

    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    public Double getChargeFee() {
        return chargeFee;
    }

    public void setChargeFee(Double chargeFee) {
        this.chargeFee = chargeFee;
    }

    public String getRuleConfig() {
        return ruleConfig;
    }

    public void setRuleConfig(String ruleConfig) {
        this.ruleConfig = ruleConfig;
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getReceiveUserType() {
		return receiveUserType;
	}

	public void setReceiveUserType(String receiveUserType) {
		this.receiveUserType = receiveUserType;
	}

	public String getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(String triggerType) {
		this.triggerType = triggerType;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public Double getTransAmt() {
		return transAmt;
	}

	public void setTransAmt(Double transAmt) {
		this.transAmt = transAmt;
	}
	
	public String getAppPayerId() {
		return appPayerId;
	}

	public void setAppPayerId(String appPayerId) {
		this.appPayerId = appPayerId;
	}

	public String getAppPayeeId() {
		return appPayeeId;
	}

	public void setAppPayeeId(String appPayeeId) {
		this.appPayeeId = appPayeeId;
	}

	public boolean isTriggerManual(){
        boolean isTrue = false ;
        if(this.triggerType.equals(OrderConstants.TriggerTypeEnum.TRIGGER_MANUAL.val)){
            isTrue = true ;
        }
        return isTrue ;
    }
	
}
