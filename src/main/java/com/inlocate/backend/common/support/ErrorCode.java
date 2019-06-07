package com.inlocate.backend.common.support;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Iceberg
 * @Date 2018年1月25日下午3:26:44
 * @Description
 */
public class ErrorCode<T> implements Serializable {
	public static final String CODE_SUCCESS = "0000";
	public static final String CODE_ERROR_UNKNOWN = "9999";
	private static final long serialVersionUID = 4418416282894231647L;

	private String errcode;
	private String msg;
	private T retval;
	private boolean success = false;

	private ErrorCode(String code, String msg, T retval) {
		this.errcode = code;
		this.msg = msg;
		this.retval = retval;
		this.success = StringUtils.equals(code, CODE_SUCCESS);
	}

	public static ErrorCode SUCCESS = new ErrorCode(CODE_SUCCESS, "操作成功！", null);
	public static ErrorCode NOT_LOGIN = new ErrorCode(CODE_ERROR_UNKNOWN, "您还没有登录，请先登录！", null);
	public static ErrorCode NORIGHTS = new ErrorCode(CODE_ERROR_UNKNOWN, "您没有权限！", null);
	public static ErrorCode REPEATED = new ErrorCode(CODE_ERROR_UNKNOWN, "不能重复操作！", null);
	public static ErrorCode NOT_FOUND = new ErrorCode(CODE_ERROR_UNKNOWN, "未找到相关数据！", null);
	public static ErrorCode DATAERROR = new ErrorCode(CODE_ERROR_UNKNOWN, "数据有错误！", null);

	@Override
	public boolean equals(Object another) {
		if (another == null || !(another instanceof ErrorCode)) {
			return false;
		}
		return this.errcode == ((ErrorCode) another).errcode;
	}

	@Override
	public int hashCode() {
		return (this.success + this.errcode + this.msg).hashCode();
	}

	public boolean isSuccess() {
		return success;
	}

	public static ErrorCode getFailure(String msg) {
		return new ErrorCode(CODE_ERROR_UNKNOWN, msg, null);
	}

	public static ErrorCode getFailure(String code, String msg) {
		return new ErrorCode(code, msg, null);
	}

	public static ErrorCode getSuccess(String msg) {
		return new ErrorCode(CODE_SUCCESS, msg, null);
	}

	public static <T> ErrorCode<T> getSuccessReturn(T retval) {
		return new ErrorCode(CODE_SUCCESS, null, retval);
	}

	public static ErrorCode getSuccessMap() {
		return new ErrorCode(CODE_SUCCESS, null, new HashMap());
	}

	public static <T> ErrorCode getFailureReturn(T retval) {
		return new ErrorCode(CODE_ERROR_UNKNOWN, null, retval);
	}

	public static ErrorCode getFullErrorCode(String code, String msg, Object retval) {
		return new ErrorCode(code, msg, retval);
	}

	public T getRetval() {
		return retval;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void setRetval(T retval) {
		this.retval = retval;
	}

	public String getMsg() {
		return msg;
	}

	public void put(Object key, Object value) {
		((Map) retval).put(key, value);
	}

	public String getErrcode() {
		return errcode;
	}
}
