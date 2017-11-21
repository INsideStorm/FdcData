package com.tiger.biz.service;

import org.apache.log4j.Logger;

import com.tiger.utils.DBUtil;
/**
 * 
 * @author hehaolong
 *
 */
public class FloatDataService {
	private static Logger logger = Logger.getLogger(FloatDataService.class);

	//插入数据库的操作
	public static void addIntoOracle(String[] datas) {
		
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO TM_FDC (TMPK,RQ,SJ,BZ,CLBZ,JD,WD,SD,FXJ,ZZZT,SFYX) VALUES ( ");
		sql.append("FDC_TMPK_SEQ.nextval,");
		for(int i=0;i<datas.length;i++){
			if(i==datas.length-1){
				sql.append("'"+datas[i]+"'");
			}
			else{
				sql.append("'"+datas[i]+"',");
			}
		}
		sql.append(")");
		DBUtil.executeUpdate("bdb", sql.toString(), null);
		System.out.println("数据入库");
	}
	
}
