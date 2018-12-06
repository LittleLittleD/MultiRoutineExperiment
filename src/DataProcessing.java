import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

//说明:实验二使用的 DataProcessing类
public class DataProcessing {
	static Hashtable<String, User> users;
	static Hashtable<String, Doc> docs;

	// 说明: 实验二、三、四 使用此处Init()函数
	public static void Init() throws IOException, DataException {
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

			if (role.equals("Operator"))
				users.put(name, new Operator(name, password, role));
			else if (role.equals("Browser"))
				users.put(name, new Browser(name, password, role));
			else if (role.equals("Administrator"))
				users.put(name, new Administrator(name, password, role));
			else {
				br.close();
				throw new DataException("数据错误！");
			}
		}
		br.close();
		// 实验三：此处编写代码，从文件file.txt中提取已经上传的文档
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
	public static boolean updateUser(String name, String password, String role) throws IOException {
		User user;

		if (users.containsKey(name)) {
			// 类Administrator、Operator和Browser编制完成后，此处代码不再报错
			if (role.equalsIgnoreCase("administrator"))
				user = new Administrator(name, password, "Administrator");
			else if (role.equalsIgnoreCase("operator"))
				user = new Operator(name, password, "Operator");
			else
				user = new Browser(name, password, "Browser");
			users.put(name, user);
			updateUserFile();
			return true;
		} else
			return false;
	}

	// 增加新用户
	public static boolean insertUser(String name, String password, String role) throws IOException {
		User user;

		if (users.containsKey(name))
			return false;
		else {
			// 类Administrator、Operator和Browser编制完成后，此处代码不再报错
			if (role.equalsIgnoreCase("administrator"))
				user = new Administrator(name, password, "administrator");
			else if (role.equalsIgnoreCase("operator"))
				user = new Operator(name, password, "operator");
			else
				user = new Browser(name, password, "Browser");
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
	public static boolean searchByName(String name) {
		if (users.containsKey(name))
			return true;
		return false;
	}

	// 实验三：实现此功能
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

	// 实验三：参照用户管理功能中的函数实现方法，完成这些函数的功能
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
	public static boolean insertDoc(String ID, String creator, long timestamp, String description, String filename)
			throws IOException {
		if (docs.containsKey(ID)) {
			return false;
		} else {
			docs.put(ID, new Doc(ID, creator, timestamp, description, filename));
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

	// 返回文件数目
	public static int getDocNumber() {
		return docs.size();
	}
}
