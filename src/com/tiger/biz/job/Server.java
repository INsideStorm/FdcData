package com.tiger.biz.job;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
public class Server {
	public static void main(String[] args) throws IOException {
		// 创建端口30000的服务器socket
		ServerSocket ss = new ServerSocket(3500);
		// 在返回客户端socket之前，accept将会一直阻塞
		Socket s = ss.accept();
		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
				s.getOutputStream()));
		BufferedReader br = new BufferedReader(new InputStreamReader(
				s.getInputStream()));
		int i = 0;
		while (true) {
			// 发送信息时，需要加上换行符，否则客户端的readline()会阻塞
			wr.write("20170720,165549,XJ,新7890,87.607712,42.775537,0,207,1," + i++ + "\n");
			// 使用flush方法可以立即清空buffer，让消息马上发出去，否则在buffer满之前消息都不会发出去
			wr.flush();

//			String line = br.readLine();
//			System.out.println("来自客户端的数据：" + line);
		}
	}
}
