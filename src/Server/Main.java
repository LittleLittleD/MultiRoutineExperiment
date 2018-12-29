package Server;

import MyDataBase.*;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
	public static void main(String[] args) {
		try {
			DataBaseProcessing.Init();
			try {
				new ClientManager();
			} catch (IOException e) {
				System.out.println("ServerSocket(开启/关闭)异常");
			}
		} catch (SQLException e) {
			System.out.println("数据库连接异常");
		} catch (ClassNotFoundException e) {
			System.out.println("驱动启动失败");
		} finally {
			try {
				DataBaseProcessing.dataBaseQuit();
			} catch (SQLException e) {
				System.out.println("数据库关闭异常");
			}
		}

	}
}
