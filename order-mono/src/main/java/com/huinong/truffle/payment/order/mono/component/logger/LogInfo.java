package com.huinong.truffle.payment.order.mono.component.logger;

import java.io.Serializable;

public class LogInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long id ;
	
	private String logTime;
	
	private String logFullClass;
	
	private String logLevel;
	
	private String message;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLogTime() {
		return logTime;
	}

	public void setLogTime(String logTime) {
		this.logTime = logTime;
	}

	public String getLogFullClass() {
		return logFullClass;
	}

	public void setLogFullClass(String logFullClass) {
		this.logFullClass = logFullClass;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
