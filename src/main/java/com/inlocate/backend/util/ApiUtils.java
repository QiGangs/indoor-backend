package com.inlocate.backend.util;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

public abstract class ApiUtils {

	private ApiUtils() {
	}

	/**
	 * 给请求签名。
	 * 
	 * @param params
	 *            所有字符型的请求参数
	 * @param secret
	 *            签名密钥
	 * @return 签名
	 * @throws IOException
	 */
	public static String signRequest(Map<String, String> params, String secret) throws IOException {
		
		// 第一步：把字典按Key的字母顺序排序
		Map<String, String> sortedParams = new TreeMap<String, String>(params);

		// 第二步：把所有参数名和参数值串在一起
		StringBuilder orgin = new StringBuilder();
		for(Map.Entry<String, String> entry:sortedParams.entrySet()){
			orgin.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		String query=StringUtils.substringBeforeLast(orgin.toString(), "&") + secret;
		
		// 第三步：使用MD5加密
		MessageDigest md5 = getMd5MessageDigest();
		byte[] bytes = md5.digest(query.getBytes("UTF-8"));

		// 第四步：把二进制转化为大写的十六进制
		StringBuilder sign = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				sign.append("0");
			}
			sign.append(hex.toUpperCase());
		}

		return sign.toString().toUpperCase();
	}

	private static MessageDigest getMd5MessageDigest() throws IOException {
		try {
			return MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IOException(e.getMessage());
		}
	}
	

	

}
