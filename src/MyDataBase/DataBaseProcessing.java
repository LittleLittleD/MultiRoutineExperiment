package MyDataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Client.Doc;
import Client.User;

public class DataBaseProcessing {
	static Connection dataConnection;
	static Statement sqlTools;

	public static void Init() throws ClassNotFoundException, SQLException {
		// 初始化SQL
//		String sqlCommand;
		String url = "jdbc:mysql://localhost:3306/document?" + "user=root&password=" + "123456"
				+ "&useUnicode=true&characterEncoding=UTF8&serverTimezone=GMT";
		Class.forName("com.mysql.cj.jdbc.Driver");// 动态加载mysql驱动
		System.out.println("驱动正常启动");
		// Class.forName("com.mysql.jdbc.Driver");
		// 报错显示:数据库驱动com.mysql.jdbc.Driver已经被弃用了,应当使用新的驱动com.mysql.cj.jdbc.Driver
		dataConnection = DriverManager.getConnection(url);
		System.out.println("数据库连接正常");
		sqlTools = dataConnection.createStatement();
		System.out.println("数据库工具正常加载");
//      // 本地版
//		// 初始化users //在联机版的信息传输中复用
//		{
//			DataProcessing.users = new Hashtable<String, User>();
//			String name, password, role;
//			sqlCommand = "select * from user_info";
//			ResultSet dataBaseUserInfo = sqlTools.executeQuery(sqlCommand);
//			while (dataBaseUserInfo.next()) {
//				name = dataBaseUserInfo.getString(1).trim();
//				password = dataBaseUserInfo.getString(2).trim();
//				role = dataBaseUserInfo.getString(3).trim();
//				if (name == null || password == null || role == null) {
//					throw new DataException("数据错误！");
//				}
//				if (role.equalsIgnoreCase("Operator") || role.equalsIgnoreCase("Browser")
//						|| role.equalsIgnoreCase("Administrator"))
//					DataProcessing.users.put(name, new User(name, password, role));
//				else {
//					throw new DataException("数据错误！");
//				}
//			}
//			dataBaseUserInfo.close();
//		}
//
//		// 初始化docs //在联机版的信息传输中复用
//		{
//			DataProcessing.docs = new Hashtable<String, Doc>();
//			String ID, creator, timestamp, description, filename;
//			sqlCommand = "select * from doc_info";
//			ResultSet dataBaseDocInfo = sqlTools.executeQuery(sqlCommand);
//			while (dataBaseDocInfo.next()) {
//				ID = dataBaseDocInfo.getString(1).trim();
//				creator = dataBaseDocInfo.getString(2).trim();
//				timestamp = dataBaseDocInfo.getString(3).trim();
//				description = dataBaseDocInfo.getString(4).trim();
//				filename = dataBaseDocInfo.getString(5).trim();
//
//				if (ID == null || creator == null || timestamp == null || description == null || filename == null) {
//					throw new DataException("数据错误！");
//				} else {
//					DataProcessing.docs.put(ID, new Doc(ID, creator, timestamp, description, filename));
//				}
//			}
//			dataBaseDocInfo.close();
//		}

		// 有一个问题:发生异常时,连接是否正常断开?
		// 在每次退出系统时,执行自己编写的systemQuit方法,在该方法中关闭
	}

	// 用户信息处理

	// 返回所有用户信息
	public static ResultSet getUserInfo() throws SQLException {
		String sqlCommand = "select * from user_info";
		return sqlTools.executeQuery(sqlCommand);
	}

	// 查找用户
	public static ResultSet searchUser(String userName) throws SQLException {
		String sqlCommand = "select * from user_info where username = '" + userName + "'";
		return sqlTools.executeQuery(sqlCommand);
	}

	// 更新用户
	public static boolean updateUser(User user) throws SQLException {
		// updateUserFile();
		String sqlCommand = "update user_info set password = " + user.getPassword() + ", role = '" + user.getRole()
				+ "' where username = '" + user.getName() + "'";
		if (sqlTools.executeUpdate(sqlCommand) != -1) {
			return true;
		} else {
			return false;
		}
	}

	// 添加用户
	public static boolean insertUser(User user) throws SQLException {
		// updateUserFile();
		String sqlCommand = "insert into user_info values('" + user.getName() + "','" + user.getPassword() + "','"
				+ user.getRole() + "')";
		// executeUpdate语句会返回一个受影响的行数，如果返回-1就没有成功
		if (sqlTools.executeUpdate(sqlCommand) != -1) {
			return true;
		} else {
			return false;
		}
	}

	// 删除用户
	public static boolean deleteUser(String name) throws SQLException {

		// updateUserFile();
		String sqlCommand = "delete from user_info where username='" + name + "'";
		if (sqlTools.executeUpdate(sqlCommand) != -1) {
			return true;
		} else {
			return false;
		}
	}

	// 档案信息处理

	// 获取数据库中的档案信息
	public static ResultSet getDocInfo() throws SQLException {
		String sqlCommand = "select * from doc_info";
		return sqlTools.executeQuery(sqlCommand);
	}

	// 查找档案信息
	public static ResultSet searchDoc(String iD) throws SQLException {
		String sqlCommand = "select * from doc_info where iD = " + iD;
		return sqlTools.executeQuery(sqlCommand);
	}

	// 增加新的档案文件信息
	public static boolean insertDoc(Doc doc) throws SQLException {
		// updateDocFile();
		String sqlCommand = "insert into doc_info values('" + doc.getID() + "','" + doc.getCreator() + "','"
				+ doc.getTimestamp() + "','" + doc.getDescription() + "','" + doc.getFilename() + "')";
		if (sqlTools.executeUpdate(sqlCommand) != -1) {
			return true;
		} else {
			return false;
		}

	}

	// 删除文档
	public static boolean deleteDocInfo(String Id) throws SQLException {
		String sqlCommand = "delete from doc_info where Id = " + Id;
		if (sqlTools.executeUpdate(sqlCommand) != -1) {
			return true;
		} else {
			return false;
		}
	}

	// 关闭连接和声明
	public static void dataBaseQuit() throws SQLException {
		sqlTools.close();
		dataConnection.close();
	}
}
