package com.huinong.truffle.payment.order.mono.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 用于退款买卖家收款卡信息
 * @author peng
 *
 */
@ApiModel(value="ReceiptCard" ,description="收款卡信息对象")
public class ReceiptCard implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="账号",required=false)
	private String receiptAccount ;
	@ApiModelProperty(value="名称",required=false)
	private String receiptName;
	@ApiModelProperty(value="收款银行",required=false)
	private String receiptBank ;
	@ApiModelProperty(value="开户行地址",required=false)
	private String receiptBankAddress ;
	@ApiModelProperty(value="招商卡标志(Y-是N-否)",required=false)
	private String bankFLG ;
	public String getReceiptAccount() {
		return receiptAccount;
	}
	public void setReceiptAccount(String receiptAccount) {
		this.receiptAccount = receiptAccount;
	}
	public String getReceiptName() {
		return receiptName;
	}
	public void setReceiptName(String receiptName) {
		this.receiptName = receiptName;
	}
	public String getReceiptBank() {
		return receiptBank;
	}
	public void setReceiptBank(String receiptBank) {
		this.receiptBank = receiptBank;
	}
	public String getReceiptBankAddress() {
		return receiptBankAddress;
	}
	public void setReceiptBankAddress(String receiptBankAddress) {
		this.receiptBankAddress = receiptBankAddress;
	}
	public String getBankFLG() {
		return bankFLG;
	}
	public void setBankFLG(String bankFLG) {
		this.bankFLG = bankFLG;
	}
}
