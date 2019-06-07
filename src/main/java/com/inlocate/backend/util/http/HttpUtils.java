package com.inlocate.backend.util.http;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpUtils {
	private static final transient Logger logger = LoggerFactory.getLogger(HttpUtils.class);
	static {
		SSLUtilities.trustAllHostnames();
		SSLUtilities.trustAllHttpsCertificates();
	}
	public static final int DEFAULT_TIMEOUT = 60 * 1000;
	public static final int SHORT_TIMEOUT = 30 * 1000;
	public static final int LONG_TIMEOUT = 120 * 1000;
	public static final int CONNECTION_TIMEOUT = 20 * 1000;
	public static final int EXCEPTION_HTTP_STATUSCODE = 9999;

	public static final String mobileUserAgent = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7A341 Safari/528.16";

	public HttpUtils() {
	}

	public static String getRemoteIp(HttpServletRequest request) {
		String xfwd = request.getHeader("X-Forwarded-For");
		if (StringUtils.isNotBlank(xfwd)) {
			String[] ipList = xfwd.split(",");
			for (String ip : ipList) {
				ip = StringUtils.trim(ip);
				if (!ip.equals("127.0.0.1") && !ip.equals("localhost")) return ip;
			}
		}
		return request.getRemoteAddr();
	}

	public static HttpResult getUrlAsString(String url) {
		HttpGet httpGet = getHttpGet(url, null, null);
		HttpResult result = executeHttpRequest(httpGet, null, DEFAULT_TIMEOUT, "utf-8");
		return result;
	}

	public static HttpResult getMobileUrlAsString(String url) {
		HttpGet httpGet = getHttpGet(url, null, null);
		Map<String, String> reqheader = new HashMap<String, String>();
		reqheader.put("User-Agent", mobileUserAgent);
		return executeHttpRequest(httpGet, reqheader, DEFAULT_TIMEOUT, "utf-8");
	}

	public static HttpResult getUrlAsString(String url, Map<String, String> params) {
		HttpGet httpGet = getHttpGet(url, params, "utf-8");
		HttpResult result = executeHttpRequest(httpGet, null, DEFAULT_TIMEOUT, "utf-8");
		return result;
	}

	public static HttpResult getUrlAsString(String url, Map<String, String> params, Map<String, String> reqHeader) {
		HttpGet httpGet = getHttpGet(url, params, "utf-8");
		return executeHttpRequest(httpGet, reqHeader, DEFAULT_TIMEOUT, "utf-8");
	}

	public static HttpResult getUrlAsString(String url, Map<String, String> params, boolean isstrict) {
		HttpGet httpGet = getHttpGet(url, params, isstrict, "utf-8");
		HttpResult result = executeHttpRequest(httpGet, null, DEFAULT_TIMEOUT, "utf-8");
		return result;
	}

	public static HttpResult getUrlAsString(String url, Map<String, String> params, int timeoutMills) {
		HttpGet httpGet = getHttpGet(url, params, "utf-8");
		HttpResult result = executeHttpRequest(httpGet, null, timeoutMills, "utf-8");
		return result;
	}

	public static HttpResult getUrlAsString(String url, Map<String, String> params, BasicClientCookie cookie) {
		HttpGet httpGet = getHttpGet(url, params, "utf-8");
		HttpResult result = executeHttpRequest(httpGet, null, cookie, DEFAULT_TIMEOUT, "utf-8");
		return result;
	}

	public static HttpResult getUrlAsString(String url, Map<String, String> params, String encode) {
		HttpGet httpGet = getHttpGet(url, params, encode);
		HttpResult result = executeHttpRequest(httpGet, null, DEFAULT_TIMEOUT, encode);
		return result;
	}

	public static boolean getUrlAsInputStream(String url, Map<String, String> params, RequestCallback callback) {
		HttpGet httpGet = getHttpGet(url, params, "utf-8");
		return executeHttpRequest(httpGet, null, null, callback, DEFAULT_TIMEOUT);
	}

	public static boolean getUrlAsInputStream(String url, Map<String, String> params, RequestCallback callback, String encode) {
		HttpGet httpGet = getHttpGet(url, params, encode);
		return executeHttpRequest(httpGet, null, null, callback, DEFAULT_TIMEOUT);
	}

	public static HttpResult postBodyAsString(String url, String body) {
		return postBodyAsString2(url, body, "UTF-8");
	}

	public static HttpResult postBodyAsString2(String url, String body, String encode) {
		HttpPost httpPost = getHttpPost(url, body, encode);
		return executeHttpRequest(httpPost, null, DEFAULT_TIMEOUT, encode);
	}

	public static HttpResult postBodyAsString2(String url, String body, String encode, Map<String, String> header) {
		HttpPost httpPost = getHttpPost(url, body, encode);
		return executeHttpRequest(httpPost, header, DEFAULT_TIMEOUT, encode);
	}

	public static HttpResult postBodyAsString(String url, String body, int timeoutMills) {
		return postBodyAsString2(url, body, "UTF-8", timeoutMills);
	}

	public static HttpResult postBodyAsString2(String url, String body, String encode, int timeoutMills) {
		HttpPost httpPost = getHttpPost(url, body, encode);
		return executeHttpRequest(httpPost, null, timeoutMills, encode);
	}

	public static HttpResult postUrlAsString(String url, Map<String, String> params) {
		HttpPost httpPost = getHttpPost(url, params, "UTF-8");
		return executeHttpRequest(httpPost, null, DEFAULT_TIMEOUT, "utf-8");
	}

	public static HttpResult postUrlAsString(String url, Map<String, String> params, int timeoutMills) {
		HttpPost httpPost = getHttpPost(url, params, "UTF-8");
		return executeHttpRequest(httpPost, null, timeoutMills, "utf-8");
	}

	public static HttpResult postUrlAsString(String url, Map<String, String> params, Map<String, String> reqHeader, String encode) {
		HttpPost httpPost = getHttpPost(url, params, encode);
		return executeHttpRequest(httpPost, reqHeader, null, DEFAULT_TIMEOUT, encode);
	}

	public static boolean postUrlAsInputStream(String url, Map<String, String> params, RequestCallback callback) {
		HttpPost httpPost = getHttpPost(url, params, "UTF-8");
		return executeHttpRequest(httpPost, null, null, callback, DEFAULT_TIMEOUT);
	}

	public static HttpResult uploadFile(String url, Map<String, String> params, InputStream is, String inputName, String fileName) {
		Map<String, InputStream> uploadMap = new HashMap<String, InputStream>();
		uploadMap.put(inputName, is);
		Map<String, String> fileNameMap = new HashMap<String, String>();
		fileNameMap.put(inputName, fileName);
		return uploadFile(url, params, uploadMap, fileNameMap);
	}

	public static HttpResult uploadFile(String url, Map<String, String> params, Map<String, InputStream> uploadMap, Map<String, String> fileNameMap) {
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setIntParameter("http.socket.timeout", LONG_TIMEOUT);
		client.getParams().setBooleanParameter("http.protocol.expect-continue", false);
		client.getParams().setIntParameter("http.connection.timeout", CONNECTION_TIMEOUT);

		HttpPost request = new HttpPost(url);
		MultipartEntity reqEntity = new MultipartEntity();
		for (String input : uploadMap.keySet()) {
			InputStreamBody isb = new InputStreamBody(uploadMap.get(input), fileNameMap.get(input));
			reqEntity.addPart(input, isb);
		}
		try {
			if (params != null && !params.isEmpty()) {
				for (String key : params.keySet()) {
					StringBody param = new StringBody(params.get(key));
					reqEntity.addPart(key, param);
				}
			}
			request.setEntity(reqEntity);
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String result = EntityUtils.toString(response.getEntity(), "utf-8");
				return HttpResult.getSuccessReturn(result);
			} else {
				int statusCode = response.getStatusLine().getStatusCode();
				String msg = "httpStatus:" + statusCode + response.getStatusLine().getReasonPhrase() + ", Header: ";
				Header[] headers = response.getAllHeaders();
				for (Header header : headers) {
					msg += header.getName() + ":" + header.getValue();
				}
				logger.error("ERROR HttpUtils:" + msg + request.getURI());
				return HttpResult.getFailure("httpStatus:" + response.getStatusLine().getStatusCode(), statusCode);
			}
		} catch (Exception e) {
			logger.error(request.getURI() + ":" + e.getMessage());
			return HttpResult.getFailure(request.getURI() + " exception:" + e.getClass().getCanonicalName(), EXCEPTION_HTTP_STATUSCODE);
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	private static HttpPost getHttpPost(String url, Map<String, String> params, String encoding) {
		HttpPost httpPost = new HttpPost(url);
		if (params != null) {
			List<NameValuePair> form = new ArrayList<NameValuePair>();
			for (String name : params.keySet()) {
				form.add(new BasicNameValuePair(name, params.get(name)));
			}
			try {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, encoding);
				httpPost.setEntity(entity);
			} catch (UnsupportedEncodingException e) {
				logger.error("", e);
			}
		}
		return httpPost;
	}

	private static HttpPost getHttpPost(String url, String body, String encoding) {
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Accept-Encoding", "gzip,deflate");
		if (body != null) {

			try {
				HttpEntity entity = new StringEntity(body, encoding);
				httpPost.setEntity(entity);
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		return httpPost;
	}

	private static HttpGet getHttpGet(String url, Map<String, String> params, boolean isstrict, String encode) {
		if (params != null) {
			if (url.indexOf('?') == -1)
				url += "?";
			else
				url += "&";
			for (String name : params.keySet()) {
				try {
					if (!isstrict || StringUtils.isNotBlank(params.get(name))) url += name + "=" + URLEncoder.encode(params.get(name), encode) + "&";
				} catch (UnsupportedEncodingException e) {
				}
			}
			url = url.substring(0, url.length() - 1);
		}
		HttpGet httpGet = new HttpGet(url);
		return httpGet;
	}

	private static HttpGet getHttpGet(String url, Map<String, String> params, String encode) {
		return getHttpGet(url, params, true, encode);
	}

	private static HttpResult executeHttpRequest(HttpUriRequest request, Map<String, String> reqHeader, int timeoutMills, String charset) {
		return executeHttpRequest(request, reqHeader, null, timeoutMills, charset);
	}

	private static HttpResult executeHttpRequest(HttpUriRequest request, Map<String, String> reqHeader, BasicClientCookie cookie, int timeoutMills, String charset) {
		DefaultHttpClient client = new DefaultHttpClient();
		if (cookie != null) {
			CookieStore cookieStore = new BasicCookieStore();
			cookieStore.addCookie(cookie);
			client.setCookieStore(cookieStore);
		}
		client.getParams().setIntParameter("http.socket.timeout", timeoutMills);
		client.getParams().setIntParameter("http.connection.timeout", CONNECTION_TIMEOUT);
		client.getParams().setBooleanParameter("http.protocol.expect-continue", false);
		if (reqHeader != null) {
			for (String name : reqHeader.keySet()) {
				request.addHeader(name, reqHeader.get(name));
			}
		}
		try {
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String result = EntityUtils.toString(response.getEntity(), charset);
				return HttpResult.getSuccessReturn(result);
			} else {
				int statusCode = response.getStatusLine().getStatusCode();
				String msg = "httpStatus:" + statusCode + response.getStatusLine().getReasonPhrase() + ", Header: ";
				Header[] headers = response.getAllHeaders();
				for (Header header : headers) {
					msg += header.getName() + ":" + header.getValue();
				}
				logger.error("ERROR HttpUtils:" + msg + request.getURI());
				return HttpResult.getFailure("httpStatus:" + response.getStatusLine().getStatusCode(), statusCode);
			}
		} catch (Exception e) {
			logger.error(request.getURI() + ":" + e.getMessage());
			return HttpResult.getFailure(request.getURI() + " exception:" + e.getClass().getCanonicalName(), EXCEPTION_HTTP_STATUSCODE);
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	private static boolean executeHttpRequest(HttpUriRequest request, Map<String, String> reqHeader, BasicClientCookie cookie, RequestCallback callback, int timeoutMills) {
		DefaultHttpClient client = new DefaultHttpClient();
		if (cookie != null) {
			CookieStore cookieStore = new BasicCookieStore();
			cookieStore.addCookie(cookie);
			client.setCookieStore(cookieStore);
		}
		client.getParams().setIntParameter("http.socket.timeout", timeoutMills);
		client.getParams().setBooleanParameter("http.protocol.expect-continue", false);
		client.getParams().setIntParameter("http.connection.timeout", CONNECTION_TIMEOUT);
		if (reqHeader != null) {
			for (String name : reqHeader.keySet()) {
				request.addHeader(name, reqHeader.get(name));
			}
		}
		try {
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return callback.processResult(response.getEntity().getContent());
			} else {
				String msg = "httpStatus:" + response.getStatusLine().getStatusCode() + response.getStatusLine().getReasonPhrase() + ", Header: ";
				Header[] headers = response.getAllHeaders();
				for (Header header : headers) {
					msg += header.getName() + ":" + header.getValue();
				}
				logger.error("ERROR HttpUtils:" + msg + request.getURI());
			}
		} catch (Exception e) {
			logger.error(request.getURI() + ":" + e.getMessage());
		} finally {
			client.getConnectionManager().shutdown();
		}
		return false;
	}

	public static class FileRequestCallback implements RequestCallback {
		private File file;

		public FileRequestCallback(File file) {
			this.file = file;
		}

		@Override
		public boolean processResult(InputStream stream) {
			OutputStream os = null;
			try {
				os = new FileOutputStream(file);
				IOUtils.copy(stream, os);
				os.close();
				return true;
			} catch (Exception e) {
				logger.error("", e);
			} finally {
				try {
					if (os != null) os.close();
				} catch (Exception e) {
				}
			}
			return false;
		}
	}

	private static Pattern QUERY_MAP_PATTERN = Pattern.compile("&?([^=&]+)=");

	public static Map<String, String> parseQueryStr(String queryString, String encode) {
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
					logger.error("", e);
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
				logger.error("", e);
			}
		}
		return map;
	}

	public static String formatQueryStr(String url, Map<String, String> params, String encode) {
		if (!params.isEmpty()) {
			if (url.indexOf('?') == -1)
				url += "?";
			else
				url += "&";
			for (String name : params.keySet()) {
				try {
					if (StringUtils.isNotBlank(params.get(name))) url += name + "=" + URLEncoder.encode(params.get(name), encode) + "&";
				} catch (UnsupportedEncodingException e) {
				}
			}
			url = url.substring(0, url.length() - 1);
		}
		return url;
	}

	public static String formatQueryStr(String url, Map<String, String> params) {
		return formatQueryStr(url, params, "UTF-8");
	}
}
