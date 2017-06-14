package com.huinong.truffle.payment.order.mono.domain;

import java.io.Serializable;

/**
 * 用于退款买卖家收款卡信息
 * @author peng
 *
 */
public class ReceiptCard implements Serializable {

	private static final long serialVersionUID = 1L;
	private String receiptAccount ;
	private String receiptName; 
	private String receiptBank ;
	private String receiptBankAddress ;
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
