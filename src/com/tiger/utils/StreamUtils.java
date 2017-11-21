package com.tiger.utils;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

/**
 * @author LeBron
 *
 */
public class StreamUtils {
	
	private static Logger logger = Logger.getLogger(StreamUtils.class);
	
	/**
	 * 关闭流
	 * @param os
	 * @param is
	 */
	public static void closeStreams(OutputStream os, InputStream is) {
		closeStream(os);
		closeStream(is);
	}
	
	/**
	 * 关闭流
	 * @param iss
	 */
	public static void closeStreams(InputStream... iss) {
		if(iss != null && iss.length > 0) {
			for (InputStream is : iss) {
				closeStream(is);
			}
		}
	}
	
	/**
	 * 关闭流
	 * @param oss
	 */
	public static void closeStreams(OutputStream... oss) {
		if(oss != null && oss.length > 0) {
			for (OutputStream os : oss) {
				closeStream(os);
			}
		}
	}
	
	/**
	 * 关闭流
	 * @param is
	 */
	public static void closeStream(InputStream is) {
		if(is != null) {
			try {
				is.close();
			} catch(Exception e) {
				logger.error("关闭输入流异常: ", e);
			}
		}
	}
	
	/**
	 * 关闭流
	 * @param os
	 */
	public static void closeStream(OutputStream os) {
		if(os != null) {
			try {
				os.close();
			} catch(Exception e) {
				logger.error("关闭输出流异常: ", e);
			}
		}
	}
	
	public static void close(Closeable closeable) {
		if(closeable != null) {
			try {
				closeable.close();
			} catch(Exception e) {
				logger.error("关闭输入流异常: ", e);
			}
		}
	}

}
