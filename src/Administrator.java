import java.io.IOException;
import java.util.*;

@SuppressWarnings("resource")

public class Administrator extends User {
	Administrator(String name, String password, String role) {
		super(name, password, role);
	}

	public void changeUserInfo() {
		System.out.print("*********************");
		System.out.print("更改用户信息");
		System.out.println("*********************");
		Scanner in = new Scanner(System.in);
		System.out.print("请输入待更改的用户的用户名:");
		String userName = in.nextLine();
		if (DataProcessing.searchByName(userName)) {
			System.out.print("请输入新密码:");
			String newPassword = in.nextLine();
			if (super.changeUserInfo(newPassword) == false)
				System.out.println("更改失败");
		} else {
			System.out.println("不存在该用户");
		}
	}

	public void delUser() {
		System.out.print("*********************");
		System.out.print("刪除用户");
		System.out.println("*********************");
		Scanner in = new Scanner(System.in);
		System.out.print("请输入待刪除的用户的用户名:");
		String userName = in.nextLine();
		if (userName.equals(getName())) {
			System.out.println("管理员不能刪除自己");
		} else if (userName.equals("AUser")) {
			System.out.println("系统管理员无法刪除");
		} else if (DataProcessing.searchByName(userName)) {
			try {
				if (DataProcessing.deleteUser(userName))
					System.out.println("删除成功");
				else
					System.out.println("删除失败");
			} catch (IOException e) {
				System.out.println("打开系统文件失败,无法删除");
			}
		} else {
			System.out.println("不存在该用户");
		}
	}

	public void addUser() {
		Scanner in = new Scanner(System.in);

		System.out.print("*********************");
		System.out.print("添加用户");
		System.out.println("*********************");
		System.out.print("请输入新增用户的用户名:");

		String userName = in.nextLine();
		if (DataProcessing.searchByName(userName)) {
			System.out.println("该用户名已存在");
		} else {
			System.out.print("请输入新增用户的密码:");
			String newPassword = in.nextLine();
			System.out.print("请输入新增用户的身份:");
			String newRole = in.nextLine();
			if (newRole.equalsIgnoreCase("administrator") == false)
				if (newRole.equalsIgnoreCase("operator") == false)
					if (newRole.equalsIgnoreCase("browser") == false) {
						System.out.println("身份关键词输入错误");
						System.out.println("添加失败");
						return;
					}
			try {
				if (DataProcessing.insertUser(userName, newPassword, newRole))
					System.out.println("添加成功");
				else
					System.out.println("添加失败");
			} catch (IOException e) {
				System.out.println("打开系统文件失败,无法添加");
			}
		}
	}

	public void listUser() {
		int count = 0;
		Enumeration<User> users = DataProcessing.getAllUser();

		System.out.print("*********************");
		System.out.print("显示所有用户信息");
		System.out.println("*********************");

		while (users.hasMoreElements()) {
			count++;
			System.out.println("Number " + count);
			System.out.println(users.nextElement().toString());
		}
	}

	public void showMenu() {
		Scanner in = new Scanner(System.in);
		char selection = 0;
		while (true) {
			System.out.print("*********************");
			System.out.print("请选择菜单");
			System.out.println("*********************");
			while (true) {
				// 该层循环检查用户输入的选择
				System.out.print("                     ");
				System.out.println("1.更改用户信息.");
				System.out.print("                     ");
				System.out.println("2.删除用户.");
				System.out.print("                     ");
				System.out.println("3.添加用户.");
				System.out.print("                     ");
				System.out.println("4.显示所有用户信息.");
				System.out.print("                     ");
				System.out.println("5.下载文件.");
				System.out.print("                     ");
				System.out.println("6.文件列表.");
				System.out.print("                     ");
				System.out.println("7.修改密码.");
				System.out.print("                     ");
				System.out.println("8.退出.");
				String answer;
				answer = in.nextLine();
				if (answer.matches("1|2|3|4|5|6|7|8")) {
					selection = answer.charAt(0);
					break;
				}
				System.out.print("*********************");
				System.out.print("输入错误,请输入正确的选择!");
				System.out.println("*********************");
			}
			switch (selection) {
			case '1':
				changeUserInfo();
				break;
			case '2':
				delUser();
				break;
			case '3':
				addUser();
				break;
			case '4':
				listUser();
				break;
			case '5':
				downloadFile();
				break;
			case '6':
				showFileList();
				break;
			case '7':
				changePassword();
				break;
			default:
				exitSystem();
				break;
			}
		}
	}
}
