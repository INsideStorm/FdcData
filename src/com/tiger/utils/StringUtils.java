package com.tiger.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;

/**
 * @author LeBron
 *
 */
public class StringUtils {
	
	private static Logger logger = Logger.getLogger(StringUtils.class);
	
	private StringUtils() {
	}
	
	/**
	 * 判断是否为为空
	 * @param string
	 * @return
	 */
	public static boolean isBlank(String string) {
		return string == null || string.trim().isEmpty();
	}
	
	/**
	 * 判断是否非空
	 * @param string
	 * @return
	 */
	public static boolean isNotBlank(String string) {
		return !isBlank(string);
	}
	

	/**
	 * 判断字符是否和数组里的值有相等的
	 * @param str
	 * @param arr
	 * @return
	 */
	public static boolean arrayEq(String str, String... arr) {
		if(str == null || arr == null || arr.length == 0) {
			return false;
		}
		for (String s : arr) {
			if(str.equals(s)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断字符是否以数组里的值有结束的
	 * @param str
	 * @param arr
	 * @return
	 */
	public static boolean arrayEndsWith(String str, String... arr) {
		if(str == null || arr == null || arr.length == 0) {
			return false;
		}
		for (String s : arr) {
			if(str.endsWith(s)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断字符是否以数组里的值开始的
	 * @param str
	 * @param arr
	 * @return
	 */
	public static boolean arrayStartsWith(String str, String... arr) {
		if(str == null || arr == null || arr.length == 0) {
			return false;
		}
		for (String s : arr) {
			if(str.startsWith(s)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 字符前或后追加一定长度字符
	 * @param str
	 * @param chr
	 * @param isPrefix
	 * @param length
	 * @return
	 */
	public static String wrap(String str, char chr, boolean isPrefix, int length) {
		StringBuilder builder = new StringBuilder();
		if(str == null) {
			str = "";
		}
		if(length - str.length() > 0) {
			if(!isPrefix) {
				builder.append(str);
			}
			for (int i = 0; i < length - str.length(); i++) {
				builder.append(chr);
			}
			if(isPrefix) {
				builder.append(str);
			}
		}
		return builder.toString();
	}
	
	
	
	/******************************************************************************************************/
	
	private static ThreadLocal<Base64> base64ThreadLocal = new ThreadLocal<>();
	private static Object objLock = new Object();
	private static Base64 base64 = new Base64();
	
	private static Base64 getSafeBase64() {
		Base64 base64 = base64ThreadLocal.get();
		if(base64 == null) {
			synchronized (objLock) {
				base64 = new Base64();
				base64ThreadLocal.set(base64);
				return base64;
			}
		}
		return base64;
	}
	
	
	public static String toBase64String(InputStream is) {
		if(is == null) {
			return null;
		}
//		return getSafeBase64().encodeToString(getByte(is));
		byte[] bytes = getByte(is);
		if(bytes == null) {
			return null;
		}
		return Base64.encodeBase64String(bytes);
	}
	
	public static byte[] base64To(String base64String) {
		if(isBlank(base64String)) {
			return null;
		}
		try {
			
			return Base64.decodeBase64(base64String);
		} catch(Exception e) {
			logger.error("", e);
			return null;
		}
	}
	
	public static String getBase64(InputStream in) {
		try {
			
			return base64ToStr(getByte(in));
		} catch(Exception e) {
			logger.error("", e);
			return null;
		}
	}
	
	public static String encodeUTF8(String xmlDoc) {
		String str = "";
		try {
			xmlDoc = transNull(xmlDoc);
			if (!"".equals(xmlDoc)) {
				str = URLEncoder.encode(xmlDoc, "utf-8");
			}
			return str;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("", ex);
		}
		return str;
	}
	
	public static String decodeUTF8(String xmlDoc) {
		String str = "";
		try {
			xmlDoc = transNull(xmlDoc);
			if(!"".equals(xmlDoc)){
				str = URLDecoder.decode(xmlDoc, "utf-8");
			}			
			return str;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("", ex);
		}
		return str;
	}

	public static String transNull(String str) {

		String result = str;
		if (null == str || "".equals(str) || "null".equalsIgnoreCase(str)) {
			result = "";
		}
		return result;
	}
	
	public static byte[] strToBase64(String content) throws IOException {
		if (null == content) {
			return null;
		}
		return new BASE64Decoder().decodeBuffer(content.trim());
	}

	public static String base64ToStr(byte[] bytes) throws IOException {
		String content = "";
		content = new sun.misc.BASE64Encoder().encode(bytes);
		return content;
	}

	public static byte[] getByte(InputStream in) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			copy(in, out);
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("", e);
		} finally {
			try {
				out.close();
			} catch(Exception e) {
			}
		}
		return null;

	}
	
	/**
	 * 流复制
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void copy(InputStream in, OutputStream out) throws IOException {
		if(in == null || out == null) {
			return;
		}
		try {
			byte[] buffer = new byte[4096];
			int nrOfBytes = -1;
			while ((nrOfBytes = in.read(buffer)) != -1) {
				out.write(buffer, 0, nrOfBytes);
			}
			out.flush();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
			}
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException ex) {
			}
		}
	}

}
