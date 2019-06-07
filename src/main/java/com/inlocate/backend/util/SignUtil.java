package com.inlocate.backend.util;

import com.google.common.collect.Lists;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 签名类
 * @author quzhuping
 *
 */

/**
 * @function
 * @author kyori
 * @date 2015-04-26 16:49:59
 */
public class SignUtil {

	/**
	 * 对字符串进行MD5签名
	 * 
	 * @param text
	 *           明文
	 * @param inputCharset
	 *           编码格式 UTF-8或GBK
	 * @return 密文,32位16进制小写字符串
	 */
	public static String md5Hex(String text, String inputCharset) {
		return DigestUtils.md5Hex(getContentBytes(text, inputCharset));
	}

	/**
	 * @param content
	 * @param charset
	 * @return
	 * @throws SignatureException
	 * @throws UnsupportedEncodingException
	 */
	private static byte[] getContentBytes(String content, String charset) {
		if (charset == null || "".equals(charset)) {
			return content.getBytes();
		}
		try {
			return content.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
		}
	}

	/**
	 * md5签名方法
	 * 
	 * @param srcStr
	 * @return 返回32位16进制大写字符串
	 */
	public static String md5(String srcStr) {
		return DigestUtils.md5Hex(srcStr).toUpperCase();
	}

	/**
	 * 对请求参数集进行MD5签名
	 * 
	 * @param param
	 *           待签名的请求参数集
	 * @param secretCode
	 *           签名密码
	 * @return 返回32位16进制大写字符串
	 */
	public static String signMD5(TreeMap param, String secretCode) {
		return DigestUtils.md5Hex(signStr(param, secretCode)).toUpperCase();
	}

	public static String rsa(String content, String privateKey) {
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");
			signature.initSign(priKey);
			signature.update(content.getBytes("UTF-8"));

			byte[] signed = signature.sign();
			return Base64.encodeBase64String(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 对请求参数集进行MD5签名
	 * 
	 * @param params
	 *           待签名的请求参数集
	 * @param secretCode
	 *           签名密码
	 * @return 返回null 或 32位16进制大写字符串
	 */
	public static String signMD5(Map params, String secretCode) {
		if (params == null || params.isEmpty()) return "";
		TreeMap treeMap = new TreeMap();
		treeMap.putAll(params);
		return signMD5(treeMap, secretCode);
	}

	/**
	 * 将请求参数按key=value&key=valuesecretCode拼接 <br/>排除key为sign和signmethod的key-value
	 * 
	 * @param param
	 *           请求参数
	 * @param secretCode
	 *           签名密码
	 * @return
	 */
	public static String signStr(TreeMap<String, Object> param, String secretCode) {
		StringBuilder orgin = new StringBuilder();
		String value = "";
		for (String name : param.keySet()) {
			// 参与签名的值不包括参数中的签名值和签名方法
			if (!StringUtils.equalsIgnoreCase(name, ApiSysParamConstants.SIGN) && !StringUtils.equalsIgnoreCase(name, ApiSysParamConstants.SIGNMETHOD) && !StringUtils.equalsIgnoreCase(name, ApiSysParamConstants.SIGNTYPE)) {
				value = "" + param.get(name);
				if (StringUtils.isEmpty(value)) {
					value = "";
				}
				orgin.append(name).append("=").append(value).append("&");
			}
		}
		return StringUtils.substringBeforeLast(orgin.toString(), "&") + secretCode;
	}
	
	

	/**
	 * 微信开发者验证
	 */
	public static String weixinverify(String[] params) {
		if (params == null || params.length < 1)
			return null;
		Arrays.sort(params);
		String tmpStr = StringUtils.join(params);
		return StringUtil.sha(tmpStr, "UTF-8");
	}

	/**
	 * 微信签名
	 */
	public static String weixinsignature(Map<String, String> paramsMap) {
		if (paramsMap == null || paramsMap.isEmpty())
			return null;

		String[] keys = paramsMap.keySet().toArray(new String[0]);
		Arrays.sort(keys);
		List<String> query = Lists.newArrayList();
		for (String key : keys) {
			query.add(key + "=" + paramsMap.get(key));
		}
		String tmpStr = StringUtils.join(query, "&");
		return StringUtil.sha(tmpStr, "UTF-8");
	}

}
