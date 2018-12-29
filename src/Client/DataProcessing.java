package Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.*;

//说明:实验二使用的 DataProcessing类
public class DataProcessing {
	final static String cookiePath = "./cookie.dll";
	public static Hashtable<String, User> users;
	public static Hashtable<String, Doc> docs;
	static String[] cookies;

	// 本地初始化
	public static void initLocalCookies() throws IOException, DataException {
		cookies = null;
		String savedName, savedPassword, savedState;
		File cookieFile = new File(cookiePath); // cookie.dll中含有用户名,密码,登陆方式
		if (!cookieFile.exists()) {
			cookieFile.createNewFile();
		} else if (cookieFile.getTotalSpace() != 0) {
			BufferedReader cookieReader = new BufferedReader(new FileReader(cookieFile));
			if ((savedName = cookieReader.readLine()) != null) {
				savedPassword = cookieReader.readLine();
				savedState = cookieReader.readLine();
				if (savedPassword == null || savedState == null || !savedState.matches("Remember|Auto")
						|| cookieReader.readLine() != null) {
					cookieReader.close();
					throw new DataException("数据错误!");
				} else {
					cookies = new String[4];
					cookies[0] = savedName;
					cookies[1] = savedPassword;
					cookies[3] = savedState;
				}
			}
			cookieReader.close();
		}
	}

	// 联机初始化UserInfo
	public static void initUserInfo(BufferedReader bufferedReader) throws IOException, DataException {
		users = new Hashtable<String, User>();
		String name, password, role;
		while (!(name = bufferedReader.readLine()).equals("Finished")) {
			password = bufferedReader.readLine();
			role = bufferedReader.readLine();
			if (password == null || role == null) {
				bufferedReader.close();
				throw new DataException("数据错误！");
			}
			User user = new User(name, password, role);
			if (role.equals("Operator") || role.equals("Browser") || role.equals("Administrator"))
				users.put(name, user);
			else {
				bufferedReader.close();
				throw new DataException("数据错误！");
			}
		}
	}

	// 联机初始化DocInfo
	public static void initDocInfo(BufferedReader bufferedReader) throws IOException, DataException {
		docs = new Hashtable<String, Doc>();
		String ID, creator, timestamp, description, filename;
		while ((ID = bufferedReader.readLine()) != null) {
			creator = bufferedReader.readLine();
			timestamp = bufferedReader.readLine();
			description = bufferedReader.readLine();
			filename = bufferedReader.readLine();
			if (creator == null || timestamp == null || description == null || filename == null) {
				bufferedReader.close();
				throw new DataException("文件列表读取异常！");
			}
			docs.put(ID, new Doc(ID, creator, timestamp, description, filename));
		}
		bufferedReader.close();
	}
//	public static void Init() throws IOException, DataException, ClassNotFoundException, SQLException {
//		// 初始化users 
//		users = new Hashtable<String, User>();
//		String name, password, role;
//		BufferedReader br = new BufferedReader(new FileReader("d:\\Multithreading\\user.txt"));
//		while ((name = br.readLine()) != null) {
//			password = br.readLine();
//			role = br.readLine();
//			if (password == null || role == null) {
//				br.close();
//				throw new DataException("数据错误！");
//			}
//			User user = new User(name, password, role);
//			if (role.equals("Operator") || role.equals("Browser") || role.equals("Administrator"))
//				users.put(name, user);
//			else {
//				br.close();
//				throw new DataException("数据错误！");
//			}
//		}
//		br.close();
//		
//		// 初始化doc
//		docs = new Hashtable<String, Doc>();
//		String ID, creator, timestamp, description, filename;
//		
//		BufferedReader fileReader = new BufferedReader(new FileReader("d:\\Multithreading\\fileList.txt"));
//		while ((ID = fileReader.readLine()) != null) {
//			creator = fileReader.readLine();
//			timestamp = fileReader.readLine();
//			description = fileReader.readLine();
//			filename = fileReader.readLine();
//			
//			if (creator == null || timestamp == null || description == null || filename == null) {
//				fileReader.close();
//				throw new DataException("文件列表读取异常！");
//			}
//			
//			docs.put(ID, new Doc(ID, creator, new Long(timestamp).longValue(), description, filename));
//		}
//		fileReader.close();

//		在本地加入数据库后的操作
//		DataBaseProcessing.Init();

	// 初始化cookie
//		cookies = null;
//		String savedName, savedPassword, savedState;
//		File cookieFile = new File("D:\\Multithreading\\cookie.dll"); // cookie.dll中含有用户名,密码,登陆方式
//		if (!cookieFile.exists()) {
//			cookieFile.createNewFile();
//		} else if (cookieFile.getTotalSpace() != 0) {
//			BufferedReader cookieReader = new BufferedReader(new FileReader(cookieFile));
//			if ((savedName = cookieReader.readLine()) != null) {
//				savedPassword = cookieReader.readLine();
//				savedState = cookieReader.readLine();
//				if (savedPassword == null || savedState == null || !savedState.matches("Remember|Auto")
//						|| cookieReader.readLine() != null) {
//					cookieReader.close();
//					throw new DataException("数据错误!");
//				} else {
//					User user = searchByName(savedName);
//					if (user == null) {
//						cookieReader.close();
//						throw new DataException("数据被篡改!");
//					} else {
//						cookies = new String[4];
//						cookies[0] = savedName;
//						cookies[1] = savedPassword;
//						cookies[2] = user.getRole();
//						cookies[3] = savedState;
//					}
//				}
//			}
//			cookieReader.close();
//		}

//	}

	// 下面是用户管理功能函数
	public static User searchUser(String name, String password) {
		if (users.containsKey(name)) {
			User temp = users.get(name);
			if ((temp.getPassword()).equals(password))
				return temp;
		}
		return null;
	}

	public static Enumeration<User> getAllUser() {
		Enumeration<User> e = users.elements();
		return e;
	}

	// 向服务器发送请求和信息
	public static void sendRequestionAndInfo(String requestion, String info) throws UnknownHostException, IOException {
		DataRequestion dataRequestion = new DataRequestion();
		PrintWriter printWriter = dataRequestion.sendRequestion(requestion);
		String[] infos = info.split(MyInfo.separator);
		int numberOfInfo;
		if ("DeleteUser".equals(requestion)) {
			numberOfInfo = 1;
		} else {
			numberOfInfo = 3;
		}
		for (int i = 0; i < numberOfInfo; i++) {
			printWriter.println(infos[i]);
		}
		printWriter.flush();
		dataRequestion.close();
	}

	// 更新用户信息
	public static void updateUser(User user) throws UnknownHostException, IOException {
		// 非管理员不能看到用户信息,此时users是null,该函数只能用来更改密码
		if (users != null) {
			users.put(user.getName(), user);
		}
		sendRequestionAndInfo("UpdateUser", user.toString());
//		return DataBaseProcessing.updateUser(user);
	}

	// 增加新用户
	public static void insertUser(User user) throws UnknownHostException, IOException {
		users.put(user.getName(), user);
		sendRequestionAndInfo("InsertUser", user.toString());
//		return DataBaseProcessing.insertUser(user);
	}

	// 删除用户
	public static void deleteUser(String name) throws UnknownHostException, IOException {
		users.remove(name);
		sendRequestionAndInfo("DeleteUser", name);
//		return DataBaseProcessing.deleteUser(name);
	}

	// 通过名字查找用户
	public static User searchByName(String name) {
		if (users.containsKey(name)) {
			User temp = users.get(name);
			return temp;
		} else
			return null;
	}

	// 将用户信息写入到文件user.txt中，实现用户信息永久保存
//	public static void updateUserFile() throws IOException {
//		BufferedWriter writer = new BufferedWriter(new FileWriter("d:\\Multithreading\\user.txt"));
//		Enumeration<User> users = DataProcessing.getAllUser();
//		while (users.hasMoreElements()) {
//			User user = users.nextElement();
//			writer.write(user.getName() + "\r\n" + user.getPassword() + "\r\n" + user.getRole() + "\r\n");
//		}
//		writer.close();
//	}

	// 下面是档案管理功能函数
	// 找到档案号为ID的档案文件信息
	public static Doc searchDoc(String ID) {
		if (docs.containsKey(ID)) {
			Doc temp = docs.get(ID);
			return temp;
		}
		return null;
	}

	// 提取所有的档案文件信息
	public static Enumeration<Doc> getAllDocs() {
		Enumeration<Doc> enumDoc = docs.elements();
		return enumDoc;
	}

	// 增加新的档案文件信息
	public static void insertDoc(Doc doc) throws UnknownHostException, IOException {
		docs.put(doc.getID(), doc);
		// 上传时自动向数据库添加 在本地添加即可
//		sendRequestionAndInfo("InsertDoc", doc.toString());
//		return DataBaseProcessing.insertDoc(doc);
	}

	// 将档案档案文件的信息写入到文件fileList.txt中
//	public static void updateDocFile() throws IOException {
//		BufferedWriter writer = new BufferedWriter(new FileWriter("d:\\Multithreading\\fileList.txt"));
//		Enumeration<Doc> docs = DataProcessing.getAllDocs();
//		while (docs.hasMoreElements()) {
//			Doc doc = docs.nextElement();
//			writer.write(doc.getID() + "\r\n" + doc.getCreator() + "\r\n" + doc.getTimestamp() + "\r\n"
//					+ doc.getDescription() + "\r\n" + doc.getFilename() + "\r\n");
//		}
//		writer.close();
//	}

	// 更新cookie信息
	public static void updateCookies() throws IOException {
		File cookieFile = new File(cookiePath);
		if (cookies == null)
			cookieFile.delete();
		else {
			BufferedWriter writer = new BufferedWriter(new FileWriter(cookieFile));
			writer.write(cookies[0] + "\r\n" + cookies[1] + "\r\n" + cookies[3] + "\r\n");
			writer.close();
		}
	}

	public static int getUserNumber() {
		return users.size();
	}

	// 返回文件数目
	public static int getDocNumber() {
		return docs.size();
	}

	public static void systemQuit() {
//		try {
//			DataBaseProcessing.dataBaseQuit();
//		} catch (SQLException e) {
//			JOptionPane.showMessageDialog(new JFrame(), e.getMessage() + "数据库连接关闭异常！", "提示",
//					JOptionPane.INFORMATION_MESSAGE);
//		} finally {
		System.exit(0);
//		}
	}

}
