package com.inlocate.backend.util;

import com.inlocate.backend.common.support.ErrorCode;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;

import java.io.UnsupportedEncodingException;
import java.rmi.server.UID;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	public static boolean regMatch(String src, String reg, boolean ignoreCase) {
		Pattern pattern = null;
		if (ignoreCase) {
			pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
		} else {
			pattern = Pattern.compile(reg);
		}
		Matcher matcher = pattern.matcher(src);
		return matcher.find();
	}

	public static String md5(String text) {
		return md5(text, "utf-8");
	}

	public static String md5(String text, String encoding) {
		return encryptPassword(text, encoding, "md5");
	}

	public static String md5(String text, int length) {
		String result = md5(text);
		if (result.length() > length) {
			result = result.substring(0, length);
		}
		return result;
	}

	public static String sha(String text, String encoding) {
		return encryptPassword(text, encoding, "sha");
	}

	private static String encryptPassword(String password, String encoding, String algorithm) {
		try {
			byte[] unencodedPassword = password.getBytes(encoding);
			return encryptPassword(unencodedPassword, algorithm);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	private static String encryptPassword(byte[] unencodedPassword, String algorithm) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (Exception e) {
			return null;
		}
		md.reset();
		md.update(unencodedPassword);
		byte[] encodedPassword = md.digest();
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < encodedPassword.length; i++) {
			if ((encodedPassword[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString(encodedPassword[i] & 0xff, 16));
		}
		return buf.toString();
	}

	public static String getUID() {
		String UID = new UID().toString().replace(':', '_').replace('-', '_');
		return "s" + UID;
	}

	public static String getRandomString(int length) {
		return getRandomString(length, true, true, true);
	}

	public static final String upper = "ABCDEFGHJKLMNPQRSTUVWXYZ";// O,I去掉
	public static final String lower = "abcdefghijkmnpqrstuvwxyz";// o,l去掉
	public static final String digital = "23456789"; // 0,1

	public static String getRandomString(int length, boolean includeUpper, boolean includeLower, boolean includeDigital) {
		if (length > 100) {
			length = 100;
		}
		String s = "";
		if (includeUpper) {
			s += upper;
		}
		if (includeLower) {
			s += lower;
		}
		if (includeDigital) {
			s += digital;
		}
		if (length > 100) {
			throw new IllegalArgumentException("生成的字符串长度必须<100！");
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(s.charAt(RandomUtils.nextInt(s.length())));
		}
		return sb.toString();
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
	public static String signMD5(TreeMap<String, String> param, String secretCode) {
		return DigestUtils.md5Hex(signStr(param, secretCode)).toUpperCase();
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
	public static String signMD5(Map<String, String> params, String secretCode) {
		if (params == null || params.isEmpty()) {
			return "";
		}
		if (params instanceof java.util.TreeMap) {
			return signMD5(params, secretCode);
		} else {
			TreeMap<String, String> treeMap = new TreeMap<String, String>();
			treeMap.putAll(params);
			return signMD5(treeMap, secretCode);
		}
	}

	public static String signStr(TreeMap<String, String> param, String secretCode) {
		StringBuilder orgin = new StringBuilder();
		String value = "";
		for (String name : param.keySet()) {
			// 参与签名的值不包括参数中的签名值和签名方法
			if (!StringUtils.equalsIgnoreCase(name, "sign") && !StringUtils.equalsIgnoreCase(name, "signmethod")) {
				value = param.get(name);
				if (StringUtils.isEmpty(value)) {
					value = "";
				}
				orgin.append(name).append("=").append(value).append("&");
			}
		}
		return StringUtils.substringBeforeLast(orgin.toString(), "&") + secretCode;
	}

	public static String getRandomNum(int len) {
		Random rand = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			sb.append(rand.nextInt(10));
		}
		return sb.toString();
	}

	public static String wrapAndReplace(String content, String split, String[] replace) {
		String[] splitarrs = StringUtils.split(content, split);
		StringBuilder sb = new StringBuilder();
		for (String str : splitarrs) {
			sb.append(replace[0]);
			sb.append(str);
			sb.append(replace[1]);
		}
		return sb.toString();
	}

	public static ErrorCode validatePwdPattern(String password) {
		if (!Pattern.matches("[a-zA-Z0-9]{6,12}", password) || Pattern.matches("[^\\d]|[^\\D]+", password)) {
			return ErrorCode.getFailure("113", "密码必须是6-12位英文字母及数字的组合");
		}
		return ErrorCode.SUCCESS;
	}

	public static void main(String[] args) {
		boolean result = Pattern.matches("((\\d)|([a-z])|([A-Z]))+", "111");
		boolean unmatch = !"wwwww".matches("(\\w)(\\1)+");
		boolean c = "111aa11".matches("[^\\d]|[^\\D]+");
		System.out.println(c);
		System.out.println(result);
		System.out.println(unmatch);

		System.out.println(validatePwdPattern("pk123456").isSuccess());
	}

}
