package Server;

import MyDataBase.*;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;

import Client.Doc;
import Client.User;

public class ClientManager extends ServerSocket {
	private static final int SERVER_PORT = 3000;
	private static String fileServecePath = "d:\\Multithreading\\Files\\";

	public ClientManager() throws IOException {
		super(SERVER_PORT);
		try {
			while (true) {
				Socket socket = accept();
				new CreateServerThread(socket);
			}
		} catch (IOException ioException) {
			System.out.println("等待连接时发生IO异常");
		} finally {
			close();
		}
	}

	// 线程类
	class CreateServerThread extends Thread {
		private Socket client;
		private BufferedReader bufferedReader;
		private PrintWriter printWriter;

		public CreateServerThread(Socket socket) {
			client = socket;
			try {
				bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (IOException ioException) {
				System.out.println("输出流获取异常");
				try {
					client.close();
				} catch (IOException e) {
					System.out.println("Socket关闭异常");
				}
				return;
			}
			try {
				printWriter = new PrintWriter(socket.getOutputStream(), true);
			} catch (IOException ioException) {
				System.out.println("执行登陆指令时,输出流获取异常");
				return;
			}
			System.out.println("有连接接入");
			start();
		}

		public void run() {
			// 获取从客户端读入的字符串
			String command;
			try {
				command = bufferedReader.readLine();
			} catch (IOException ioException) {
				System.out.println("指令读取异常");
				return;
			}
			try {
				if (command.equals("Login")) {
					String name = bufferedReader.readLine();
					String password = bufferedReader.readLine();
					String role = null;
					ResultSet resultSet;
					resultSet = DataBaseProcessing.searchUser(name);
					if (resultSet.next()) {
						role = resultSet.getString(3).trim();
						if (password.equals(resultSet.getString(2).trim())) {
							printWriter.println(role);
							printWriter.flush();
							if (role.equals("Administrator")) {
								transmitUserInfo(client);
								printWriter.println("Finished");
								printWriter.flush();
							}
							transmitDocInfo(client);
							System.out.println(role + "用户:" + name + "登陆系统");
						} else {
							System.out.println(role + "用户:" + name + "密码错误,登陆失败");
						}
					} else {
						System.out.println("未知用户:" + name + "访问系统");
					}
				} else if (command.equals("UpdateUser")) {
					String name = bufferedReader.readLine();
					String password = bufferedReader.readLine();
					String role = bufferedReader.readLine();
					DataBaseProcessing.updateUser(new User(name, password, role));
				} else if (command.equals("InsertUser")) {
					String name = bufferedReader.readLine();
					String password = bufferedReader.readLine();
					String role = bufferedReader.readLine();
					DataBaseProcessing.insertUser(new User(name, password, role));
				} else if (command.equals("DeleteUser")) {
					String name = bufferedReader.readLine();
					DataBaseProcessing.deleteUser(name);
				} else {
					try {
						if (command.equals("DownloadFile")) {
							// 传输之前先读文档ID
							String iD = bufferedReader.readLine();
							transmitOutDoc(client, iD);
						} else if (command.equals("UploadFile")) {
							// 接收文档之前先读文档详细信息
							String[] infos = new String[5];
							for (int i = 0; i < 5; i++) {
								infos[i] = bufferedReader.readLine();
								// 已确定检查都收到(271MB,68MB,更小的文件)
								System.out.println(infos[i]);
							}
							transmitInDoc(client, new Doc(infos[0], infos[1], infos[2], infos[3], infos[4]));
						} else {
							System.out.println("切断一个未知连接");
						}
					} catch (IOException ioException) {
						System.out.println("档案库操作异常");
					} catch (SQLException sqlException) {
						System.out.println("User_doc数据库操作异常");
					}
				}
			} catch (SQLException e) {
				System.out.println("User_info数据库操作异常");
			} catch (IOException e) {
				System.out.println("指令后续信息获取失败");
			}
			try {
				client.close();
			} catch (IOException e) {
				System.out.println("Socket关闭异常");
			}
			System.out.println("Socket连接已断开");
		}
	}

	private void transmitOutDoc(Socket socket, String iD) throws SQLException, IOException {
		final int BytesEachTime = 10000;// 一次读入的最大数据
		String filename;
		ResultSet docResult = DataBaseProcessing.searchDoc(iD);
		if (docResult.next()) {
			filename = docResult.getString(5).trim();
		} else {
			System.out.println("文档" + iD + "匹配异常");
			return;
		}
		File filetoDownload = new File(
				fileServecePath + iD + "." + filename.substring(filename.lastIndexOf(".") + 1, filename.length()));
		DataInputStream dataInputStream = new DataInputStream(new FileInputStream(filetoDownload));
		DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
		// 先传输文件大小

		long fileSize = filetoDownload.length();
		// TODO Test
		System.out.println(fileSize);

		dataOutputStream.writeLong(fileSize);
		// 再传输文件
		// 传输文件
		int length = 0;
		long curLength = 0;
		float currentProgress = 0;
		byte[] sendBytes = new byte[BytesEachTime];
		while ((length = dataInputStream.read(sendBytes, 0, sendBytes.length)) > 0) {
			dataOutputStream.write(sendBytes, 0, length);
			curLength = curLength + length;
			currentProgress = (float) ((double) curLength / (double) fileSize * 100.0);
			System.out.println("文档" + iD + "已传输" + currentProgress + "%");
		}
		dataOutputStream.flush();
		System.out.println("文件传输完毕!");
		try {
			dataInputStream.close();
		} catch (IOException ioException) {
			System.out.println("文件流关闭异常");
		}
		// socket 会在构造函数中关闭
	}

	private void transmitInDoc(Socket socket, Doc doc) throws SQLException, IOException {
		final int BytesEachTime = 10000;// 一次读入的最大数据
		String filename = doc.getFilename();
		File filetoSave = new File(fileServecePath + doc.getID() + "."
				+ filename.substring(filename.lastIndexOf(".") + 1, filename.length()));
		// 存入文档信息
		DataBaseProcessing.insertDoc(doc);
		try {
			DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
			DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(filetoSave));
			// 先读文件大小
			// TODO bug:文件大小为284778602B(271MB)时错误即显示为5785721462337830912,而68MB时正常
			// 第二次测试(271MB)时显示正常 第三次测试(未记录) 第四次测试异常(显示为-7676423963002534235)
			// 第五次测试正常(此时使用默认描述信息) 第六次测试正常(此时使用默认描述信息)
			// 第七次测试正常(描述信息:123) 第八次测试正常(描述信息:271(与2-4次测试的描述相同))
			// 第九次测试正常(同第一次描述) bug 没有复现,增大文件大小(543MB)--->正常
			// 改用1.85GB-->正常 继续改用2.12GB(大于int上限)(前半段正常,后半段异常)原因(int型变量curLength溢出)
			// 第一次的问题在后来几次没有复现,(疑惑?) 注:测试中遇到其他三个新的bug在client的Main方法中显示
			// 以此次修改为模板,更改其他三处文件传输的代码
			long fileSize = dataInputStream.readLong();
			// 接收文件
			int length = 0;// 当前一次读入的数据 (小于等于BytesEachTime,因此数据类型与其一致)
			long curLength = 0;// 已上传的
			float currentProgress = 0;
			byte[] sendBytes = new byte[BytesEachTime];
			while ((length = dataInputStream.read(sendBytes, 0, sendBytes.length)) > 0) {
				dataOutputStream.write(sendBytes, 0, length);
				curLength = curLength + length;
				// Fixed bug: currentProgress = (curLength * 100 / fileSize);
				// 原因:乘以一百容易导致溢出(测试大小71336566B)
				currentProgress = (float) ((double) curLength / (double) fileSize * 100.0);
				System.out.println("当前长度 = " + curLength);
				System.out.println("文件长度 = " + fileSize);
				System.out.println("进度 = " + currentProgress);
				System.out.println("文档" + doc.getID() + "已传输" + currentProgress + "%");
			}

			System.out.println("文件接收完毕!");
			try {
				dataOutputStream.close();
			} catch (IOException ioException) {
				System.out.println("文件流关闭异常");
			}
		} catch (IOException ioException) {
			System.out.println("文档" + doc.getID() + "传输失败!");
			// 删除错误文档
			if (filetoSave.exists()) {
				filetoSave.delete();
			}
			// 删除错误文档信息
			try {
				DataBaseProcessing.deleteDocInfo(doc.getID());
			} catch (SQLException sqlException) {
				System.out.println("错误文档信息删除失败,建议手动删除,以免系统异常");
			}
			// 抛出异常以表明文件接收失败
			throw new IOException();
		}
		// socket 会在构造函数中关闭
	}

	private boolean transmitUserInfo(Socket socket) {
		ResultSet resultSet;
		PrintWriter printWriter;
		String name, password, role;
		try {
			printWriter = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e1) {
			System.out.println("获取链接的输出流时出错");
			return false;
		}
		try {
			resultSet = DataBaseProcessing.getUserInfo();
			while (resultSet.next()) {
				name = resultSet.getString(1).trim();
				password = resultSet.getString(2).trim();
				role = resultSet.getString(3).trim();
				assert (name == null || password == null || role == null);
				printWriter.println(name);
				printWriter.println(password);
				printWriter.println(role);
				printWriter.flush();
			}
		} catch (SQLException sqlException) {
			System.out.println("数据库操作出错");
			return false;
		}
		return true;
	}

	private boolean transmitDocInfo(Socket socket) {
		ResultSet resultSet;
		PrintWriter printWriter;
		String ID, creator, timestamp, description, filename;
		try {
			printWriter = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e1) {
			System.out.println("获取链接的输出流时出错");
			return false;
		}
		try {
			resultSet = DataBaseProcessing.getDocInfo();
			while (resultSet.next()) {
				ID = resultSet.getString(1).trim();
				creator = resultSet.getString(2).trim();
				timestamp = resultSet.getString(3).trim();
				description = resultSet.getString(4).trim();
				filename = resultSet.getString(5).trim();
				assert (ID == null || creator == null || timestamp == null || description == null || filename == null);
				printWriter.println(ID);
				printWriter.println(creator);
				printWriter.println(timestamp);
				printWriter.println(description);
				printWriter.println(filename);
				printWriter.flush();
			}
		} catch (SQLException sqlException) {
			System.out.println("数据库操作出错");
			return false;
		}
		return true;
	}
}
