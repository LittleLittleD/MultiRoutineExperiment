import java.util.Scanner;
@SuppressWarnings("resource")

public class Browser extends User {
	// 实验一：在此处编写代码
	Browser(String name, String password, String role) {
		super(name, password, role);
	}

	public void showMenu() {
		Scanner in = new Scanner(System.in);
		char selection;
		while (true) {
			System.out.print("*********************");
			System.out.print("请选择菜单");
			System.out.println("*********************");
			while (true) {
				// 该层循环检查用户输入的选择
				System.out.print("                     ");
				System.out.println("1.下载文件.");
				System.out.print("                     ");
				System.out.println("2.文件列表.");
				System.out.print("                     ");
				System.out.println("3.更改密码.");
				System.out.print("                     ");
				System.out.println("4.退出.");
				String answer;
				answer = in.nextLine();
				if (answer.matches("1|2|3|4")) {
					selection = answer.charAt(0);
					break;
				}
				System.out.print("*********************");
				System.out.print("输入错误,请输入正确的选择!");
				System.out.println("*********************");
			}
			switch (selection) {
			case '1':
				downloadFile();
				break;
			case '2':
				System.out.print("*********************");
				System.out.print("文件列表");
				System.out.println("*********************");
				showFileList();
				break;
			case '3':
				changePassword();
				break;
			default:
				exitSystem();
				break;
			}
		}
	}
}
