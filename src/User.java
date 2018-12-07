import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.Scanner;

@SuppressWarnings("resource")

public abstract class User {
	private String name;
	private String password;
	private String role;

	User() {
	}

	User(String name, String password, String role) {
		this.name = name;
		this.password = password;
		this.role = role;
	}

	public boolean changeUserInfo(String password) {
		// 写用户信息到存储
		try {
			if (DataProcessing.updateUser(name, password, role)) {
				this.password = password;
				System.out.println("修改成功");
				return true;
			} else {
				System.out.println("修改失败");
				return false;
			}
		} catch (IOException e) {
			System.out.println("打开系统文件失败,无法修改");
			return false;
		}
	}

	public boolean downloadFile() {
		Doc fileInfo;
		String fileID;
		String fileSever = "d:\\Multithreading\\Files\\";
		String fileSaveTrace;
		Scanner in = new Scanner(System.in);

		System.out.print("*********************");
		System.out.print("下载文件");
		System.out.println("*********************");

		// 正确得到文档ID
		System.out.print("请输入文件ID:");
		fileID = in.nextLine();
		while (fileID.length() == 0)
			fileID = in.nextLine();
		
		while ((fileInfo = DataProcessing.searchDoc(fileID)) == null) {
			System.out.println("ID错误,找不到该文件!");
			System.out.print("请重新输入文件ID(输入回车以退出):");
			fileID = in.nextLine();
			if (fileID.length() == 0)
				return false;
		}

		System.out.print("请输入文件存储路径:" + "(输入回车则选择默认路径D:\\Files)");
		if ((fileSaveTrace = in.nextLine()).length() == 0)
			fileSaveTrace = "d:\\Files\\";

		try {
			File file = new File(fileSaveTrace + fileInfo.getFilename());
			file.createNewFile();
			FileInputStream fileInput = new FileInputStream(fileSever + fileID + "." + fileInfo.getFileType());
			FileOutputStream fileOutput = new FileOutputStream(file);
			FileChannel readFileChannel = fileInput.getChannel();
			FileChannel writeFileChannel = fileOutput.getChannel();
			writeFileChannel.transferFrom(readFileChannel, 0, readFileChannel.size());
			fileInput.close();
			fileOutput.close();
			System.out.println("下载成功");
		} catch (FileNotFoundException e) {
			System.out.println("文件打开失败");
		} catch (IOException e) {
			System.out.println("下载失败");
		}
		return true;
	}

	public void showFileList() {
		Enumeration<Doc> docs = DataProcessing.getAllDocs();

		System.out.print("*********************");
		System.out.print("文件列表");
		System.out.println("*********************");

		while (docs.hasMoreElements()) {
			System.out.println(docs.nextElement().toString() + "\n");
		}
	}

	public void changePassword() {
		Scanner in = new Scanner(System.in);
		System.out.print("*********************");
		System.out.print("修改密码");
		System.out.println("*********************");

		// 正确得到密码
		System.out.print("请输入新的密码:");
		String newPassword = in.nextLine();
		while (newPassword.length() == 0)
			newPassword = in.nextLine();

		System.out.print("请再输入一次新的密码以确认:");
		String newPasswordCopy = in.nextLine();
		while (newPasswordCopy.length() == 0)
			newPasswordCopy = in.nextLine();

		while (newPassword.equals(newPasswordCopy) == false) {
			System.out.print("请重新输入新的密码(输入回车以退出):");
			newPassword = in.nextLine();
			if (newPassword.length() == 0)
				return;
			System.out.print("请再输入一次新的密码以确认:");
			newPasswordCopy = in.nextLine();
			while (newPasswordCopy.length() == 0)
				newPasswordCopy = in.nextLine();
		}

		setPassword(newPassword);
		System.out.println("成功修改密码");
	}

	public abstract void showMenu();

	public void exitSystem() {
		System.out.println("系统退出, 谢谢使用 ! ");
		System.exit(0);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String toString() {
		return "Name: " + name + "\tPasward: " + password + "\tRole: " + role;
	}
}