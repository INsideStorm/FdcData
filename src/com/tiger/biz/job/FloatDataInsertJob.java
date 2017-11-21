package com.tiger.biz.job;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import javax.activation.DataSource;
import javax.xml.crypto.Data;

import org.apache.log4j.Logger;

import com.tiger.biz.service.FloatDataService;
import com.tiger.utils.DBUtil;
import com.tiger.utils.ThreadUtil;

public class FloatDataInsertJob implements Runnable{

	private Logger logger = Logger.getLogger(getClass());
	
	private ArrayList<String> dataStrings = new ArrayList<>();
	
	public FloatDataInsertJob(ArrayList<String> arrayList) {
		this.dataStrings=arrayList;
	}

	@Override
	public void run() {
		
//		while(true){
			final CountDownLatch latch = new CountDownLatch(dataStrings.size());
			for(final String str:dataStrings){
				ThreadUtil.runWithThreadPool(new Runnable() {
					
					@Override
					public void run() {
						String[] datas = str.split(",");
						FloatDataService.addIntoOracle(datas);
						latch.countDown();
					}
				});
			}
				
			
//		}
	}

}
