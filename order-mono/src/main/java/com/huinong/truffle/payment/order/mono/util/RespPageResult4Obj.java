package com.huinong.truffle.payment.order.mono.util;

public class RespPageResult4Obj<T> extends RespPageResult<T>{
 
	/** 
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/ 
	private static final long serialVersionUID = 7176836735330591304L;
	private T data ;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	
  
}
