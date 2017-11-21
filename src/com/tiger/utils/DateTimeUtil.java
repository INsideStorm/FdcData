package com.tiger.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author LeBron
 *
 */
public class DateTimeUtil {
	
	private static Logger logger = Logger.getLogger(DateTimeUtil.class);
	
//	/**
//	 * 日期格式化 (yyyy-MM-dd HH:mm:ss)
//	 */
//	private static final SimpleDateFormat NORMAL_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	
//	/**
//	 * 日期格式化 (yyyyMMddHHmmss)
//	 */
//	private static final SimpleDateFormat COMPACT_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
//	
//	/**
//	 * 日期格式化 (yyyyMMdd)
//	 */
//	private static final SimpleDateFormat COMPACT_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
//	
//	/**
//	 * 日期格式化 (yyyy-MM-dd)
//	 */
//	private static final SimpleDateFormat SHORT_NORMAL_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	private static final ThreadLocal<SimpleDateFormat> NOR_FORMAT_T_LOCAL = new ThreadLocal<>();
	private static final ThreadLocal<SimpleDateFormat> COMPACT_FORMAT_T_LOCAL = new ThreadLocal<>();
	private static final ThreadLocal<SimpleDateFormat> COMPACT_DATE_FORMAT_T_LOCAL = new ThreadLocal<>();
	private static final ThreadLocal<SimpleDateFormat> COMPACT_DATEHOUR_FORMAT_T_LOCAL = new ThreadLocal<>();
	private static final ThreadLocal<SimpleDateFormat> SHORT_NORMAL_FORMAT_T_LOCAL = new ThreadLocal<>();
	
	/**
	 * 日期格式化 (yyyy-MM-dd HH:mm:ss)
	 * @return
	 */
	public static SimpleDateFormat getNormalSafeFormater() {
		SimpleDateFormat format = NOR_FORMAT_T_LOCAL.get();
		if(format == null) {
			NOR_FORMAT_T_LOCAL.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		}
		return NOR_FORMAT_T_LOCAL.get();
	}
	/**
	 * 日期格式化 (yyyyMMddHHmmss)
	 * @return
	 */
	public static SimpleDateFormat getCompactSafeFormater() {
		SimpleDateFormat format = COMPACT_FORMAT_T_LOCAL.get();
		if(format == null) {
			COMPACT_FORMAT_T_LOCAL.set(new SimpleDateFormat("yyyyMMddHHmmss"));
		}
		return COMPACT_FORMAT_T_LOCAL.get();
	}
	/**
	 * 日期格式化 (yyyyMMdd)
	 * @return
	 */
	public static SimpleDateFormat getCompactDateSafeFormater() {
		SimpleDateFormat format = COMPACT_DATE_FORMAT_T_LOCAL.get();
		if(format == null) {
			COMPACT_DATE_FORMAT_T_LOCAL.set(new SimpleDateFormat("yyyyMMdd"));
		}
		return COMPACT_DATE_FORMAT_T_LOCAL.get();
	}
	/**
	 * 日期格式化 (yyyyMMddHH)
	 * @return
	 */
	public static SimpleDateFormat getCompactDateHourSafeFormater() {
		SimpleDateFormat format = COMPACT_DATEHOUR_FORMAT_T_LOCAL.get();
		if(format == null) {
			COMPACT_DATEHOUR_FORMAT_T_LOCAL.set(new SimpleDateFormat("yyyyMMddHH"));
		}
		return COMPACT_DATEHOUR_FORMAT_T_LOCAL.get();
	}
	/**
	 * 日期格式化 (yyyy-MM-dd)
	 * @return
	 */
	public static SimpleDateFormat getShortNorSafeFormater() {
		SimpleDateFormat format = SHORT_NORMAL_FORMAT_T_LOCAL.get();
		if(format == null) {
			SHORT_NORMAL_FORMAT_T_LOCAL.set(new SimpleDateFormat("yyyy-MM-dd"));
		}
		return SHORT_NORMAL_FORMAT_T_LOCAL.get();
	}
	
	
	public static SimpleDateFormat getNormalFormat() {
		return getNormalSafeFormater();
	}
	
	/**
	 * 日期转字符串(yyyy-MM-dd HH:mm:ss)
	 * @param date
	 * @return
	 */
	public static String format(Date date) {
		if(date == null) {
			return null;
		}
		return getNormalSafeFormater().format(date);
	}
	
	/**
	 * 字符串转日期
	 * @param dateTimeStr	yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static Date parseNormal(String dateTimeStr) {
		return parse(getNormalSafeFormater(), dateTimeStr);
	}
	
	/**
	 * 字符串转日期
	 * @param dateTimeStr	yyyyMMddHHmmss
	 * @return
	 */
	public static Date parseCompact(String dateTimeStr) {
		return parse(getCompactSafeFormater(), dateTimeStr);
	}
	
	/**
	 * 
	 * @param format
	 * @param str
	 * @return
	 */
	private static Date parse(SimpleDateFormat format, String str) {
		try {
			if(format == null|| str == null || "null".equals(str) || "".equals(str)) {
				return null;
			}
			return format.parse(str);
		} catch(Exception e) {
			logger.error("日期转换异常 ["+str+"]: ", e);
		}
		return null;
	}
	
	/**
	 * 获取当前日期	(yyyyMMdd)
	 * @return
	 */
	public static String getCurrentCompactDate() {
		return getCompactDateSafeFormater().format(new Date());
	}
	
	/**
	 * 获取当前日期	(yyyyMMddHH)
	 * @return
	 */
	public static String getCurrentCompactHourDate() {
		return getCompactDateHourSafeFormater().format(new Date());
	}
	
	/**
	 * 获取当前日期	(yyyy-MM-dd)
	 * @return
	 */
	public static String getCurrentShortNormalDate() {
		return getShortNorSafeFormater().format(new Date());
	}
	
	/**
	 * 比较两个日期是否同一天
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static boolean compareDateEqYMD(Date d1, Date d2) {
		if(d1 == null || d2 == null) {
			return false;
		}
		Calendar c1 = Calendar.getInstance();
		c1.setTime(d1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);
		
		return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) 
				&& c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) 
				&& c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * 获取下一天日期信息
	 * @param dateString	[yyyyMMdd]
	 * @return
	 */
	public static String getNextDate(String dateString) {
		if(dateString == null || !dateString.matches("^\\d{8}$")) {
			logger.error("Input error string.");
			return null;
		}
		
		try {
			SimpleDateFormat format = getCompactDateSafeFormater();
			Date curDate = format.parse(dateString);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(curDate);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			return format.format(calendar.getTime());
		} catch(Exception e) {
			logger.error("", e);
		}
		return null;
	}

}
