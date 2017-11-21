package com.tiger.biz.job;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	public static void main(String[] args) throws UnknownHostException,
			IOException, InterruptedException {
		Socket socket = new Socket("127.0.0.1", 3500);
		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream()));
		BufferedReader br = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		int i = 0;
		while (true) {
			// 发送信息时，需要加上换行符，否则服务器端的readline()会阻塞
			wr.write("你好，您收到客户端的新年祝福[" + i++ + "]\n");
			// 使用flush方法可以立即清空buffer，让消息马上发出去，否则在buffer满之前消息都不会发出去
			wr.flush();

			String line = br.readLine();
			System.out.println("来自服务器的数据：" + line);
			Thread.sleep(1000);
		}
	}
}
