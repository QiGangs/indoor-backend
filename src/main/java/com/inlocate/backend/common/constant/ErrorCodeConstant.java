package com.inlocate.backend.common.constant;

import org.apache.commons.collections.map.UnmodifiableMap;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Iceberg
 * @Date 2018年1月25日下午3:26:29
 * @Description
 */
public abstract class ErrorCodeConstant {

	/** common *****/
	public static final String CODE_400 = "400";
	public static final String CODE_401 = "401";
	public static final String CODE_403 = "403";
	public static final String CODE_404 = "404";

	public static final String AUTH_CODE_INVALID = "100";
	public static final String REQUESTURL_ERROR = "101";
	public static final String DATA_ERROR = "201";
	public static final String DATA_NOTEXISTS = "202";
	public static final String DATA_SIGN_ERROR = "203";

	/** logic *****/
	public static final String APIUSER_NOTEXISTS = "1001";
	public static final String USER_NOTEXISTS = "1002";
	public static final String MEMBER_NOTEXISTS = "1003";
	public static final String APIUSER_LOCKED = "1004";
	public static final String API_SIGN_ERROR = "1005";
	public static final String MEMBER_INVALID = "1006";
	public static final String SMS_CODE_ERROR = "1007";
	public static final String CREAT_MEMBER_ERROR = "1008";
	public static final String NICKNAME_TOO_LONG = "1009";

	public static final String PARAMS_ERROR = "301";
	public static final String PARAMS_NOTEXISTS = "302";
	public static final String PARAMS_INVALID_DATE = "310";
	public static final String PARAMS_INVALID_TIME = "311";

	public static final String MAIL_NOTEXISTS = "2001";

	private static final Map<String, String> ERRORCODE_MAP;
	static {
		Map<String, String> tmp = new HashMap<String, String>();
		tmp.put(CODE_400, "未知错误");
		tmp.put(CODE_401, "该接口已关闭");
		tmp.put(CODE_403, "权限不足403");
		tmp.put(CODE_404, "找不到目标404");
		tmp.put(REQUESTURL_ERROR, "请求路径异常");
		tmp.put(DATA_ERROR, "数据错误");
		tmp.put(DATA_NOTEXISTS, "数据不存在");
		tmp.put(DATA_SIGN_ERROR, "数据签名错误");
		tmp.put(PARAMS_ERROR, "参数错误");
		tmp.put(PARAMS_NOTEXISTS, "缺少参数");
		tmp.put(PARAMS_INVALID_DATE, "日期参数不在有效范围");
		tmp.put(PARAMS_INVALID_TIME, "时间参数不在有效范围");
		tmp.put(SMS_CODE_ERROR, "短信验证码错误");
		tmp.put(CREAT_MEMBER_ERROR, "创建用户时发生错误");
		tmp.put(NICKNAME_TOO_LONG, "用户名过长");

		tmp.put(MAIL_NOTEXISTS, "邮件不存在");

		ERRORCODE_MAP = UnmodifiableMap.decorate(tmp);
	}

	public static String getCodeMsg(String code) {
		if (ERRORCODE_MAP.containsKey(code)) {
			return ERRORCODE_MAP.get(code);
		}
		return "UNKNOWN ERROR";
	}
}
