package com.tiger.utils;

import org.apache.log4j.Logger;

/**
 * @author LeBron
 *
 */
public class IntegerUtils {
	
	private static Logger logger = Logger.getLogger(IntegerUtils.class);
	
	/**
	 * parse the String to Integer
	 * @param str
	 * @param defaultVal
	 * @return
	 */
	public static Integer parseInt(String str, Integer defaultVal) {
		if(str == null || "null".equalsIgnoreCase(str) || "".equals(str)) {
			return defaultVal;
		}
		try {
			return Integer.parseInt(str);
		} catch(Exception e) {
			logger.error("input string ["+str+"]", e);
			return defaultVal;
		}
	}
	
	/**
	 * default null
	 * @param str
	 * @return
	 */
	public static Integer parseInt(String str) {
		return parseInt(str, null);
	}
	
	/**
	 * parse the String to Double
	 * @param str
	 * @param defaultVal
	 * @return
	 */
	public static Double parseDouble(String str, Double defaultVal) {
		if(str == null || "null".equalsIgnoreCase(str) || "".equals(str)) {
			return defaultVal;
		}
		try {
			return Double.parseDouble(str);
		} catch(Exception e) {
			logger.error("input string ["+str+"]", e);
			return defaultVal;
		}
	}
	
	/**
	 * default null
	 * @param str
	 * @return
	 */
	public static Double parseDouble(String str) {
		return parseDouble(str, null);
	}
	
	/**
	 * @param str
	 * @param defaultVal
	 * @return
	 */
	public static Long parseLong(String str, Long defaultVal) {
		if(str == null || "null".equalsIgnoreCase(str) || "".equals(str)) {
			return defaultVal;
		}
		try {
			return Long.parseLong(str);
		} catch(Exception e) {
			logger.error("input string ["+str+"]", e);
			return defaultVal;
		}
	}
	
	/**
	 * default null
	 * @param str
	 * @return
	 */
	public static Long parseLong(String str) {
		return parseLong(str, null);
	}
	
	/**
	 * 
	 * @param str
	 * @param defaultVal
	 * @return
	 */
	public static Long parseRoundingLong(String str, Long defaultVal) {
		if(str == null || "null".equalsIgnoreCase(str) || "".equals(str)) {
			return defaultVal;
		}
		try {
			String[] arr = str.split("\\.");
			str = (arr[0]);
			long lon = Long.parseLong(str);
			if(arr.length == 2) {
				int i = parseInt(arr[1].charAt(0)+"", 0);
				if(i > 5)
					lon++;
			}
			return lon;
		} catch(Exception e) {
			logger.error("input string ["+str+"]", e);
			return defaultVal;
		}
	}
	
	public static Long parseRoundingLong(String str) {
		return parseRoundingLong(str, 0L);
	}

}
