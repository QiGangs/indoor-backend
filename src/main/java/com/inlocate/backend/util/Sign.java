package com.inlocate.backend.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.*;
import java.security.Key;
import java.security.KeyFactory;
import java.security.SignatureException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.TreeMap;

/**
 * 签名类
 * 
 * @author quzhuping
 * 
 */
public class Sign {
	
	private static final transient Logger dbLogger = LoggerFactory.getLogger(Sign.class);

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
		if (charset == null || "".equals(charset)) { return content.getBytes(); }
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
		if (params == null || params.isEmpty()) return "";
		if (params instanceof java.util.TreeMap) {
			return signMD5(params, secretCode);
		} else {
			TreeMap<String, String> treeMap = new TreeMap<String, String>();
			treeMap.putAll(params);
			return signMD5(treeMap, secretCode);
		}
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
	public static String signStr(TreeMap<String, String> param, String secretCode) {
		StringBuilder orgin = new StringBuilder();
		String value = "";
		for (String name : param.keySet()) {
			// 参与签名的值不包括参数中的签名值和签名方法
			if (!StringUtils.equalsIgnoreCase(name, ApiSysParamConstants.SIGN) && !StringUtils.equalsIgnoreCase(name, ApiSysParamConstants.SIGNMETHOD) && !StringUtils.equalsIgnoreCase(name, ApiSysParamConstants.CLIENT_REQUEST_IP)) {
				value = param.get(name);
				if (StringUtils.isEmpty(value)) {
					value = "";
				}
				orgin.append(name).append("=").append(value).append("&");
			}
		}
		return StringUtils.substringBeforeLast(orgin.toString(), "&") + secretCode;
	}

	public static String encryptRSA(String content, String publicKey) {
		try {
			byte[] data = content.getBytes();
			byte[] keyBytes = Base64Utils.decode(publicKey);
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			Key publicK = keyFactory.generatePublic(x509KeySpec);
			// 对数据加密
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, publicK);
			int inputLen = data.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段加密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > 117) {
					cache = cipher.doFinal(data, offSet, 117);
				} else {
					cache = cipher.doFinal(data, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * 117;
			}
			byte[] encryptedData = out.toByteArray();
			return Base64Utils.encode(encryptedData);
		} catch (Exception e) {
			dbLogger.warn(e.getMessage());
			return null;
		}
	}
}


class Base64Utils {

   /**
    * 文件读取缓冲区大小
    */
   private static final int CACHE_SIZE = 1024;
   
   /**
    * <p>
    * BASE64字符串解码为二进制数据
    * </p>
    * 
    * @param base64
    * @return
    * @throws Exception
    */
   public static byte[] decode(String base64) throws Exception {
       return Base64.decodeBase64(base64.getBytes());
   }
   
   /**
    * <p>
    * 二进制数据编码为BASE64字符串
    * </p>
    * 
    * @param bytes
    * @return
    * @throws Exception
    */
   public static String encode(byte[] bytes) throws Exception {
       return new String(Base64.encodeBase64(bytes));
   }
   
   /**
    * <p>
    * 将文件编码为BASE64字符串
    * </p>
    * <p>
    * 大文件慎用，可能会导致内存溢出
    * </p>
    * 
    * @param filePath 文件绝对路径
    * @return
    * @throws Exception
    */
   public static String encodeFile(String filePath) throws Exception {
       byte[] bytes = fileToByte(filePath);
       return encode(bytes);
   }
   
   /**
    * <p>
    * BASE64字符串转回文件
    * </p>
    * 
    * @param filePath 文件绝对路径
    * @param base64 编码字符串
    * @throws Exception
    */
   public static void decodeToFile(String filePath, String base64) throws Exception {
       byte[] bytes = decode(base64);
       byteArrayToFile(bytes, filePath);
   }
   
   /**
    * <p>
    * 文件转换为二进制数组
    * </p>
    * 
    * @param filePath 文件路径
    * @return
    * @throws Exception
    */
   public static byte[] fileToByte(String filePath) throws Exception {
       byte[] data = new byte[0];
       File file = new File(filePath);
       if (file.exists()) {
           FileInputStream in = new FileInputStream(file);
           ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
           byte[] cache = new byte[CACHE_SIZE];
           int nRead = 0;
           while ((nRead = in.read(cache)) != -1) {
               out.write(cache, 0, nRead);
               out.flush();
           }
           out.close();
           in.close();
           data = out.toByteArray();
        }
       return data;
   }
   
   /**
    * <p>
    * 二进制数据写文件
    * </p>
    * 
    * @param bytes 二进制数据
    * @param filePath 文件生成目录
    */
   public static void byteArrayToFile(byte[] bytes, String filePath) throws Exception {
       InputStream in = new ByteArrayInputStream(bytes);
       File destFile = new File(filePath);
       if (!destFile.getParentFile().exists()) {
           destFile.getParentFile().mkdirs();
       }
       destFile.createNewFile();
       OutputStream out = new FileOutputStream(destFile);
       byte[] cache = new byte[CACHE_SIZE];
       int nRead = 0;
       while ((nRead = in.read(cache)) != -1) {   
           out.write(cache, 0, nRead);
           out.flush();
       }
       out.close();
       in.close();
   }
}
