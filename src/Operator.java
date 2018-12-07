import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.Scanner;

@SuppressWarnings("resource")

public class Operator extends User {
	// 实验一：在此处编写代码
	Operator(String name, String password, String role) {
		super(name, password, role);
	}

	public void uploadFile() {
		long timestamp;
		String fileSorcTrace;
		Scanner in = new Scanner(System.in);
		String fileSever = "d:\\Multithreading\\Files\\";
		String ID, creator, description, filename;

		System.out.print("*********************");
		System.out.print("上传文件");
		System.out.println("*********************");

		// 正确得到文件路径
		System.out.println("请输入文件存储路径:");
		fileSorcTrace = in.nextLine();
		File saveFile = new File(fileSorcTrace);
		while (saveFile.exists() == false) {
			System.out.println("文件路径错误!");
			System.out.println("请重新输入文件存储路径(输入回车以退出操作):");
			fileSorcTrace = in.nextLine();
			if (fileSorcTrace.length() == 0)
				return;
			saveFile = new File(fileSorcTrace);
		}

		try {
			creator = getName();
			timestamp = new Date().getTime();
			ID = (new Integer(DataProcessing.getDocNumber())).toString();
			filename = fileSorcTrace.substring(fileSorcTrace.lastIndexOf('\\') + 1);
			System.out.print("请输入对该文件的描述(输入回车跳过):");
			if ((description = in.nextLine()).length() == 0)
				description = "(上传者未写填写描述)";
			FileInputStream fileInput = new FileInputStream(saveFile);
			FileOutputStream fileOutput = new FileOutputStream(
					fileSever + ID + filename.substring(filename.lastIndexOf('.')));
			FileChannel readFileChannel = fileInput.getChannel();
			FileChannel writeFileChannel = fileOutput.getChannel();
			writeFileChannel.transferFrom(readFileChannel, 0, readFileChannel.size());
			DataProcessing.insertDoc(ID, creator, timestamp, description, filename);
			fileInput.close();
			fileOutput.close();

		} catch (IOException e) {
			System.out.println("上传失败");
			return;
		}
		System.out.println("上传成功!");

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
				System.out.println("1.文件列表.");
				System.out.print("                     ");
				System.out.println("2.上传文件.");
				System.out.print("                     ");
				System.out.println("3.下载文件.");
				System.out.print("                     ");
				System.out.println("4.修改密码.");
				System.out.print("                     ");
				System.out.println("5.退出.");
				String answer;
				answer = in.nextLine();
				if (answer.matches("1|2|3|4|5")) {
					selection = answer.charAt(0);
					break;
				}
				System.out.print("*********************");
				System.out.print("输入错误,请输入正确的选择!");
				System.out.println("*********************");
			}
			switch (selection) {
			case '1':
				showFileList();
				break;
			case '2':
				uploadFile();
				break;
			case '3':
				downloadFile();
				break;
			case '4':
				changePassword();
				break;
			default:
				exitSystem();
				break;
			}
		}
	}
}
