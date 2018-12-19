
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public abstract class FrameUser extends JFrame {

	private static final long serialVersionUID = 1L;

	private User user;
	@SuppressWarnings("unused")
	private FrameCheckPassword frameCheckPassword;
	private FrameDownloadFile frameDownloadFile;
	protected JButton changePasswordButton;
	protected JButton downloadFileButton;
	protected JButton logoutButton;
	protected JLabel lableName;

	public FrameUser(User user) {
		this.user = user;
		frameDownloadFile = new FrameDownloadFile(user);
		changePasswordButton = new JButton("更改密码");
		downloadFileButton = new JButton("下载文档");
		logoutButton = new JButton("注销");

		changePasswordButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent eventChangwPassward) {
				// 先验证密码方可允许更改密码
				frameCheckPassword = new FrameCheckPassword(user, new FrameChangePassword(user));
				setVisible(false);
			}
		});

		downloadFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent eventDownload) {
				frameDownloadFile.initial();
				setVisible(false);
			}
		});

		logoutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent eventLogout) {
				setVisible(false);
				FrameLogin frameLogin = new FrameLogin();
				DataProcessing.cookies[3] = "Restart";
				frameLogin.initLoginWindow();
			}
		});
	}

	public String getName() {
		return user.getName();
	}

	public String getRole() {
		return user.getRole();
	}

	public String getPassword() {
		return user.getPassword();
	}

	public abstract void init();
}
