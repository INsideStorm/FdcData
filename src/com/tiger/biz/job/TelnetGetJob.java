package com.tiger.biz.job;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class TelnetGetJob {

	public static ArrayList<String> getData() {
		
		ArrayList<String> telnetArrayList = new ArrayList<>();
		try {
			Socket socket = new Socket("127.0.0.1", 3500);

			// 获取Socket的输出流，用来发送数据到服务器端
//			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
//					socket.getOutputStream()));

			// 获取socket的输入流，用来接收从服务器端发送过来的数据
			BufferedReader br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			
			while (true) {

				for (int i = 0; i < 200; i++) {
					String line = br.readLine();
					telnetArrayList.add(line);
				}

			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return telnetArrayList;

	}

}
