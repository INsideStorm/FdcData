package com.tiger.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 文件工具类
 * @author LeBron
 *
 */
public class FileUtils {
	
	private static Logger logger = Logger.getLogger(FileUtils.class);
	
	/**
	 * 移动重复
	 */
	public final static int MOVE_REPEAT = -1;
	
	/**
	 * 解析文本文件返回数据集合
	 * @param filePath
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public static List<String> readLines(String filePath, String encoding) throws Exception {
		return readLines(new File(filePath), encoding);
	}
	
	/**
	 * 解析文本文件返回数据集合
	 * @param file		文件
	 * @param encoding	编码
	 * @return
	 * @throws Exception
	 */
	public static List<String> readLines(File file, String encoding) throws Exception {
//		while(!file.renameTo(file));
		while(!file.canRead());
		return file == null? null: org.apache.commons.io.FileUtils.readLines(file, encoding);
//		FileInputStream fis = null;
//		InputStreamReader isr = null;
//		BufferedReader reader = null;
//		
//		List<String> datas = null;
//		try {
//			fis = new FileInputStream(file);
//			isr = new InputStreamReader(fis, encoding);
//			reader = new BufferedReader(isr);
//			
//			String readStr = null;
//			datas = new ArrayList<>();
//			int i = 0;
//			while((readStr = reader.readLine()) != null) {
//				datas.add(readStr);
//			}
//		} finally {
//			if(reader != null) {
//				reader.close();
//			}
//			if(isr != null) {
//				isr.close();
//			}
//			if(fis != null) {
//				fis.close();
//			}
//		}
//		return datas;
	}
	
	/**
	 * 解析文件内容并返回集合列表
	 * @param file			文件
	 * @param encoding		编码
	 * @param fileCursor	解析判断, 需要重写{@link FileCursor#isReadEnd(int, String)} 方法
	 * @param timeout		解析超时时间
	 * @return
	 * @throws Exception
	 */
	public static List<String> readLines(File file, String encoding, FileCursor fileCursor, long timeout) throws Exception {
		FileInputStream input = null;
		InputStreamReader isr = null;
		BufferedReader reader = null;
		
		try {
			input = new FileInputStream(file);
			isr = new InputStreamReader(input, encoding);
			reader = new BufferedReader(isr);
			
			final List<String> list = new ArrayList<String>();
	        String line = null;
	        int i = 0;
	        
	        long start = System.currentTimeMillis();
	        while (/*true &&*/ ((System.currentTimeMillis() - start) <= timeout)) {
	        	
	        	if(fileCursor.isReadEnd(i, line)) {
	        		break;
	        	} else {
	        		line = reader.readLine();
		        	if(line != null) {
		        		i++;
		        		list.add(line);
		        	} else {
		        		ThreadUtil.sleepThreadTenMs();
		        	}
	        	}
	        }
	        
	        return list;
		} finally {
			StreamUtils.close(reader);
			StreamUtils.close(isr);
			StreamUtils.closeStream(input);
		}
        
	}
	
	/**
	 * 
	 * @author LeBron
	 *
	 */
	public static class FileCursor {
		/**
		 * 是否已经读到了行尾
		 * @param i			读取行数
		 * @param content	
		 * @return
		 */
		public boolean isReadEnd(int i, String content) {
			return true;
		}
	}
	
	/**
	 * 移动文件 
	 * @see org.apache.commons.io.FileUtils#moveFile(File, File)
	 * @param srcFile
	 * @param destFile
	 * @return
	 */
	public static boolean moveFile(File srcFile, File destFile) {
		try {
			
			org.apache.commons.io.FileUtils.moveFile(srcFile, destFile);
			return true;
		} catch(Exception e) {
			logger.error("", e);
		}
		return false;
	}
	
	/**
	 * 移动文件 
	 * @see org.apache.commons.io.FileUtils#moveFile(File, File)
	 * @param srcFile
	 * @param destFile
	 * @return
	 * <ul>
	 * 	<li>1 : 成功</li>
	 * 	<li>0 : 失败</li>
	 * 	<li>-1: 文件已存在</li>
	 * </ul>
	 */
	public static int moveFileTo(File srcFile, File destFile) {
		try {
			
			org.apache.commons.io.FileUtils.moveFile(srcFile, destFile);
			return 1;
		} catch(Exception e) {
			logger.error("", e);
			if(e instanceof org.apache.commons.io.FileExistsException) {
				return MOVE_REPEAT;
			}
		}
		return 0;
	}
	
	/**
	 * 判断是否是移动文件存在code
	 * @param code
	 * @return
	 */
	public static boolean isMoveRepeat(int code) {
		return code == MOVE_REPEAT;
	}
	
	/**
	 * 移动至替换目录
	 * @param oldRoot
	 * @param newRoot
	 * @param srcFile
	 * @return
	 */
	public static boolean moveFileToReplaceRoot(String oldRoot, String newRoot, File srcFile) {
		String newFilePath = srcFile.getPath().replace(oldRoot, newRoot);
		return moveFile(srcFile, new File(newFilePath));
	}
	
	/**
	 * 复制文件
	 * @param srcFile
	 * @param destFile
	 * @param preserveFileDate	是否保留修改日期
	 */
	public static boolean copyFile(File srcFile, File destFile, boolean preserveFileDate) {
		try {
			org.apache.commons.io.FileUtils.copyFile(srcFile, destFile, preserveFileDate);
			return true;
		} catch(Exception e) {
			logger.error("复制文件异常: ", e);
			return false;
		}
	}
	
	/**
	 * 复制文件 [保留修改日期]
	 * @param srcFile
	 * @param destFile
	 */
	public static boolean copyFile(File srcFile, File destFile) {
		try {
			org.apache.commons.io.FileUtils.copyFile(srcFile, destFile);
			return true;
		} catch(Exception e) {
			logger.error("复制文件异常: ", e);
			return false;
		}
	}
	
	/**
	 * 向文件中写入内容
	 * @param file		文件
	 * @param content	内容
	 * @param append	是否拼接
	 * @return
	 */
	public static boolean write(File file, String content, boolean append) {
		return write(file, content, "UTF-8", append);
	}
	
	/**
	 * @param is
	 * @param file
	 * @return
	 */
	public static boolean write(InputStream is, File file) {
		if(is == null || file == null) {
			return false;
		}
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			byte[] bys = new byte[1024];
			int len;
			while((len = is.read(bys)) != -1) {
				fos.write(bys, 0, len);
			}
			return true;
		} catch(Exception e) {
			logger.error("", e);
		} finally {
			StreamUtils.closeStreams(fos, is);
		}
		return false;
	}
	
	public static boolean write(byte[] bytes, File file) {
		if(bytes == null || file == null) {
			return false;
		}
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(bytes);
			return true;
		} catch(Exception e) {
			logger.error("", e);
		} finally {
			StreamUtils.closeStream(fos);
		}
		return false;
	}
	
	/**
	 * 向文件中写入内容
	 * @param file		文件
	 * @param content	内容
	 * @param encoding	编码格式
	 * @param append	是否拼接
	 * @return
	 */
	public static boolean write(File file, String content, String encoding, boolean append) {
		try {
			org.apache.commons.io.FileUtils.writeStringToFile(file, content, encoding, append);
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			logger.error("", e);
//			FileLog.init(FileUtils.class).log("文件写入异常: " + e.getMessage() + "\r\n File : " + file.getPath() + "\r\n Content : " + content);
		}
		return false;
	}
	
	/**
	 * 插入数据并换行
	 * @param file
	 * @param contents
	 * @param preNewLine
	 * @param encoding
	 * @return
	 */
	public static boolean writeLine(File file, List<String> contents, boolean preNewLine, String encoding) {
		if(contents == null || contents.isEmpty()) 
			return false;
		StringBuilder builder = new StringBuilder();
		int size = contents.size();
		for (int i = 0; i < size-1; i++) {
			builder.append(contents.get(i) == null?"null": contents.get(i)).append("\r\n");
		}
		builder.append(contents.get(size-1));
		return writeLine(file, builder.toString(), preNewLine, encoding);
	}
	
	/**
	 * 插入数据并换行
	 * @param file
	 * @param contents
	 * @param encoding
	 * @return
	 */
	public static boolean writeLine(File file, List<String> contents, String encoding) {
		return writeLine(file, contents, false, encoding);
	}
	
	/**
	 * 插入并换行
	 * @param file
	 * @param content
	 * @param encoding
	 * @return
	 */
	public static boolean writeLine(File file, String content, String encoding) {
		return writeLine(file, content, false, encoding);
	}
	
	/**
	 * 插入并换行
	 * @param file
	 * @param content
	 * @param preNewLine
	 * @param encoding
	 * @return
	 */
	public static boolean writeLine(File file, String content, boolean preNewLine, String encoding) {
		if(content == null) {
			return false;
		}
		if(preNewLine) {
			content = "\r\n" + content;
		}
		content += "\r\n";
		return write(file, content, encoding, true);
	}
	
	/**
	 * 获取某个目录下所有文件列表信息
	 * @param dir
	 * @param fileFilter
	 * @return
	 */
	public static List<File> getFiles(String dir, FileFilter fileFilter) {
		return getFiles(new File(dir), fileFilter);
	}
	
	/**
	 * 获取某个目录下所有文件列表信息 <i style='color: red'>(不包括文件夹 及 子目录)</i>
	 * @param dir			文件夹
	 * @param fileFilter	文件过滤器
	 * @return
	 */
	public static List<File> getFiles(File dir, final FileFilter fileFilter) {
		if(dir == null || !dir.exists()) {
			return new ArrayList<>();
		}
		List<File> files = new ArrayList<>();
		if(!dir.isDirectory()) {
			if(fileFilter.accept(dir)) {
				files.add(dir);
			}
		} else {
			File[] fileArr = dir.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					// TODO Auto-generated method stub
					return pathname.isFile() && fileFilter.accept(pathname);
				}
			});
			if(fileArr != null && fileArr.length > 0) {
				files = new ArrayList<>(java.util.Arrays.asList(fileArr));
			}
		}
		return files;
	}
	
	/**
	 * 获取某个目录及子目录下所有文件列表信息 <i style='color: red'>(不包括文件夹)</i>
	 * @param dir
	 * @param fileFilter
	 * @return
	 */
	public static List<File> eachFiles(File dir, FileFilter fileFilter) {
		if(dir == null || !dir.exists()) {
			return new ArrayList<>();
		}
		List<File> files = getFiles(dir, fileFilter);
		
		File[] listFiles = dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return pathname.isDirectory();
			}
		});
		
		if(listFiles != null && listFiles.length > 0 ) {
			for (File f : listFiles) {
				files.addAll(eachFiles(f, fileFilter));
			}
		}
		return files;
	}
	
	/**
	 * 获取文件列表
	 * @param dir
	 * @param fileSuffixs
	 * @return
	 */
	public static List<File> getFiles(String dir, final String... fileSuffixs) {
		return getFiles(new File(dir), fileSuffixs);
	}
	
	/**
	 * 获取某个目录下所有文件列表信息 <i style='color: red'>(不包括文件夹 及 子目录)</i>
	 * @param dir			文件目录
	 * @param fileSuffixs	文件后缀名
	 * @return
	 */
	public static List<File> getFiles(File dir, final String... fileSuffixs) {
		return getFiles(dir, new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				
				return isSuffixName(pathname, fileSuffixs);
			}
		});
	}
	
	/**
	 * 遍历文件夹 及 子目录  <i style='color: red;'>(不包含neDirs目录下的文件)</i>
	 * @param dir
	 * @param neDirs
	 * @return
	 */
	public static List<File> eachFilesNeDirs(File dir, final String... neDirs) {
		List<File> list = new ArrayList<>();
		if(dir == null || !dir.exists() || !dir.isDirectory()) {
			return list;
		}
		File[] files = dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return !(pathname.isDirectory() && StringUtils.arrayEq(pathname.getName(), neDirs));
			}
		});
		
		if(files == null || files.length == 0) {
			return list;
		}
		for (File file : files) {
			if(file.isDirectory()) {
				list.addAll(eachFilesNeDirs(file, neDirs));
			}
			if(file.isFile()) {
				list.add(file);
			}
		}
		return list;
	}
	
	/**
	 * 遍历获取文件夹下文件列表
	 * @param dir
	 * @return
	 */
	@Deprecated
	public static List<File> getLoopFiles(File dir) {
		List<File> files = new ArrayList<>();
		if(dir == null || !dir.exists()) {
			return files;
		}
		if(!dir.isDirectory()) {
			files.add(dir);
			return files;
		}
		File[] listFiles = dir.listFiles();
		if(listFiles != null && listFiles.length > 0 ) {
			for (File f : listFiles) {
				files.addAll(getLoopFiles(f));
			}
		}
		return files;
	}
	
	/**
	 * 校验并创建目录
	 * @param dir	文件目录
	 * @return
	 */
	public static File ensureDirs(String dir) {
		File file = new File(dir);
		return ensureDirs(file);
	}
	
	/**
	 * 验证并创建目录
	 * @param dir
	 * @return
	 */
	public static File ensureDirs(File dir) {
		if(dir == null) {
			return null;
		}
		if(!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
		}
		return dir;
	}
	
	/**
	 * 验证并创建目录
	 * @param parentDir
	 * @param name
	 * @return
	 */
	public static File ensureDirs(File parentDir, String name) {
		return ensureDirs(new File(parentDir, name));
	}
	

	/**
	 * 按日期生成文件夹
	 * @param parentDir
	 * @return
	 */
	public static File createDirByDate(File parentDir) {
		return ensureDirs(new File(parentDir, DateTimeUtil.getCurrentCompactDate()));
	}
	
	/**
	 * 按日期生成文件夹
	 * @param parentDir
	 * @return
	 */
	public static File createDirByDate(String parentDir) {
		return ensureDirs(new File(parentDir, DateTimeUtil.getCurrentCompactDate()));
	}
	
	/**
	 * 校验是否是目录
	 * @param pathname
	 * @return
	 */
	public static boolean isDirectory(String pathname) {
		return new File(pathname).isDirectory();
	}
	
	/**
	 * 校验是否是文件
	 * @param pathname
	 * @return
	 */
	public static boolean isFile(String pathname) {
		return new File(pathname).isFile();
	}
	
	/**
	 * 检查文件是否是某后缀名文件
	 * @param file		文件对象
	 * @param suffixs	后缀名数组
	 * @return
	 */
	public static boolean isSuffixName(File file, String... suffixs) {
		if(file == null || file.getName() == null || !file.exists()) {
			return false;
		}
		for (String suffix : suffixs) {
			if(suffix == null) {
				continue;
			}
			if(file.getName().toLowerCase().endsWith(suffix.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 向上父目录级联删除
	 * @param dir
	 * @param outs
	 */
	public static void cascadeDeleteByDir(File dir, String... outs) {
		if(dir == null || !dir.exists() || !dir.isDirectory()) {
			return ;
		}
		
		if(StringUtils.arrayEq(dir.getName(), outs)) {
			return;
		}
		
		File[] files = dir.listFiles();
		if(files == null || files.length == 0) {
			dir.delete();
			cascadeDeleteByDir(dir.getParentFile(), outs);
		}
	}
	
	/**
	 * 删除所有空文件夹
	 * <p>如果子文件夹为空, 则删除并重新判别上级目录</p>
	 * @param rootDir
	 */
	public static void deleteEmptyDirs(File rootDir) {
		deleteEmptyDirs(rootDir, rootDir, true);
	}
	
	/**
	 * 删除文件夹
	 * @param dir
	 * @param rootDir
	 * @param down	是否向下级遍历
	 */
	private static void deleteEmptyDirs(File dir, File rootDir, boolean down) {
		if(!isExistDir(dir) || !isExistDir(rootDir)) {
			return;
		}
		File[] children = dir.listFiles();
		if(children == null || children.length == 0) {
			if(dir.getPath().equals(rootDir.getPath())) {
				return;
			}
			dir.delete();	// 没有数据子目录则删除
			deleteEmptyDirs(dir.getParentFile(), rootDir, false);
		} else if(down) {
			for (File child : children) {
				deleteEmptyDirs(child, rootDir, down);
			}
		}
	}
	
	/**
	 * 判断是否是已存在目录
	 * @param file
	 * @return
	 */
	public static boolean isExistDir(File file) {
		return file != null && file.exists() && file.isDirectory();
	}
	
	/**
	 * 向上父目录级联删除
	 * @param file
	 * @param outs
	 */
	public static void cascadeDelete(File file, String... outs) {
		if(file == null) {
			return ;
		}
		if(file.isFile()) {
			if(file.exists()) {
				file.delete();
			}
			file = file.getParentFile();
		} 
		cascadeDeleteByDir(file, outs);
	}
	
	/**
	 * 遍历目录下所有文件 <i style='color: red;'>(包括文件夹 和 文件, 不遍历子目录)</i>
	 * @param dirPath
	 * @return
	 */
	public static File[] getFiles(String dirPath) {
		File dir = new File(dirPath);
		if(!dir.exists() || !dir.isDirectory()) {
			return null;
		}
		return dir.listFiles();
	}
	
	/**
	 * 遍历目录及子目录下所有文件名, 返回文件名列表
	 * @param dir
	 * @return
	 */
	public static String[] eachNames(File dir) {
		if(dir == null) {
			return null;
		}
		File[] files = dir.listFiles();
		List<String> names = new ArrayList<>();
		for (File f : files) {
			if(!f.exists()) 
				continue;
			if(f.isFile()) {
				names.add(f.getName());
				continue;
			}
			if(f.isDirectory()) {
				String[] ns = eachNames(f);
				names.addAll(new ArrayList<>(Arrays.asList(ns)));
			}
		}
		return names.toArray(new String[names.size()]);
	}
	
	/**
	 * 遍历目录及子目录下所有文件, 并返回文件列表(不包括文件夹)
	 * @param dir
	 * @return
	 */
	public static List<File> eachFiles(File dir) {
		if(dir == null) {
			return null;
		}
		File[] files = dir.listFiles();
		List<File> eachFiles = new ArrayList<>();
		for (File f : files) {
			if(!f.exists()) 
				continue;
			if(f.isFile()) {
				eachFiles.add(f);
				continue;
			}
			if(f.isDirectory()) {
				List<File> ns = eachFiles(f);
				eachFiles.addAll(ns);
			}
		}
		return eachFiles;
	}
	
	/**
	 * 遍历目录及子目录下所有文件, 获取除文件名和outs相等外的, 并返回文件列表(不包括文件夹)
	 * @param dir
	 * @param outs
	 * @return
	 */
	public static List<File> eachFiles(File dir, String... outs) {
		if(dir == null) {
			return null;
		}
		File[] files = dir.listFiles();
		List<File> eachFiles = new ArrayList<>();
		for (File f : files) {
			if(!f.exists()) 
				continue;
			if(StringUtils.arrayEq(f.getName(), outs)) {
				continue;
			}
			if(f.isFile()) {
				eachFiles.add(f);
				continue;
			}
			if(f.isDirectory()) {
				List<File> ns = eachFiles(f, outs);
				eachFiles.addAll(ns);
			}
		}
		return eachFiles;
	}
	
	/**
	 * 遍历目录及子目录下 和 eqs相等的 所有文件, 并返回文件列表(不包括文件夹)
	 * @param dir
	 * @param outs
	 * @return
	 */
	public static List<File> eachEqNamesFiles(File dir, String... eqs) {
		if(dir == null) {
			return null;
		}
		File[] files = dir.listFiles();
		List<File> eachFiles = new ArrayList<>();
		for (File f : files) {
			if(!f.exists()) 
				continue;
			if(f.isDirectory()) {
				List<File> ns = eachEqNamesFiles(f, eqs);
				eachFiles.addAll(ns);
			}
			
			if(!StringUtils.arrayEq(f.getName(), eqs)) {
				continue;
			}
			if(f.isFile()) {
				eachFiles.add(f);
				continue;
			}
			
		}
		return eachFiles;
	}
	
	/**
	 * 创建新文件
	 * @param filePath
	 * @return
	 */
	public static File createNewFile(String filePath) {
		if(filePath == null) {
			return null;
		}
		File file = new File(filePath);
		try {
			return (file.createNewFile() || file.exists()) ? file: null;
		} catch(Exception e) {
			e.printStackTrace();
			logger.error("", e);
		}
		return null;
	}
	
	/**
	 * 创建新文件
	 * @param dir
	 * @param filename
	 * @return
	 */
	public static File createNewFile(File dir, String filename) {
		if(filename == null) {
			return null;
		}
		File file = new File(dir, filename);
		try {
			return (file.createNewFile() || file.exists()) ? file: null;
		} catch(Exception e) {
			e.printStackTrace();
			logger.error("", e);
		}
		return null;
	}

}
