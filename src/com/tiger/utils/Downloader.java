package com.tiger.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.activation.MimetypesFileTypeMap;

import org.apache.log4j.Logger;


/**
 * 实现文件下载
 * 
 * @author zhaoqy
 * 
 */
public class Downloader {
	protected static Logger logger = Logger.getLogger(Downloader.class);

	public static final String DEFAULT_DOWNLOAD_FILENAME = "xjdownloadfile";
	private static final MimetypesFileTypeMap mimes = new MimetypesFileTypeMap();

	/**
	 * 处理中文文件名称，防止出现乱码
	 * 
	 * @param fileName
	 * @return
	 */
	private static String encodeFileName(String fileName) {
		try {
			return URLEncoder.encode(fileName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return fileName;
	}

	/**
	 * 下载
	 * 
	 * @param file
	 * @param urlString
	 */
	public static void download(File file, String urlString) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		URL url = null;
		HttpURLConnection httpUrl = null;
		try {
			url = new URL(UrlUtil.encodeURI(urlString));
			httpUrl = (HttpURLConnection) url.openConnection();
			httpUrl.connect();
			
			bis = new BufferedInputStream(httpUrl.getInputStream());
			bos = new BufferedOutputStream(new FileOutputStream(file));
			
			 int len = 2048;
			 byte[] b = new byte[len];
			 while ((len = bis.read(b)) != -1)
			 {
			     bos.write(b, 0, len);
			 }
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
		} finally {
			if (bos != null) {
				try {
					bos.flush();
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			StreamUtils.closeStream(bis);
			if (httpUrl != null) {
				httpUrl.disconnect();
			}
		}
	}
	

	/**
	 * 简单的从给定的url下载单个文件
	 * 
	 * @param url 要下载的url:
	 *            http://www.chinasofti.com/cms/cms/upload/info/201006/1552/1275638620804.jpg
	 * @param savePath 保存地址: d:/picture
	 * @param useOriginalName	是否使用链接的文件名命名
	 * @throws MalformedURLException
	 */
	public static void downloadFromNet(String url, String savePath, boolean useOriginalName, int timeout)
			throws MalformedURLException {
		// 下载网络文件
//		int bytesum = 0;
		int byteread = 0;

		if(useOriginalName) {
			savePath += url.substring(url.lastIndexOf("/"));
		}
		FileOutputStream fs = null;
		InputStream inStream = null;
		try {
			inStream = getInputStreamFromNet(url, timeout);
			if(inStream == null) {
				return;
			}
			
			fs = new FileOutputStream(savePath);

			byte[] buffer = new byte[1204];
			while ((byteread = inStream.read(buffer)) != -1) {
//				bytesum += byteread;
				fs.write(buffer, 0, byteread);
			}
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			StreamUtils.closeStreams(fs, inStream);
		}
	}
	
	public static InputStream getInputStreamFromNet(String url, int timeout) {
		if(url == null || "".equals(url)) {
			return null;
		}
		// 下载网络文件
		try {
			URL urls = new URL(UrlUtil.encodeURI(url));
			URLConnection conn = urls.openConnection();
			conn.setConnectTimeout(timeout);
			return conn.getInputStream();
		} catch (IOException e) {
			logger.error("", e);
		}
		return null;
	}
	
	/**
	 * 获取Url中的文件名
	 * @param url
	 * @return
	 */
	public static String getUrlFileName(String url) {
		return url == null?null: url.substring(url.lastIndexOf("/"));
	}

	// 将InputStream直接转换为Reader
	public static class ReaderInputStream extends InputStream {
		protected Reader reader;
		protected ByteArrayOutputStream byteArrayOut;
		protected Writer writer;
		protected char[] chars;
		protected byte[] buffer;
		protected int index, length;

		/**
		 * 带Reader参数构造函数
		 * 
		 * @param reader - InputStream使用的Reader
		 */
		public ReaderInputStream(Reader reader) {
			this.reader = reader;
			byteArrayOut = new ByteArrayOutputStream();
			writer = new OutputStreamWriter(byteArrayOut);
			chars = new char[1024];
		}

		/**
		 * 带Reader和字符编码格式参数的构造函数
		 * 
		 * @param reader - InputStream使用的Reader
		 * @param encoding - InputStream使用的字符编码格式.
		 * @throws 如果字符编码格式不支持,则抛UnsupportedEncodingException异常
		 */
		public ReaderInputStream(Reader reader, String encoding)
				throws UnsupportedEncodingException {
			this.reader = reader;
			byteArrayOut = new ByteArrayOutputStream();
			writer = new OutputStreamWriter(byteArrayOut, encoding);
			chars = new char[1024];
		}

		/**
		 * @see java.io.InputStream#read()
		 */
		public int read() throws IOException {
			if (index >= length)
				fillBuffer();
			if (index >= length)
				return -1;
			return 0xff & buffer[index++];
		}

		protected void fillBuffer() throws IOException {
			if (length < 0)
				return;
			int numChars = reader.read(chars);
			if (numChars < 0) {
				length = -1;
			} else {
				byteArrayOut.reset();
				writer.write(chars, 0, numChars);
				writer.flush();
				buffer = byteArrayOut.toByteArray();
				length = buffer.length;
				index = 0;
			}
		}

		/**
		 * @see java.io.InputStream#read(byte[], int, int)
		 */
		public int read(byte[] data, int off, int len) throws IOException {
			if (index >= length)
				fillBuffer();
			if (index >= length)
				return -1;
			int amount = Math.min(len, length - index);
			System.arraycopy(buffer, index, data, off, amount);
			index += amount;
			return amount;
		}

		/**
		 * @see java.io.InputStream#available()
		 */
		public int available() throws IOException {
			return (index < length) ? length - index : ((length >= 0) && reader.ready()) ? 1 : 0;
		}

		/**
		 * @see java.io.InputStream#close()
		 */
		public void close() throws IOException {
			reader.close();
		}
	}
}
