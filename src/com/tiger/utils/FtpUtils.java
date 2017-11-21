package com.tiger.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

/**
 * FTP 工具类
 * @author LeBron
 *
 */
public class FtpUtils {
	
	private static Logger logger = Logger.getLogger(FtpUtils.class);
	
	/**
	 * FTP host
	 */
	private String host;
	/**
	 * FTP port
	 */
	private int port;
	
	private String username;
	private String password;
	
	private FTPClient ftpClient;
	
	private final static String DEFAULT_SYSTEMKEY = FTPClientConfig.SYST_NT;	// Windows系统标识
	private final static String DEFAULT_ENCODING = "UTF-8";
	private final static int DEFAULT_BUFFERSIZE = 300000;
	
	private final static String FTP_HEAD = "ftp://";
	
	public final static int DEFAULT_PORT = 21;
	
	private FtpUtils() {
	}

	private FtpUtils(String host, int port, String username, String password, String systemKey) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		
		// 初始化FTP client 对象
		ftpClient = getFtpClient(systemKey);
	}
	
	/**
	 * 获取FTP client
	 * @param bufferSize	缓冲大小
	 * @param encoding		编码
	 * @param systemKey		系统标识
	 * @return
	 */
	public static FTPClient getFtpClient( int bufferSize, String encoding, String systemKey) {
		FTPClient ftpClient = new FTPClient();
		ftpClient.setBufferSize(bufferSize);
		ftpClient.setControlEncoding(encoding);
		FTPClientConfig conf = new FTPClientConfig(systemKey == null?DEFAULT_SYSTEMKEY:systemKey);
		conf.setServerLanguageCode("zh");
		return ftpClient;
	}
	
	/**
	 * 获取FTP client
	 * @param systemKey	系统标识
	 * @return
	 */
	public static FTPClient getFtpClient( String systemKey) {
		return getFtpClient(DEFAULT_BUFFERSIZE, DEFAULT_ENCODING, systemKey);
	}
	
	/**
	 * 获取FTP client (Windows系统)
	 * @return
	 */
	public static FTPClient getFtpClient() {
		return getFtpClient(null);
	}
	
	/** 初始化工具类对象 **/
	public static FtpUtils init(String host, int port, String username, String password, String systemKey) {
		return new FtpUtils(host, port, username, password, systemKey);
	}
	
	public static FtpUtils init(String host, int port, String username, String password) {
		
		return init(host, port, username, password, null);
	}
	
	public static FtpUtils init(String host, String username, String password, String systemKey) {
		return init(host, -1, username, password, systemKey);
	}
	
	public static FtpUtils init(String host, String username, String password) {
		
		return init(host, -1, username, password, null);
	}
	
	
	/**
	 * 创建并连接FTP服务器
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static FTPClient connectFtpServer(String host, int port, String username, String password) throws Exception {
		FTPClient client = getFtpClient();
		boolean connected = connectFtpServer(client, host, port, username, password);
		if(connected) {
			return client;
		} else {
			disConnect(client);
		}
		return null;
	}
	
	/**
	 * 创建并连接FTP服务器
	 * @param host
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static FTPClient connectFtpServer(String host, String username, String password) throws Exception {
		return connectFtpServer(host, -1, username, password);
	}
	
	/**
	 * 创建并连接FTP服务器
	 * @param ftpClient
	 * @param host
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static boolean connectFtpServer(FTPClient ftpClient, String host, String username, String password) throws Exception {
		return connectFtpServer(ftpClient, host, -1, username, password);
	}
	
	/**
	 * 连接FTP服务器
	 * @param ftpClient
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static boolean connectFtpServer(FTPClient ftpClient, String host, int port, String username, String password) throws Exception {
		if(port == -1) {
			// 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
			ftpClient.connect(host);
		} else {
			ftpClient.connect(host, port);
		}
		ftpClient.login(username, password);
		
		int reply = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftpClient.disconnect();
			return false;
		}
		return true;
	}
	
	/**
	 * 连接FTP服务器
	 * @throws Exception
	 */
	private boolean connectFtpServer() throws Exception {
		return connectFtpServer(ftpClient, host, port, username, password);
	}
	
	/**
	 * 获取文件目录
	 * @param client
	 * @param ftpDirectory
	 * @return
	 * @throws Exception
	 */
	public static List<FTPFile> getFtpFiles(FTPClient client, String ftpDirectory) throws Exception {
		FTPFileFilter filter = null;
		return getFtpFiles(client, ftpDirectory, filter);
	}
	
	/**
	 * 获取FTP 目录文件列表 (前提需要创建并连接FTPServer)
	 * @param client		FTP client
	 * @param ftpDirectory	FTP 目录
	 * @param ftpFilter		文件过滤器
	 * @return
	 * @throws Exception
	 */
	public static List<FTPFile> getFtpFiles(FTPClient client, String ftpDirectory, FTPFileFilter ftpFilter) throws Exception {
		boolean changed = changeWorkingDirectory(client, ftpDirectory);	// 转移到FTP服务器目录
		if(!changed) {
			return Collections.emptyList();
		}
		
		FTPFile[] fs = client.listFiles();
		List<FTPFile> files = new ArrayList<>();
		for (FTPFile ftpFile : fs) {
			if(ftpFilter == null || ftpFilter.accept(ftpFile)) {
				files.add(ftpFile);
			}
		}
		return files;
	}
	
	/**
	 * 获取文件列表 (前提需要创建并连接FTPServer)
	 * @param client
	 * @param ftpDirectory
	 * @param allowSuffixs
	 * @return
	 * @throws Exception
	 */
	public static List<FTPFile> getFtpFiles(FTPClient client, String ftpDirectory, final String... allowSuffixs) throws Exception {
		return getFtpFiles(client, ftpDirectory, getAllowSuffixFilter(allowSuffixs));
	}
	
	/**
	 * 转移到FTP服务器目录
	 * @param client
	 * @param ftpDirectory
	 * @return
	 * @throws Exception
	 */
	private static boolean changeWorkingDirectory(FTPClient client, String ftpDirectory) throws Exception {
		client.enterLocalPassiveMode();
		return client.changeWorkingDirectory(ftpDirectory);	// 转移到FTP服务器目录
	}
	
	/**
	 * 下载文件到本地
	 * @param client
	 * @param ftpDirectory	FTP 目录
	 * @param localPath		本地路径
	 * @param overlap		如果文件存在是否替换
	 * @return
	 */
	public static boolean downLoadFiles(FTPClient client, String ftpDirectory, String localPath, boolean overlap) {
		return downLoadFiles(client, ftpDirectory, localPath, overlap, new String[0]);
	}
	
	public static boolean downLoadFiles(FTPClient client, String ftpDirectory, String localPath) {
		return downLoadFiles(client, ftpDirectory, localPath, true);
	}
	
	/**
	 * 下载文件到本地
	 * @param client		FTP client
	 * @param ftpDirectory	FTP 目录
	 * @param localPath		本地文件路径
	 * @param overlap		如果文件是否替换
	 * @param filter		文件过滤器
	 * @return
	 */
	public static boolean downLoadFiles(FTPClient client, String ftpDirectory, String localPath, boolean overlap, FTPFileFilter filter) {
		if(client == null ) 
			return false;
		try {
			
			return downLoads(client, ftpDirectory, localPath, overlap, filter);
		} catch (Exception e) {
			logger.error("FTP服务器下载对账文件失败：", e);
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean downLoadFiles(FTPClient client, String ftpDirectory, String localPath, FTPFileFilter filter) {
		return downLoadFiles(client, ftpDirectory, localPath, true, filter);
	}
	
	/**
	 * 下载文件
	 * @param client
	 * @param ftpDirectory
	 * @param localPath
	 * @param overlap
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	private static boolean downLoads(FTPClient client, String ftpDirectory, String localPath, boolean overlap, FTPFileFilter filter) throws Exception {
		boolean changed = changeWorkingDirectory(client, ftpDirectory);	// 转移到FTP服务器目录
		if(!changed) {
			return false;
		}
		
		FTPFile[] fs = client.listFiles();
		if(fs == null || fs.length == 0) {
			return false;
		}
		FileUtils.ensureDirs(localPath);	// 校验并创建本地文件夹
		
		if(filter == null ) {
			for (FTPFile ff : fs) {
				if(ff.isFile()) {
					receiveFtpFiles(client, localPath, ff.getName(), ff, overlap);
				}
			}
		} else {
			for (FTPFile ff : fs) {
				if(filter.accept(ff) && ff.isFile()) {
					receiveFtpFiles(client, localPath, ff.getName(), ff, overlap);
				}
			}
		}
		return true;
	}
	
	/**
	 * 下载文件
	 * @param client		FTP client
	 * @param ftpDirectory	FTP 文件目录
	 * @param localPath		本地文件路径
	 * @param overlap		如果文件存在, 是否替换
	 * @param allowSuffixs	允许的文件后缀名
	 * @return
	 */
	public static boolean downLoadFiles(FTPClient client, String ftpDirectory, String localPath, boolean overlap, final String... allowSuffixs) {
		FTPFileFilter filter = getAllowSuffixFilter(allowSuffixs);
		return downLoadFiles(client, ftpDirectory, localPath, overlap, filter);
	}
	
	public static boolean downLoadFiles(FTPClient client, String ftpDirectory, String localPath, final String... allowSuffixs) {
		return downLoadFiles(client, ftpDirectory, localPath, true, allowSuffixs);
	}
	
	/**
	 * 遍历下载文件夹文件 <i style='color: red;'>(文件夹过滤器和目录过滤器全选, 会先匹配文件夹过滤器, 然后匹配该目录下文件过滤器)</i>
	 * @param client		FTP client
	 * @param ftpDirectory	FTP 根目录
	 * @param loopDir		是否保持跟原目录子结构一致
	 * @param localPath		本地下载路径
	 * @param overlap		如果文件存在是否覆盖
	 * @param dirFilter		目录过滤器
	 * @param fileFilter	文件过滤器
	 * @return
	 * @throws Exception
	 */
	private static boolean loopDownLoads(FTPClient client, String ftpDirectory, boolean loopDir, String localPath, boolean overlap, FTPFileFilter dirFilter, FTPFileFilter fileFilter) throws Exception {
		boolean changed = changeWorkingDirectory(client, ftpDirectory);	// 转移到FTP服务器目录
		if(!changed) {
			return false;
		}
		
		FTPFile[] fs = client.listFiles();
		if(fs == null || fs.length == 0) {
			return false;
		}
		
//		File dir = FileUtils.ensureDirs(localPath);	// 校验并创建本地文件夹
		
		if(dirFilter == null && fileFilter == null) {
			for (FTPFile ff : fs) {
				// 目录
				if(ff.isDirectory()) {
					String newFtpDirectory = getNewFtpDirName(ftpDirectory, ff.getName());
					if(loopDir) {
						loopDownLoads(client, newFtpDirectory, loopDir, getNewFileName(localPath, ff.getName()), overlap, dirFilter, fileFilter);
					} else {
						loopDownLoads(client, newFtpDirectory, loopDir, localPath, overlap, dirFilter, fileFilter);
					}
				}
				
				if(ff.isFile()) {
					
					receiveFtpFiles(client, localPath, ff.getName(), ff, overlap);
				}
			}
		} else {
			if(dirFilter != null && fileFilter != null) {
				// 文件过滤器和文件夹过滤器
				for (FTPFile ff : fs) {
					// 目录
					
					if(ff.isDirectory()) {
						String newFtpDirectory = getNewFtpDirName(ftpDirectory, ff.getName());
						FTPFileFilter newDirFilter = dirFilter.accept(ff)? null: dirFilter;
						if(loopDir) {
							loopDownLoads(client, newFtpDirectory, loopDir, getNewFileName(localPath, ff.getName()), overlap, newDirFilter, fileFilter);
						} else {
							loopDownLoads(client, newFtpDirectory, loopDir, localPath, overlap, newDirFilter, fileFilter);
						}
					}
				}
			} else {
				
				if(dirFilter != null) {
					// 文件夹过滤器不为空
					for (FTPFile ff : fs) {
						if(ff.isDirectory() && dirFilter.accept(ff)) {
							// 当前是目录, 且通过文件过滤器
							String newFtpDirectory = getNewFtpDirName(ftpDirectory, ff.getName());
							FTPFileFilter newDirFilter = dirFilter.accept(ff)? null: dirFilter;
							if(loopDir) {
								loopDownLoads(client, newFtpDirectory, loopDir, getNewFileName(localPath, ff.getName()), overlap, newDirFilter, null);
							} else {
								loopDownLoads(client, newFtpDirectory, loopDir, localPath, overlap, newDirFilter, null);
							}
						}
					}
				} else if(fileFilter != null) {
					// 文件过滤器不为空
					for (FTPFile ff : fs) {
						// 目录
						
						if(ff.isDirectory()) {
							String newFtpDirectory = getNewFtpDirName(ftpDirectory, ff.getName());
							if(loopDir) {
								loopDownLoads(client, newFtpDirectory, loopDir, getNewFileName(localPath, ff.getName()), overlap, null, fileFilter);
							} else {
								loopDownLoads(client, newFtpDirectory, loopDir, localPath, overlap, null, fileFilter);
							}
						}
						
						if(ff.isFile() && fileFilter.accept(ff)) {
							
							receiveFtpFiles(client, localPath, ff.getName(), ff, overlap);
						}
					}
				}
			}
			
		}
		return true;
	}
	
	/**
	 * FTP文件下载到本地
	 * @param client		FTP client
	 * @param localFilePath	本地文件完整路径
	 * @param ftpFile		FTP file
	 * @param overlap		如果存在是否替换本地文件
	 * @throws Exception
	 */
	private static void receiveFtpFiles(FTPClient client, String localPath, String fileName, FTPFile ftpFile, boolean overlap) throws Exception {
		FileUtils.ensureDirs(localPath);
		receiveFtpFiles(client, getNewFileName(localPath, fileName), ftpFile, overlap);
	}
	
	private static void receiveFtpFiles(FTPClient client, String localFilePath, FTPFile ftpFile, boolean overlap) throws Exception {
		File localFile = new File(localFilePath);
		
		if(localFile.exists() && !overlap) 
			return;
		OutputStream is = null;
		try {
			is = new FileOutputStream(localFile);
			client.retrieveFile(ftpFile.getName(), is);
		} finally {
			closeStream(is);
		}
	}
	
	/**
	 * 根据FTP路径获取输入流
	 * @param client
	 * @param remote
	 * @return
	 */
	public static InputStream getFtpInputStream(FTPClient client, String remote) {
		if(client == null || StringUtils.isBlank(remote)) {
			return null;
		}
		try {
			
			return client.retrieveFileStream(remote);
		} catch(Exception e) {
			logger.error("", e);
			return null;
		}
	}
	
	/**
	 * 根据FTP路径获取输入流
	 * @param client
	 * @param ftpUrl
	 * @return
	 */
	public static InputStream getFtpUrlInputStream(FTPClient client, String ftpUrl) {
		String[] ftpInfo = getFtpUrlInfo(ftpUrl);
		if(ftpInfo == null || ftpInfo[2] == null) {
			return null;
		}
		return getFtpInputStream(client, ftpInfo[2]);
	}
	
	/**
	 * 获取FTP 路径基本信息 [IP, 端口, 后路径]
	 * @param ftpUrl
	 * @return [IP, PORT, URI]
	 */
	public static String[] getFtpUrlInfo(String ftpUrl) {
		
		if(ftpUrl == null || !ftpUrl.toLowerCase().startsWith(FTP_HEAD)) {
			return null;
		}
		String[] arr = new String[3];
		int head = FTP_HEAD.length();		// FTP头长度
		int mh = ftpUrl.indexOf(":", head);	// 
		int xg = ftpUrl.indexOf("/", head);	// 
		int ipEnd = getInt(mh, xg, -1 );
		arr[0] = (ipEnd == -1)?null:ftpUrl.substring(FTP_HEAD.length(), ipEnd);	// 获取IP
		arr[1] = mh == -1? ""+DEFAULT_PORT: (xg == -1? ftpUrl.substring(mh+1): ftpUrl.substring(mh+1, xg));	// 获取端口
		arr[2] = xg == -1? null:ftpUrl.substring(xg);	// 获取后路径
		return arr;
	}
	
	/**
	 * <pre>
	 * 如果a == notEq 且b == notEq return notEq
	 * 如果a != notEq return a
	 * 如果a == notEq 且b != notEq return b </pre>
	 * @param a
	 * @param b
	 * @param notEq
	 * @return
	 */
	private static int getInt(int a, int b, int notEq) {
		return (a == notEq) ? (b == notEq? notEq:b):a;
	}
	
	
	/**
	 * @param dir
	 * @param child
	 * @return
	 */
	private static String getNewFileName(String dir, String child) {
		return (dir.endsWith("/")||dir.endsWith("\\") || dir.endsWith(File.separator))?dir + child: dir + File.separator + child;
	}
	
	/**
	 * 获取新FTP目录
	 * @param dir	根目录
	 * @param child	子目录
	 * @return
	 */
	private static String getNewFtpDirName(String dir, String child) {
		return (dir.endsWith("/")||dir.endsWith("\\"))?dir + child: dir + "/" + child;
	}
	
	/**
	 * 遍历下载文件夹文件 <i style='color: red;'>(文件夹过滤器和目录过滤器全选, 会先匹配文件夹过滤器, 然后匹配该目录下文件过滤器)</i>
	 * @param client		FTPClient
	 * @param ftpDirectory	FTP目录
	 * @param localPath		本地根路径
	 * @param keepDir		是否保持跟原目录子结构一致
	 * @param overlap		如果文件存在是否覆盖
	 * @param dirFilter		目录过滤器
	 * @param fileFilter	文件过滤器
	 * @return
	 */
	public static boolean loopDownLoadFiles(FTPClient client, String ftpDirectory, String localPath, boolean keepDir, boolean overlap, FTPFileFilter dirFilter, FTPFileFilter fileFilter) {
		if(client == null ) 
			return false;
		try {
			return loopDownLoads(client, ftpDirectory, keepDir, localPath, overlap, dirFilter, fileFilter);
		} catch (Exception e) {
			logger.error("FTP服务器下载对账文件失败：", e);
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 遍历下载文件夹文件, 如果文件存在则覆盖 <i style='color: red;'>(文件夹过滤器和目录过滤器全选, 会先匹配文件夹过滤器, 然后匹配该目录下文件过滤器)</i>
	 * @param client		FTPClient
	 * @param ftpDirectory	FTP目录
	 * @param localPath		本地根路径
	 * @param keepDir		是否保持跟原目录子结构一致
	 * @param dirFilter		目录过滤器
	 * @param fileFilter	文件过滤器
	 * @return
	 */
	public static boolean loopDownLoadFiles(FTPClient client, String ftpDirectory, String localPath, boolean keepDir, FTPFileFilter dirFilter, FTPFileFilter fileFilter) {
		return loopDownLoadFiles(client, ftpDirectory, localPath, keepDir, true, dirFilter, fileFilter);
	}
	
	public static void main1(String[] args) throws Exception {
//		FTPClient client = FtpUtils.connectFtpServer( "21.41.15.24", "ftptest", "123456");
//		String ftpDirectory = "/chengxinde2/bxyx/050/20170529/违章图片目录/";
//		String ftpDirectory = "/baokang/chd/20170711/";
		
		FTPClient client = FtpUtils.connectFtpServer( "192.168.141.70", "lebron", "123456");
		String ftpDirectory = "/ftpdir/";
		
		List<FTPFile> files = FtpUtils.getFtpFiles(client, ftpDirectory, "txt", "avi");
		for (FTPFile f : files) {
			System.out.println(f.getName());
		}
		
		FtpUtils.downLoadFiles(client, ftpDirectory, "D:/FTPDIRECTORY", false, getAllowSuffixFilter("txt", "jpg"));
		
		FtpUtils.loopDownLoadFiles(client, ftpDirectory, "D:/FTPDIRECTORYs", true, true, getEqFileNameFilter("502", "506"), getAllowSuffixFilter("txt", "jpg"));
		disConnect(client);
	}
	
	
	public boolean downLoadFiles(String ftpDirectory, String localPath, boolean overlap) {
		return downLoadFiles(ftpClient, ftpDirectory, localPath, overlap);
	}
	
	
	/** 过滤器定义 **/
	
	/**
	 * 获取允许文件后缀名过滤器
	 * @param suffixs	文件后缀名数组
	 * @return
	 */
	public static FTPFileFilter getAllowSuffixFilter(final String... suffixs) {
		FTPFileFilter filter = new FTPFileFilter() {
			
			@Override
			public boolean accept(FTPFile ftpFile) {
				// TODO Auto-generated method stub
				if(ftpFile == null || suffixs == null || suffixs.length == 0) {
					return false;
				}
				for (String suffix : suffixs) {
					if(suffix == null) {
						continue;
					}
					if(ftpFile.getName().toUpperCase().endsWith("." + suffix.toUpperCase())) {
						return true;
					}
				}
				return false;
			}
		};
		return filter;
	}
	
	/**
	 * 获取不允许文件后缀名过滤器
	 * @param suffixs	文件后缀名数组
	 * @return
	 */
	public static FTPFileFilter getNotAllowSuffixFilter(final String... suffixs) {
		FTPFileFilter filter = new FTPFileFilter() {
			
			@Override
			public boolean accept(FTPFile ftpFile) {
				// TODO Auto-generated method stub
				if(ftpFile == null) {
					return false;
				}
				if(suffixs == null || suffixs.length == 0) {
					return true;
				}
				for (String suffix : suffixs) {
					if(suffix == null) {
						continue;
					}
					if(ftpFile.getName().toUpperCase().endsWith("." + suffix.toUpperCase())) {
						return false;
					}
				}
				return true;
			}
		};
		return filter;
	}
	
	/**
	 * 获取文件名一致过滤器
	 * @param fileNames
	 * @return
	 */
	public static FTPFileFilter getEqFileNameFilter(final String... fileNames) {
		return new FTPFileFilter() {
			
			@Override
			public boolean accept(FTPFile ftpFile) {
				// TODO Auto-generated method stub
				if(ftpFile == null || fileNames == null || fileNames.length == 0) {
					return false;
				}
				for (String name : fileNames) {
					if(name == null) 
						continue;
					if(name.equals(ftpFile.getName())) {
						return true;
					}
				}
				return false;
			}
		};
	}
	
	
	/** 资源关闭 **/
	
	/**
	 * 关闭FTP连接
	 * @return
	 */
	public boolean disConnect() {
		return disConnect(ftpClient);
	}
	
	/**
	 * 关闭FTP连接
	 * @param ftpClient
	 * @return
	 */
	public static boolean disConnect(FTPClient ftpClient) {
		if(ftpClient != null && ftpClient.isConnected()) {
			try {
				ftpClient.logout();
				ftpClient.disconnect();
				return true;
			} catch(Exception e) {
				logger.error("关闭ftp连接异常: ", e);
			}
		}
		return false;
	}
	
	/**
	 * 关闭输入流
	 * @param is
	 * @return
	 */
	public static boolean closeStream(InputStream is) {
		if(is != null) {
			try {
				
				is.close();
			} catch(Exception e) {
				logger.error("关闭流异常: ", e);
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 关闭输出流
	 * @param os
	 * @return
	 */
	public static boolean closeStream(OutputStream os) {
		if(os != null) {
			try {
				
				os.close();
			} catch(Exception e) {
				logger.error("关闭流异常: ", e);
				return false;
			}
		}
		return true;
	}

}
