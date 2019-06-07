package com.inlocate.backend.util.http;

import com.inlocate.backend.util.StringUtil;
import com.inlocate.backend.util.json.JsonUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseWebUtils {
	protected final static transient Logger dbLogger = LoggerFactory.getLogger(BaseWebUtils.class);

	public static final String getRemoteIp(HttpServletRequest request) {
		String xfwd = request.getHeader("X-Forwarded-For");
		String gewaip = null;
		if (StringUtils.isNotBlank(xfwd)) {
			String[] ipList = xfwd.split(",");
			for (int i = ipList.length - 1; i >= 0; i--) {
				String ip = ipList[i];
				ip = StringUtils.trim(ip);
				if (!ip.equals("127.0.0.1") && !ip.equals("localhost")) {
					return ip;
				}
			}
		}
		if (gewaip != null) return gewaip;
		return request.getRemoteAddr();
	}

	public static final boolean isLocalRequest(HttpServletRequest request) {
		String ip = getRemoteIp(request);
		return isLocalIp(ip);
	}

	public static final boolean isLocalIp(String ip) {
		return ip.contains("192.168.") || ip.equals("127.0.0.1"); // 本地
	}

	public static final void writeJsonResponse(HttpServletResponse res, boolean success, String retval) {
		res.setContentType("application/json;charset=utf-8");
		res.setCharacterEncoding("utf-8");
		try {
			PrintWriter writer = res.getWriter();
			Map jsonMap = new HashMap();
			jsonMap.put("success", success);
			if (!success) {
				jsonMap.put("msg", retval);
			} else {
				jsonMap.put("retval", retval);
			}
			writer.write("var data=" + JsonUtils.writeObjectToJson(jsonMap));
			res.flushBuffer();
		} catch (IOException e) {
		}
	}

	public static final String getAttributeStr(HttpServletRequest request, String spliter) {
		String paramsStr = "";
		String tmpname;
		Enumeration params = request.getAttributeNames();
		while (params.hasMoreElements()) {
			tmpname = (String) params.nextElement();
			paramsStr += tmpname + "=" + request.getAttribute(tmpname) + spliter;
		}
		return paramsStr;
	}

	public static final String getHeaderStr(HttpServletRequest request) {
		return "" + getHeaderMap(request);
	}

	public static final Map<String, String> getRequestMap(HttpServletRequest request) {
		Map<String, String> result = new HashMap<String, String>();
		Enumeration<String> it = request.getParameterNames();
		String key = null;
		while (it.hasMoreElements()) {
			key = it.nextElement();
			result.put(key, request.getParameter(key));
		}
		return result;
	}

	public static final Map<String, String> getHeaderMap(HttpServletRequest request) {
		Map<String, String> result = new HashMap<String, String>();
		Enumeration<String> it = request.getHeaderNames();
		String key = null;
		while (it.hasMoreElements()) {
			key = it.nextElement();
			String value = request.getHeader(key);
			result.put(key, value);
		}
		return result;
	}

	/**
	 * 返回Map，但key=“head4”+originalKey
	 * 
	 * @param request
	 * @return
	 */
	public static final Map<String, String> getHeaderMapWidthPreKey(HttpServletRequest request) {
		Map<String, String> result = new HashMap<String, String>();
		Enumeration<String> it = request.getHeaderNames();
		String key = null;
		while (it.hasMoreElements()) {
			key = it.nextElement();
			String value = request.getHeader(key);
			// 禁止cookie日志打印
			if (StringUtils.containsIgnoreCase(key, "cookie")) {
				value = "*******";
			}
			result.put("head4" + key, value);
		}
		return result;

	}

	public static final void clearCookie(HttpServletResponse response, String path, String cookieName) {
		Cookie cookie = new Cookie(cookieName, null);
		cookie.setMaxAge(0);
		cookie.setPath(path);
		response.addCookie(cookie);
	}

	public static final boolean isRobot(String userAgent) {
		return StringUtils.containsIgnoreCase(userAgent, "spider") || StringUtils.containsIgnoreCase(userAgent, "Googlebot") || StringUtils.containsIgnoreCase(userAgent, "robot");
	}

	public static final boolean isAjaxRequest(HttpServletRequest request) {
		boolean result = StringUtils.isNotBlank(request.getHeader("X-Requested-With"));
		return result;
	}

	public static final void addCookie(HttpServletResponse response, String cookiename, String cookievalue, String path, int maxSecond) {
		Cookie cookie = new Cookie(cookiename, cookievalue);
		cookie.setPath(path);
		cookie.setMaxAge(maxSecond);// 24 hour
		response.addCookie(cookie);
	}

	public static final Cookie getCookie(HttpServletRequest request, String cookiename) {
		Cookie cookies[] = request.getCookies();
		if (cookies == null) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if (cookiename.equals(cookie.getName())) {
				return cookie;
			}
		}
		return null;
	}

	public static final String getCookieValue(HttpServletRequest request, String cookiename) {
		Cookie cookie = getCookie(request, cookiename);
		if (cookie == null) {
			return null;
		}return cookie.getValue();
	}

	public static final String joinParams(Map params, boolean ignoreBlank) {
		StringBuilder content = new StringBuilder();
		List<String> keys = new ArrayList(params.keySet());
		Collections.sort(keys);
		for (String key : keys) {
			Object value = params.get(key);
			if (!ignoreBlank || value != null && StringUtils.isNotBlank("" + value)) content.append(key + "=" + value + "&");
		}
		if (content.length() > 0) content.deleteCharAt(content.length() - 1);
		return content.toString();
	}

	public static final boolean checkString(String str) {
		if (StringUtils.isBlank(str)) {
			return false;
		}
		if (StringUtils.contains(StringUtils.lowerCase(str), "<script")) {
			return true;// 验证JS
		}
		if (StringUtils.contains(StringUtils.lowerCase(str), "<iframe")) {
			return true;// 验证iframe
		}
		return false;
	}

	public static final boolean checkPropertyAll(Object entity) {
		try {
			Map result = PropertyUtils.describe(entity);
			for (Object key : result.keySet()) {
				if (result.get(key) instanceof String) {
					if (checkString(result.get(key) + "")) {
						return true;
					}
				}
			}
		} catch (Exception ex) {
		}
		return false;
	}

	public static final Map<String, String> getRequestParams(HttpServletRequest request, String... pnames) {
		Map<String, String> result = new TreeMap<String, String>();
		if (pnames != null) {
			for (String pn : pnames) {
				String pv = request.getParameter(pn);
				if (StringUtils.isNotBlank(pv)) result.put(pn, pv);
			}
		}
		return result;
	}

	/**
	 * 判断用户浏览器信息
	 */
	public static final String getBrowerInfo(String userAgent) {
		String browserInfo = "UNKNOWN";
		String info = StringUtils.lowerCase(userAgent);
		try {
			String[] strInfo = info.substring(info.indexOf("(") + 1, info.indexOf(")") - 1).split(";");
			if ((info.indexOf("msie")) > -1) {
				return strInfo[1].trim();
			} else {
				String[] str = info.split(" ");
				if (info.indexOf("navigator") < 0 && info.indexOf("firefox") > -1) {
					return str[str.length - 1].trim();
				} else if ((info.indexOf("opera")) > -1) {
					return str[0].trim();
				} else if (info.indexOf("chrome") < 0 && info.indexOf("safari") > -1) {
					return str[str.length - 1].trim();
				} else if (info.indexOf("chrome") > -1) {
					return str[str.length - 2].trim();
				} else if (info.indexOf("navigator") > -1) {
					return str[str.length - 1].trim();
				}
			}
		} catch (Exception e) {
		}
		return browserInfo;
	}

	/**
	 * 查询串提取
	 * 
	 * @param queryStr
	 * @param encode
	 * @return
	 */
	private static Pattern QUERY_MAP_PATTERN = Pattern.compile("&?([^=&]+)=");

	/**
	 * @param queryString
	 *           encoded queryString queryString is already encoded (e.g %20 and &
	 *           may be present)
	 * @param encode
	 * @return
	 */
	public static final Map<String, String> parseQueryStr(String queryString, String encode) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		if (StringUtils.isBlank(queryString)) return map;
		Matcher matcher = QUERY_MAP_PATTERN.matcher(queryString);
		String key = null, value;
		int end = 0;
		while (matcher.find()) {
			if (key != null) {
				try {
					value = queryString.substring(end, matcher.start());
					if (StringUtils.isNotBlank(value)) {
						value = URLDecoder.decode(value, encode);
						map.put(key, value);
					}
				} catch (UnsupportedEncodingException e) {
					dbLogger.error("", e);
				}
			}
			key = matcher.group(1);
			end = matcher.end();
		}
		if (key != null) {
			try {
				value = queryString.substring(end);
				if (StringUtils.isNotBlank(value)) {
					value = URLDecoder.decode(value, encode);
					map.put(key, value);
				}
			} catch (UnsupportedEncodingException e) {
				dbLogger.error("", e);
			}
		}
		return map;
	}

	public static final String getQueryStr(HttpServletRequest request, String encode) {
		return getQueryStr(flatRequestMap(request.getParameterMap(), ","), encode);
	}

	public static final Map<String, String> flatRequestMap(Map<String, String[]> reqMap, String joinChar) {
		Map<String, String> flatMap = new HashMap<String, String>();
		for (String key : reqMap.keySet()) {
			flatMap.put(key, StringUtils.join(reqMap.get(key), joinChar));
		}
		return flatMap;
	}

	/**
	 * 多值用“,”号隔开
	 * 
	 * @param requestMap
	 * @param encode
	 * @return
	 */
	public static final String getQueryStr(Map<String, String> requestMap, String encode) {
		if (requestMap == null || requestMap.isEmpty()) {
			return "";
		}
		String result = "";
		for (String name : requestMap.keySet()) {
			try {
				result += name + "=" + URLEncoder.encode(requestMap.get(name), encode) + "&";
			} catch (UnsupportedEncodingException e) {
			}
		}
		return result.substring(0, result.length() - 1);
	}

	public static final String encodeParam(String params, String encode) {
		Map<String, String> paramMap = parseQueryStr(params, encode);
		String result = "";
		for (String key : paramMap.keySet()) {
			try {
				result += "&" + key + "=" + URLEncoder.encode(paramMap.get(key), encode);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if (StringUtils.isNotBlank(result)) return result.substring(1);
		return "";
	}

	public static final String getContextPath(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		if (!StringUtils.endsWith(contextPath, "/")) {
			contextPath += "/";
		}
		return contextPath;
	}

	public static final String getParamStr(HttpServletRequest request, boolean removeSensitive, String... sensitiveKeys) {
		Map<String, String> requestMap = getRequestMap(request);
		if (removeSensitive) {
			removeSensitiveInfo(requestMap, sensitiveKeys);
		}
		return "" + requestMap;
	}

	private static final List<String> DEFAULT_SENSITIVE = Arrays.asList("mobile", "pass", "sign", "encode");

	public static final void removeSensitiveInfo(Map<String, String> params, String... keys) {
		List<String> keyList = null;
		if (keys != null) {
			keyList = new ArrayList<String>(DEFAULT_SENSITIVE);
			keyList.addAll(Arrays.asList(keys));
		} else {
			keyList = DEFAULT_SENSITIVE;
		}

		for (String pname : new ArrayList<String>(params.keySet())) {
			for (String key : keyList) {
				if (StringUtils.containsIgnoreCase(pname, key) && StringUtils.isNotBlank(params.get(pname))) {
					params.put(pname, "MG" + StringUtil.md5("kcj3STidSC" + params.get(pname)));
				}
			}
		}
	}

	public static final String getRemotePort(HttpServletRequest request) {// 获取请求端口号
		String port = request.getHeader("x-client-port");
		if (StringUtils.isBlank(port)) {
			return "" + request.getRemotePort();
		}
		return port;
	}
}
