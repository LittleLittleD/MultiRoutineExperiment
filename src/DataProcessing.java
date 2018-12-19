import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

//说明:实验二使用的 DataProcessing类
public class DataProcessing {
	static Hashtable<String, User> users;
	static Hashtable<String, Doc> docs;
	static String[] cookies;

	// 说明: 实验二、三、四 使用此处Init()函数
	public static void Init() throws IOException, DataException {
		// 初始化users
		users = new Hashtable<String, User>();
		String name, password, role;
		BufferedReader br = new BufferedReader(new FileReader("d:\\Multithreading\\user.txt"));
		while ((name = br.readLine()) != null) {
			password = br.readLine();
			role = br.readLine();
			if (password == null || role == null) {
				br.close();
				throw new DataException("数据错误！");
			}
			User user = new User(name, password, role);
			if (role.equals("Operator") || role.equals("Browser") || role.equals("Administrator"))
				users.put(name, user);
			else {
				br.close();
				throw new DataException("数据错误！");
			}
		}
		br.close();

		// 初始化cookie
		cookies = null;
		String savedName, savedPassword, savedState;
		File cookieFile = new File("D:\\Multithreading\\cookie.dll"); // cookie.dll中含有用户名,密码,登陆方式
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
					User user = searchByName(savedName);
					if (user == null) {
						cookieReader.close();
						throw new DataException("数据被篡改!");
					} else {
						cookies = new String[4];
						cookies[0] = savedName;
						cookies[1] = savedPassword;
						cookies[2] = user.getRole();
						cookies[3] = savedState;
					}
				}
			}
			cookieReader.close();
		}

		// 初始化doc
		docs = new Hashtable<String, Doc>();
		String ID, creator, timestamp, description, filename;

		BufferedReader fileReader = new BufferedReader(new FileReader("d:\\Multithreading\\fileList.txt"));
		while ((ID = fileReader.readLine()) != null) {
			creator = fileReader.readLine();
			timestamp = fileReader.readLine();
			description = fileReader.readLine();
			filename = fileReader.readLine();

			if (creator == null || timestamp == null || description == null || filename == null) {
				fileReader.close();
				throw new DataException("文件列表读取异常！");
			}

			docs.put(ID, new Doc(ID, creator, new Long(timestamp).longValue(), description, filename));
		}
		fileReader.close();
	}

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

	// 更新用户信息
	public static boolean updateUser(User user) throws IOException {
		String name = user.getName();
		if (users.containsKey(name)) {
			users.put(name, user);
			updateUserFile();
			return true;
		} else
			return false;
	}

	// 增加新用户
	public static boolean insertUser(User user) throws IOException {
		String name = user.getName();
		if (users.containsKey(name))
			return false;
		else {
			users.put(name, user);
			updateUserFile();
			return true;
		}
	}

	// 删除用户
	public static boolean deleteUser(String name) throws IOException {
		if (users.containsKey(name)) {
			users.remove(name);
			updateUserFile();
			return true;
		} else
			return false;
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
	public static void updateUserFile() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("d:\\Multithreading\\user.txt"));
		Enumeration<User> users = DataProcessing.getAllUser();
		while (users.hasMoreElements()) {
			User user = users.nextElement();
			writer.write(user.getName() + "\r\n" + user.getPassword() + "\r\n" + user.getRole() + "\r\n");
		}
		writer.close();
	}

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
	public static boolean insertDoc(Doc doc) throws IOException {
		String ID = doc.getID();
		if (docs.containsKey(ID)) {
			return false;
		} else {
			docs.put(ID, doc);
			updateDocFile();
			return true;
		}
	}

	// 将档案档案文件的信息写入到文件fileList.txt中
	public static void updateDocFile() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("d:\\Multithreading\\fileList.txt"));
		Enumeration<Doc> docs = DataProcessing.getAllDocs();
		while (docs.hasMoreElements()) {
			Doc doc = docs.nextElement();
			writer.write(doc.getID() + "\r\n" + doc.getCreator() + "\r\n" + doc.getTimestamp() + "\r\n"
					+ doc.getDescription() + "\r\n" + doc.getFilename() + "\r\n");
		}
		writer.close();
	}

	// 更新cookie信息
	public static void updateCookies() throws IOException {
		File cookieFile = new File("D:\\Multithreading\\cookie.dll");
		if (cookies == null)
			cookieFile.delete();
		else {
			BufferedWriter writer = new BufferedWriter(new FileWriter(cookieFile));
			writer.write(cookies[0] + "\r\n" + cookies[1] + "\r\n" + cookies[3] + "\r\n");
			writer.close();
		}
	}

	// 返回文件数目
	public static int getUserNumber() {
		return users.size();
	}

	public static int getDocNumber() {
		return docs.size();
	}
}
