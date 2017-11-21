package com.tiger.biz.job;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.activation.DataSource;

import org.apache.log4j.Logger;

import com.tiger.biz.common.PubConstants;
import com.tiger.utils.ThreadUtil;
/**
 * 
 * @author hehaolong
 *
 */
public class RunStart {
	
	private static Logger logger = Logger.getLogger(RunStart.class);
	
	public static void main(String[] args) {
//		String[] teStrings={
//				"20170719,165549,XJ,新1234,87.607712,42.775537,0,207,1,1",
//				"20170720,165549,XJ,新5678,87.607712,42.775537,0,207,1,1",
//				"20170720,165549,XJ,新7890,87.607712,42.775537,0,207,1,1"};
		
		logger.error("浮动车数据入库开始");
		ArrayList<String> arrayList = new ArrayList<>();
		arrayList = TelnetGetJob.getData();
		ThreadUtil.runWithNewThread(new FloatDataInsertJob(arrayList));
		
	}

}
