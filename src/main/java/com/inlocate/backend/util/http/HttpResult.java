package com.inlocate.backend.util.http;

import org.apache.http.HttpStatus;

public class HttpResult {
	private boolean success;
	private String response;
	private String msg;
	private int status;
	
	public HttpResult(boolean success, String response, String msg){
		this.success = success;
		this.response = response;
		this.msg = msg;
	}
	
	public HttpResult(boolean success, String response, String msg, int status){
		this.success = success;
		this.response = response;
		this.msg = msg;
		this.status = status;
	}
	
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public static HttpResult getSuccessReturn(String result) {
		return new HttpResult(true, result, null, HttpStatus.SC_OK);
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public static HttpResult getFailure(String msg) {
		return new HttpResult(false, null, msg);
	}
	
	public static HttpResult getFailure(String msg, int status) {
		return new HttpResult(false, null, msg, status);
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}	
}
