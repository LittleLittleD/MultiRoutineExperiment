import java.io.IOException;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		try {
			DataProcessing.Init();
		} catch (DataException dataE) {
			System.out.println(dataE.getMessage() + "软件初始化失败.");
			return;
		} catch (IOException e) {
			System.out.println("数据库文件未找到,软件初始化失败！");
			return;
		}
		Scanner in = new Scanner(System.in);
		while (true) {
			System.out.print("*********************");
			System.out.print("欢迎使用文件管理系统V1.0");
			System.out.println("*********************");
			String selection;
			while (true) {
				System.out.print("                     ");
				System.out.println("1.登陆.");
				System.out.print("                     ");
				System.out.println("2.退出.");
				selection = in.nextLine();
				if (selection.matches("1|2"))
					break;
				System.out.print("*********************");
				System.out.print("输入错误,请输入正确的选择!");
				System.out.println("*********************");
			}
			if (selection.equals("1")) {
				System.out.print("用户名:");
				String name = in.nextLine();
				System.out.print("密码:");
				String password = in.nextLine();
				User user = DataProcessing.searchUser(name, password);
				if (user != null) {
					System.out.println("登陆成功!");
					user.showMenu();
					break;
				}
				System.out.println("用户名或密码错误!");
			} else {
				break;
			}
		}
		in.close();
	}
}