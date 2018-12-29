package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

public class DataRequestion {

	private Socket socket;
	private PrintWriter printWriter;// 用于发送信息

	// 申请与服务器建立链接(长期)
	public DataRequestion() throws UnknownHostException, IOException {
		socket = new Socket("127.0.0.1", 3000);
		printWriter = new PrintWriter(socket.getOutputStream(), true);
	}

	// 向服务器端发送请求
	public PrintWriter sendRequestion(String requestion) throws IOException {
		printWriter.println(requestion);
		printWriter.flush();
		return printWriter;
	}

	// 向服务器端发送下载文档请求(含文档ID)
	public InputStream downloadRequestion(String iD) throws IOException {
		printWriter.println("DownloadFile");
		printWriter.println(iD);
		printWriter.flush();
		return socket.getInputStream();
	}

	// 向服务器端发送上传文档请求(含文档详细信息)
	public OutputStream uploadRequestion(Doc fileInfo) throws IOException {
		printWriter.println("UploadFile");
		String[] infos = fileInfo.toString().split(MyInfo.separator);
		for (int i = 0; i < 5; i++) {
			printWriter.println(infos[i]);
		}
		printWriter.flush();
		return socket.getOutputStream();
	}

	// 发送登陆申请,服务器将返回身份信息,并根据身份返回文档信息和用户信息
	public String loginRequestion(String userName, String userPassword)
			throws SocketException, IOException, TimeoutException, DataException {
		BufferedReader respondReader;
		String respond = null;
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
		printWriter.println("Login");
		printWriter.println(userName);
		printWriter.println(userPassword);
		printWriter.flush();
		respondReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		try {
			socket.setSoTimeout(60000); // 1min超时设定
		} catch (Exception timeOutException) {
			throw new TimeoutException();
		}
		respond = respondReader.readLine();
		// 在此需要根据身份再次接受信息以初始化hashtable
		if (respond != null) {
			if (respond.equals("Administrator")) {
				DataProcessing.initUserInfo(respondReader);
				printWriter.flush();
			}
			DataProcessing.initDocInfo(respondReader);
		} else {
			respond = "Wrong";
		}
		respondReader.close();
		return respond;
	}

	// 关闭链接
	public void close() throws IOException {
		socket.close();
	}
}
