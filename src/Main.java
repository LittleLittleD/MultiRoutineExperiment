import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Main {
	public static void main(String[] args) {
		JFrame initialFram = new JFrame();
		try {
			DataProcessing.Init();
		} catch (DataException dataE) {

			JOptionPane.showMessageDialog(initialFram, dataE.getMessage() + "软件初始化失败.", "提示",
					JOptionPane.INFORMATION_MESSAGE);
			System.exit(0);
			return;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(initialFram, e.getMessage() + "数据库文件未找到,软件初始化失败！", "提示",
					JOptionPane.INFORMATION_MESSAGE);
			System.exit(0);
			return;
		}

		FrameLogin frameLogin = new FrameLogin();
		// 登陆
		frameLogin.initLoginWindow();

	}

}