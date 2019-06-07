package com.inlocate.backend.util.http;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class WebUtils {
	public static final boolean isLocalIp(String ip) {
		return ip.contains("192.168.") || ip.equals("127.0.0.1") || ip.equals("10.0.");
	}

	public static String getRemoteIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (StringUtils.isNotBlank(ip)) {
			String[] ipList = ip.split(",");
			for (String i : ipList) {
				i = StringUtils.trim(i);
				if (!i.equals("127.0.0.1") && !i.equals("localhost")) return i;
			}
		}
		return ip;
	}

	public static final String getParamStr(HttpServletRequest request, String spliter) {
		String paramsStr = "";
		List<String> params = new ArrayList<String>(request.getParameterMap().keySet());
		Collections.sort(params);
		for (String pname : params) {
			paramsStr += pname + "=" + request.getParameter(pname) + spliter;
		}
		return paramsStr;
	}

	public static final String getEncodeParamStr(HttpServletRequest request, String spliter, boolean encode) {
		String paramsStr = "";
		List<String> params = new ArrayList<String>(request.getParameterMap().keySet());
		Collections.sort(params);

		for (String pname : params) {
			String value = "";
			if (StringUtils.containsIgnoreCase(pname, "pass")) {
				value = "****";
			} else {
				value = request.getParameter(pname);
			}
			paramsStr += (encode ? encodeName(pname) : pname) + "=" + value + spliter;
		}
		return paramsStr;
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

	public static final String getHeaderStr(HttpServletRequest request, String spliter, boolean encode) {
		String result = "", tmpname;
		for (Enumeration names = request.getHeaderNames(); names.hasMoreElements();) {
			tmpname = (String) names.nextElement();
			result += (encode ? encodeName(tmpname) : tmpname) + "=" + request.getHeader(tmpname) + spliter;
		}
		return result;
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

	public static final Map<String, String> getRequestMap(HttpServletRequest request, String excludeParam) {
		Map<String, String> result = new HashMap<String, String>();
		Enumeration<String> it = request.getParameterNames();
		String key = null;
		while (it.hasMoreElements()) {
			key = it.nextElement();
			if (!StringUtils.equals(excludeParam, key)) result.put(key, request.getParameter(key));
		}
		return result;
	}

	public static final Map getHeaderMap(HttpServletRequest request) {
		Map<String, String> result = new HashMap<String, String>();
		Enumeration<String> it = request.getHeaderNames();
		String key = null;
		while (it.hasMoreElements()) {
			key = it.nextElement();
			result.put(key, request.getHeader(key));
		}
		return result;
	}

	private static String encodeName(String name) {
		return "<span style='color:red'>" + name + "</span>";
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

	public static final void addCookie(HttpServletResponse response, String cookiename, String cookievalue, String path, int maxSecond) {
		Cookie cookie = new Cookie(cookiename, cookievalue);
		cookie.setPath(path);
		cookie.setMaxAge(maxSecond);// 24 hour
		response.addCookie(cookie);
	}

	public static final Cookie getCookie(HttpServletRequest request, String cookiename) {
		Cookie cookies[] = request.getCookies();
		if (cookies == null) return null;
		for (Cookie cookie : cookies) {
			if (cookiename.equals(cookie.getName())) {
				return cookie;
			}
		}
		return null;
	}

	public static final String getCookieValue(HttpServletRequest request, String cookiename) {
		Cookie cookie = getCookie(request, cookiename);
		if (cookie == null) return null;
		return cookie.getValue();
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
		if (StringUtils.isBlank(str)) return false;
		if (StringUtils.contains(StringUtils.lowerCase(str), "<script")) return true;//
		if (StringUtils.contains(StringUtils.lowerCase(str), "<iframe")) return true;//
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

	public static final void removeSensitiveInfo(Map<String, String> params) {
		for (String key : new ArrayList<String>(params.keySet())) {
			if (StringUtils.contains(key, "mobile") || StringUtils.contains(key, "pass")) {
				params.put(key, "***");
			}
		}
	}

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
	 * ��ѯ����ȡ
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
					e.printStackTrace();
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
				e.printStackTrace();
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
	 * ��ֵ�á�,���Ÿ�
	 * 
	 * @param requestMap
	 * @param encode
	 * @return
	 */
	public static final String getQueryStr(Map<String, String> requestMap, String encode) {
		if (requestMap == null || requestMap.isEmpty()) return "";
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
		if (!StringUtils.endsWith(contextPath, "/")) contextPath += "/";
		return contextPath;
	}

	public static String encode(String str) {
		return urlEncode(str, "utf-8");
	}

	public static String decode(String str) {
		return urlDecode(str, "utf-8");
	}

	public static String urlDecode(String str, String encode) {
		String tmp = "";
		try {
			tmp = URLDecoder.decode(str, encode);
		} catch (Exception e) {
		}
		return tmp;
	}

	public static String urlEncode(String str, String encode) {
		String tmp = "";
		try {
			tmp = URLEncoder.encode(str, encode);
		} catch (Exception e) {
		}
		return tmp;
	}
}
