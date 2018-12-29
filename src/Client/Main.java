package Client;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

//Fixed bug:cookie文件存储失败. 原因:有两个地方均用到了cookie的路径而没有做到修改的全覆盖.
//方法 定义一个final的静态变量,便于后期的修改
//Fixed bug:文件描述框内输入回车时,会导致文件名丢失
//原因:字符串中出现了回车,在切分字符串时发生错误,导致文件名为空
//方法 添加对回车的监视,使用户敲下回车即表示确定,并去掉所有的回车
//不足 由于分隔符的存在,用户仍然有可能在文件描述中使用到了这个分隔符
//改进 进一步检查文本,告知用户不能使用该字符
//TODO bug:服务器文件接收失败时,用户得不到任何信息(即以为上传成功)
public class Main {
	public static void main(String[] args) {

		// 本地初始化
		JFrame initialFram = new JFrame();
		try {
			DataProcessing.initLocalCookies();
		} catch (DataException dataE) {

			JOptionPane.showMessageDialog(initialFram, dataE.getMessage() + "软件初始化失败.", "提示",
					JOptionPane.INFORMATION_MESSAGE);
			DataProcessing.systemQuit();
			return;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(initialFram, e.getMessage() + "数据库文件未找到,软件初始化失败！", "提示",
					JOptionPane.INFORMATION_MESSAGE);
			System.exit(0);
			return;
		}

		// 登陆
		FrameLogin frameLogin = new FrameLogin();
		frameLogin.initLoginWindow();
	}

}