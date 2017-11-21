package com.tiger.utils;

import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

/**
 * @author LeBron
 *
 */
public class ThreadUtil {
	
	private static Logger logger = Logger.getLogger(ThreadUtil.class);
	
	/**
	 * 系统线程数量
	 */
	private final static int SYS_THREAD_COUNT = Runtime.getRuntime().availableProcessors()*2;
	
	private static ConcurrentHashMap<String, ExecutorService> threadPoolMap = new ConcurrentHashMap<>();
	
	public static int getSysThreadCount() {
		return SYS_THREAD_COUNT;
	}
	
	/**
	 * 创建新线程并执行
	 * @param target
	 * @return
	 */
	public static Thread runWithNewThread(Runnable target) {
		Thread t = new Thread(target);
		t.start();
		return t;
	}
	
	/**
	 * 线程睡眠
	 * @param ms
	 * @return
	 */
	public static boolean sleepThread(long ms) {
		try {
			Thread.sleep(ms);
			return true;
		} catch(Exception e) {
			logger.error("线程["+Thread.currentThread().getName()+"] 睡眠失败: ", e);
//			FileLog.init(ThreadUtil.class).log("线程["+Thread.currentThread().getName()+"]睡眠失败: ", e);
			return false;
		}
	}
	
	/**
	 * 线程睡眠10毫秒
	 * @return
	 */
	public static boolean sleepThreadTenMs() {
		return sleepThread(10L);
	}
	
	/**
	 * 线程睡眠一个单位
	 * @return
	 */
	public static boolean sleepThreadUnit() {
		return sleepThread(20L);
	}
	
	/** pool **/
	
	private static ThreadLocal<ExecutorService> threadLocal = new ThreadLocal<>();
	private static ExecutorService mainExecutor ;
	
	private static int nThreads = 0;
	static {
//		try {
//			String count = ResourceBundle.getBundle("sys").getString("threadpool.count");
//			nThreads = Integer.parseInt(count);
//		} catch(Exception e) {
//			logger.error("", e);
//		}
		if(nThreads < 10 || nThreads > 100) 
			nThreads = SYS_THREAD_COUNT;
		
		mainExecutor = Executors.newFixedThreadPool(nThreads);
		logger.info("线程池开启了[" + nThreads+"] 个线程.");
		
	}
	
	
	private static ExecutorService getExecutor() {
		ExecutorService executor = threadLocal.get();
		if(executor == null) {
			executor = Executors.newFixedThreadPool(nThreads);
			logger.info("开启了[" + nThreads+"] 个线程.");
			threadLocal.set( executor);
			return executor;
		}
		return executor;
	}
	
	/**
	 * 从线程池中获取一个线程运行
	 * <pre>
	 * 该线程池共享一个, 所有调用此方法的线程公用一个线程池
	 * </pre> 
	 * @param runnable
	 */
	public static void runWithThreadPool(Runnable runnable) {
		
		mainExecutor.submit(runnable);
	}
	
	/**
	 * 从线程池中获取一个线程运行
	 * <pre>
	 * 该线程池非共享, 调用此方法的线程创建使用自己的一个线程池
	 * </pre> 
	 * @param runnable
	 */
	public static void runWithSelfThreadPool(Runnable runnable) {
		
		getExecutor().submit(runnable);
	}
	
	/**
	 * 线程池创建锁, 防止重复创建线程池
	 */
	private static Lock locker = new ReentrantLock();
	/**
	 * 根据线程池名称, 选择所对应的线程池运行
	 * @param runnable		实现
	 * @param name			线程池名称
	 * @param threadCount	线程池线程数量
	 */
	public static void runWithThreadPool(Runnable runnable, String name, int threadCount) {
		ExecutorService service;
		locker.lock();
		try {
			if(threadPoolMap.containsKey(name)) {
				service = threadPoolMap.get(name);
			} else {
				service = Executors.newFixedThreadPool(threadCount);
				logger.info("开启了[" + threadCount+"] 个线程.");
				threadPoolMap.put(name, service);
			}
		} finally {
			locker.unlock();
		}
		
		service.submit(runnable);
	}
	
	/**
	 * 根据线程池名称, 选择所对应的线程池运行
	 * @param runnable	实现
	 * @param name		线程池名称
	 */
	public static void runWithThreadPool(Runnable runnable, String name) {
		runWithThreadPool(runnable, name, nThreads);
	}
	
	/**
	 * 创建新的线程池
	 * @return
	 */
	public static ExecutorService newSysThreadPool() {
		logger.info("开启了[" + SYS_THREAD_COUNT+"] 个线程.");
		return Executors.newFixedThreadPool(SYS_THREAD_COUNT);
	}
	
	/**
	 * 判断线程池任务是否已满
	 * @param service
	 * @return
	 */
	public static boolean isFilledPool(ExecutorService service) {
		if(service instanceof ThreadPoolExecutor) {
			ThreadPoolExecutor pool = (ThreadPoolExecutor) service;
			return pool.getActiveCount() == pool.getMaximumPoolSize();
		}
		return false;
	}
	
	/**
	 * 判断线程池是否已满
	 * @return
	 */
	public static boolean isFilledPool() {
		return isFilledPool(mainExecutor);
	}

}
