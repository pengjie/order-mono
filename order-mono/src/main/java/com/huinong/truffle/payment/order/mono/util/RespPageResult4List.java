package com.huinong.truffle.payment.order.mono.util;

import java.util.List;

public class RespPageResult4List<T>  extends RespPageResult<T>{
	

	private static final long serialVersionUID = 4362392863984314667L;

	public RespPageResult4List() {
	}
	
	private List<T> data ;

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}
	
	

}
