package com.tiger.biz.common;

import java.io.File;
import java.util.ResourceBundle;

import com.tiger.utils.FileUtils;


/**
 * 常量类
 * @author xu
 *
 */
public final class PubConstants {
	
	private static ResourceBundle app_bundle;
	static {
		app_bundle = ResourceBundle.getBundle("app");
	}
	
	/**
	 * 监控目录
	 */
	public final static String MONITOR_ROOTPATH = app_bundle.getString("monitor.rootpath");
	
	public final static File MONITOR_FILE = new File(MONITOR_ROOTPATH);
	
	/**
	 * 解压缩目录
	 */
	public final static File DECOMPRESS_DIR = FileUtils.ensureDirs(MONITOR_FILE.getParentFile(), "KK_DECOMPRESS");
	public final static File DECOMPRESS_ERR_DIR = FileUtils.ensureDirs(MONITOR_FILE.getParentFile(), "KK_DECOMPRESS_ERR");
	
	/**
	 * 文件解析失败根路径
	 */
	public final static String ANALYSIS_ERR_PATH = app_bundle.getString("analysis.err.rootpath");
	
	/**
	 * 发送失败目录
	 */
	public final static String SEND_ERR_PATH = app_bundle.getString("send.err.rootpath");
	
	public final static String RMINF_URL = app_bundle.getString("ws.rminf.url");
	
	/**
	 * 缉查布控接口WSDL
	 */
	public final static String JC_JK_WSDL = RMINF_URL + "/services/Trans?wsdl";
	
	/**
	 * 接口序列号
	 */
	public final static String WS_INVOKE_JKXLH = app_bundle.getString("ws.jkxlh");
	
	// 
	public final static String WS_INVOKE_IP		= app_bundle.getString("ws.invoke.ip");
	public final static String WS_INVOKE_PORT	= app_bundle.getString("ws.invoke.port");
	public final static String WS_INVOKE_JKID	= app_bundle.getString("ws.invoke.jkid");
	
	// 图片生成根目录
	public final static String PICSHARED_PATH	= app_bundle.getString("pic.shared.path");

}
