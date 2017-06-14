package com.huinong.truffle.payment.order.mono.util;

import java.io.Serializable;

public class RespPageResult<T> implements Serializable{
	
	private static final String STATUS_SUCCESS = "SUCCESS"	;
	
	private static final String STATUS_FAIL = "FAIL"	;
	
	private static final String MESSAGE = "系统异常，请稍后再试！"	;
	
	private PageInfo pageInfo ;
	
	private String status ;
	
	private String message ;
	
	public RespPageResult(){
		this.status = STATUS_FAIL;
		this.message = MESSAGE;
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6076045853296291449L;
	
	public class PageInfo implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private Integer currPageNo;
		
		private Integer pageSize ;
		
		private Integer totalRecord;
		
		private Integer lastPageNo;
		
		private Integer nextPageNo;
		
		private Integer prevPageNo;

		public Integer getCurrPageNo() {
			return currPageNo;
		}

		public void setCurrPageNo(Integer currPageNo) {
			this.currPageNo = currPageNo;
		}

		public Integer getPageSize() {
			return pageSize;
		}

		public void setPageSize(Integer pageSize) {
			this.pageSize = pageSize;
		}

		public Integer getTotalRecord() {
			return totalRecord;
		}

		public void setTotalRecord(Integer totalRecord) {
			this.totalRecord = totalRecord;
		}

		public Integer getLastPageNo() {
			return lastPageNo;
		}

		public void setLastPageNo(Integer lastPageNo) {
			this.lastPageNo = lastPageNo;
		}

		public Integer getNextPageNo() {
			return nextPageNo;
		}

		public void setNextPageNo(Integer nextPageNo) {
			this.nextPageNo = nextPageNo;
		}

		public Integer getPrevPageNo() {
			return prevPageNo;
		}

		public void setPrevPageNo(Integer prevPageNo) {
			this.prevPageNo = prevPageNo;
		}

	}
	
	/**
	 * 判定结果是否正常返回结果
	 * @return
	 */
	public boolean isSuccess(){
		return STATUS_SUCCESS.equals(this.status);
	}
	
	/**
	 * 判定结果是否为异常结果
	 * @return
	 */
	public boolean isFailure(){
		return STATUS_FAIL.equals(this.status);
	}

}
