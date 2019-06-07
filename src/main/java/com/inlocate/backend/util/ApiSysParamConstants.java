package com.inlocate.backend.util;

public interface ApiSysParamConstants {
	
	/**
	 *  客户端IP
	 * */
	String CLIENT_REQUEST_IP = "client_request_ip";
	String SERVER_REQUEST_IP = "server_request_ip";
	/**
	 * 签名值
	 */
	String SIGN = "sign";
	/**
	 * 签名方法
	 */
	String SIGNMETHOD = "signmethod";
	
	String SIGNTYPE = "sign_type";
	
	/**
	 * API平台分配给consumer的appkey的参数名称
	 */
	String APPKEY = "appkey";
	
	/**
	 * 时间戳
	 */
	String TIMESTAMP = "timestamp";
	
	/**
	 * api版本号
	 */
	String V = "v";
	
	/**
	 * api响应结果格式
	 */
	String FORMAT = "format";
	
	/**
	 * consumer请求的调用方法
	 */
	String METHOD = "method";
}
