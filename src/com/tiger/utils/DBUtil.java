package com.tiger.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * @author LeBron
 *
 */
public class DBUtil {
	
	static ResourceBundle bundle = ResourceBundle.getBundle("jdbc");
	
	
	private static Logger logger = Logger.getLogger(DBUtil.class);
	
	private static ComboPooledDataSource bdb_ds = new ComboPooledDataSource();
	private static ComboPooledDataSource mdb_ds = new ComboPooledDataSource("mdb");
	
	private static Connection getConnection(DataSource ds) {
		try {
			return ds.getConnection();
		} catch(Exception e) {
			logger.error("", e);
		}
		return null;
	}
	
	/**
	 * 必须以*.jdbc.*的属性
	 * @param prefixName
	 * @return
	 */
	public static Connection getConnection(String prefixName) {
		if("mdb".equals(prefixName)) {
			return getConnection(mdb_ds);
		} else if("bdb".equals(prefixName)) {
			return getConnection(bdb_ds);
		}
		return null;
		
//		try {
//			return getConnection(bundle.getString(prefixName + ".jdbc.driver"), 
//					bundle.getString(prefixName + ".jdbc.url"), 
//					bundle.getString(prefixName + ".jdbc.username"), 
//					bundle.getString(prefixName + ".jdbc.password"));
//		} catch(Exception e) {
//			logger.error("", e);
//			return null;
//		}
	}
	
//	public static Connection getConnection() {
//		try {
//			return getConnection(bundle.getString("jdbc.driver"), 
//					bundle.getString("jdbc.url"), 
//					bundle.getString("jdbc.username"), 
//					bundle.getString("jdbc.password"));
//		} catch(Exception e) {
//			logger.error("", e);
//			return null;
//		}
//	}
	
//	/**
//	 * 获取数据库连接对象
//	 * @return
//	 */
//	public static Connection getConnection(String driver, String url, String username, String password) {
//		
//		try {
//			Class.forName(driver);
//			return DriverManager.getConnection(url, username, password);
//		} catch(Exception e) {
//			e.printStackTrace();
//			logger.error("获取数据库连接异常: ", e);
//			filelogger.log("获取数据库连接异常: ", e);
//			return null;
//		}
//	}
	
	/**
	 * 更新数据库操作
	 * @param connPrefix
	 * @param sql
	 * @param param
	 * @return
	 */
	public static boolean executeUpdate(String connPrefix, String sql, Object[] param) {
		return executeUpdate(getConnection(connPrefix), sql, param);
	}
	
	/**
	 * 更新数据库操作
	 * @param sql
	 * @param param
	 * @return
	 */
	public static boolean executeUpdate(Connection conn, String sql, Object[] param) {
		return executeUpdate(conn, sql, param, null);
	}
	
	public static boolean executeUpdate(String connPrefix, String sql, Object[] param, File errLog) {
		return executeUpdate(getConnection(connPrefix), sql, param, errLog);
	}
	
	/**
	 * 更新数据库操作
	 * @param sql	SQL语句
	 * @param param	参数
	 * @param errLog	数据库异常导出SQL语句文件
	 * @return
	 */
	public static boolean executeUpdate(Connection conn, String sql, Object[] param, File errLog) {
		if(conn == null) {
			if(errLog != null) {
				if(errLog.getParentFile() == null || !errLog.getParentFile().exists()) {
					errLog.mkdirs();
				}
				FileUtils.writeLine(errLog, generateSql(sql, param), "UTF-8");
			}
			return false;
		}
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement(sql);
			if(param != null) {
				int i = 1;
				for (Object p : param) {
					if(p != null && p instanceof Integer) {
						pst.setInt(i, (Integer)p);
					} else {
						pst.setObject(i, p);
					}
					i++;
				}
			}
			return pst.executeUpdate() > 0;
		} catch(Exception e) {
			e.printStackTrace();
			if(errLog != null) {
				FileUtils.writeLine(errLog, generateSql(sql, param), "UTF-8");
			}
			logger.error("数据库更新异常: ", e);
		} finally {
			releaseRes(conn, pst);
		}
		return false;
	}
	
	/**
	 * 批量更新数据库操作
	 * @param sql
	 * @param params
	 * @return
	 */
	public static boolean batchUpdate(Connection conn, String sql, List<Object[]> params) {
		if(conn == null) {
			return false;
		}
		PreparedStatement pst = null;
		try {
			
			pst = conn.prepareStatement(sql);
			
			int count = 0;
			for (Object[] param : params) {
				for (int i = 0; i < param.length; i++) {
					pst.setObject(i + 1, param[i]);
				}
				count++;
				pst.addBatch();
				if(count % 400 == 0) {
					pst.executeBatch();
				}
			}
			
			if(count % 400 != 0) {
				pst.executeBatch();
			}
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			logger.error("数据库批量更新异常: ", e);
		} finally {
			releaseRes(conn, pst);
		}
		return false;
	}
	
	public static boolean batchUpdate(String connPrefix, List<String> sqls) {
		return batchUpdate(getConnection(connPrefix), sqls);
	}
	
	public static boolean batchUpdate(Connection conn, List<String> sqls) {
		if(sqls == null || sqls.isEmpty()) {
			return false;
		}
		return batchUpdate(conn, sqls.toArray(new String[sqls.size()]));
	}
	
	/**
	 * 批量更新
	 * @param sqls
	 * @return
	 */
	public static boolean batchUpdate(Connection conn, String[] sqls) {
		if(sqls == null || sqls.length == 0) {
			return false;
		}
		if(conn == null) {
			return false;
		}
		
		Statement statement = null;
		try {
			
			statement = conn.createStatement();
			int count = 0;
			for (String sql : sqls) {
				if(sql != null && !"".equals(sql.trim())) {
					statement.addBatch(sql);
					count++;
				}
				if(count % 400 == 0) {
					statement.executeBatch();
				}
			}
			if(count == 0) {
				return true;
			}
			if(count % 400 != 0) {
				statement.executeBatch();
			}
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			logger.error("数据库批量更新异常: ", e);
		} finally {
			releaseRes(conn, statement);
		}
		return false;
	}
	
	/**
	 * 生成SQL语句
	 * @param sql
	 * @param param
	 * @return
	 */
	public static String generateSql(String sql, Object[] param) {
		if(param == null || param.length == 0) {
			return sql;
		}
		for (Object p : param) {
			if(p != null && p instanceof Date) {
				p = DateTimeUtil.format((Date)p);
			}
			sql = sql.replaceFirst("\\?", p == null?"null":"'" + p.toString() + "'");
		}
		logger.debug("GEN SQL : [" + sql + "]");
		return sql;
	}
	
	public static <T> List<T> query(String connPrefix, String sql, Object[] params, Class<T> cls) throws Exception {
		return query(getConnection(connPrefix), sql, params, cls);
	}
	
	/**
	 * @param conn
	 * @param sql
	 * @param params
	 * @param cls
	 * @return
	 * @throws Exception 
	 */
	public static <T> List<T> query(Connection conn, String sql, Object[] params, Class<T> cls) throws Exception {
		Method[] methods = cls.getMethods();
		
		if(conn == null) {
			return null;
		}
		PreparedStatement pst = null;
		ResultSet rs = null;
		ResultSetMetaData meta = null;
		
		Method method = null;
		try {
			pst = conn.prepareStatement(sql);
			
			if(params != null) {
				for (int i = 0; i < params.length; i++) {
					pst.setObject(i + 1, params[i]);
				}
			}
			
			rs = pst.executeQuery();
			meta = pst.getMetaData();
			
			int colCount = meta.getColumnCount();
			List<T> datas = new ArrayList<>();
			while(rs.next()) {
				T t = cls.newInstance();
				for (int i = 0; i < colCount; i++) {
					String methodName = "set" + meta.getColumnName(i+1)/*.replace("_", "")*/;
					method = getMethod(methods, methodName);
					if(method == null) {
						method = getMethod(methods, methodName.replace("_", ""));
					}
					if(method != null) {
						Class<?> paramType = getFirstParamType(method);
						if(paramType == null) {
							continue;
						}
						String simpleName = paramType.getSimpleName();
						
						if("String".equals(simpleName)) {
							method.invoke(t, rs.getString(i+1));
						} else if("long".equals(simpleName)) {
							method.invoke(t, rs.getLong(i+1));
						} else if("int".equals(simpleName)) {
							method.invoke(t, rs.getInt(i+1));
						} else if("Date".equals(simpleName)) {
							method.invoke(t, DateTimeUtil.parseNormal(rs.getString(i+1)));
						} else if("Long".equals(simpleName)) {
							String value = rs.getString(i+1);
							Long longValue = (value == null)?null:Long.parseLong(value);
							method.invoke(t, longValue);
						} else if("Integer".equals(simpleName)) {
							String value = rs.getString(i+1);
							Integer integerValue = (value == null)?null:Integer.parseInt(value);
							method.invoke(t, integerValue);
						}
					}
				}
				datas.add(t);
			}
			return datas;
		} catch(Exception e) {
//			if(method != null) {
//				System.out.println("-------------->" + method.getName() + "" + method.getParameterTypes());
//			}
			e.printStackTrace();
			logger.error("数据库查询异常: ", e);
			throw e;
		} finally {
			releaseRes(conn, pst, rs);
		}
	}
	
	private static Class<?> getFirstParamType(Method method) {
		Class<?>[] cls = method.getParameterTypes();
		return (cls == null || cls.length == 0)? null: cls[0];
	}
	
	private static Method getMethod(Method[] methods, String methodName) {
		if(methods == null || methods.length == 0 || methodName == null) {
			return null;
		}
		for (Method method : methods) {
			if(methodName.equalsIgnoreCase(method.getName())) {
				return method;
			}
		}
		return null;
	}
	
	/**
	 * @param connPrefix
	 * @param sql
	 * @param params
	 * @return
	 */
	public static List<java.util.Map<String, Object>> query(String connPrefix, String sql, Object[] params) {
		return query(getConnection(connPrefix), sql, params);
	}
	
	/**
	 * 查询列表数据
	 * @param conn
	 * @param sql
	 * @param params
	 * @return
	 */
	public static List<java.util.Map<String, Object>> query(Connection conn, String sql, Object[] params) {
		if(conn == null) {
			return null;
		}
		PreparedStatement pst = null;
		ResultSet rs = null;
		ResultSetMetaData meta = null;
		
		try {
			pst = conn.prepareStatement(sql);
			
			if(params != null) {
				for (int i = 0; i < params.length; i++) {
					pst.setObject(i + 1, params[i]);
				}
			}
			
			rs = pst.executeQuery();
			meta = pst.getMetaData();
			
			int colCount = meta.getColumnCount();
			List<java.util.Map<String, Object>> datas = new ArrayList<>();
			while(rs.next()) {
				java.util.Map<String, Object> mapData = new java.util.HashMap<>();
				for (int i = 0; i < colCount; i++) {
					String colName = meta.getColumnName(i+1);
					Object objValue = rs.getObject(i+1);
					if(objValue == null) {
						mapData.put(colName, null);
						continue;
					}
					
					int type = meta.getColumnType(i+1);
					if(isStringType(type)) {
						mapData.put(colName, rs.getString(i+1));
					} else if(type == Types.BIGINT) {
						mapData.put(colName, rs.getLong(i+1));
					} else if(inType(type, Types.INTEGER, Types.TINYINT, Types.SMALLINT)) {
						mapData.put(colName, rs.getInt(i+1));
					} else if(type == Types.BOOLEAN) {
						mapData.put(colName, rs.getBoolean(i+1));
					} else if(type == Types.FLOAT) {
						mapData.put(colName, rs.getFloat(i+1));
					} else if(inType(type, Types.DOUBLE, Types.NUMERIC)) {
						mapData.put(colName, parseDoubleInt(rs.getDouble(i+1)));
					} else if(inType(type, Types.DATE)) {
						mapData.put(colName, rs.getDate(i+1));
					} else if(inType(type, Types.TIMESTAMP)) {
						mapData.put(colName, rs.getTimestamp(i+1));
					} else {
						mapData.put(colName, objValue);
					}

				}
				datas.add(mapData);
			}
			return datas;
		} catch(Exception e) {
//			if(method != null) {
//				System.out.println("-------------->" + method.getName() + "" + method.getParameterTypes());
//			}
			e.printStackTrace();
			logger.error("数据库查询列表异常: ", e);
		} finally {
			releaseRes(conn, pst, rs);
		}
		return null;
	}
	
	/**
	 * 如果是整数则返回int整数值
	 * @param d
	 * @return
	 */
	private static Object parseDoubleInt(double d) {
		if (d == (int)d) {
			return (int)d;
		}
		return d;
	}
	
	private static boolean isStringType(int type) {
//		return (type == Types.CHAR || type == Types.VARCHAR 
//				|| type == Types.NVARCHAR || type == Types.CLOB || type == Types.NCLOB || type == Types.NCHAR);
		return inType(type, Types.CHAR, Types.VARCHAR, Types.NVARCHAR, Types.CLOB, Types.NCLOB, Types.NCHAR);
	}
	
	private static boolean inType(int type, int...types) {
		if(types == null || types.length == 0) {
			return false;
		}
		for (int t : types) {
			if(type == t) 
				return true;
		}
		return false;
	}
	
	/**
	 * 释放数据库资源
	 * @param con
	 * @param pst
	 * @param rs
	 */
	public static void releaseRes(Connection con, Statement st, ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
			} catch(Exception e) {
				e.printStackTrace();
				logger.error("数据库资源释放异常: ", e);
			}
			rs = null;
		}
		
		if(st != null) {
			try {
				st.close();
			} catch(Exception e) {
				logger.error("数据库资源释放异常: ", e);
			}
			st = null;
		}
		
		if(con != null) {
			try {
				con.close();
			} catch(Exception e) {
				logger.error("数据库资源释放异常: ", e);
			}
			con = null;
		}
	}
	
	/**
	 * 释放数据库资源
	 * @param con
	 * @param pst
	 */
	public static void releaseRes(Connection con, Statement st) {
		releaseRes(con, st, null);
	}

}
