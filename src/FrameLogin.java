import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class FrameLogin extends JFrame {
	private static final long serialVersionUID = 1L;

	private JLabel labelHead;
	private JLabel labelAccount;
	private JLabel labelPassword;
	private JTextField accountField;
	private JPasswordField passwordField;
	private JRadioButton rememberButton;
	private JRadioButton autoLogButton;
	private JButton loginButton;

	public FrameLogin() {

		labelHead = new JLabel("文档管理系统登陆窗口");
		labelAccount = new JLabel("账号：");
		labelPassword = new JLabel("密码：");

		accountField = new JTextField();
		passwordField = new JPasswordField();

		rememberButton = new JRadioButton("记住密码");
		autoLogButton = new JRadioButton("自动登陆");

		loginButton = new JButton("登陆");

		// 为指定的 Container 创建 GroupLayout
		GroupLayout layout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);

		// 创建GroupLayout的水平连续组，，越先加入的ParallelGroup，优先级级别越高。
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGap(5);// 添加间隔
		hGroup.addGroup(layout.createParallelGroup().addComponent(labelAccount).addComponent(labelPassword));
		hGroup.addGap(5);
		hGroup.addGroup(layout.createParallelGroup().addComponent(labelHead).addComponent(passwordField)
				.addComponent(rememberButton).addComponent(autoLogButton).addComponent(accountField)
				.addComponent(loginButton));
		hGroup.addGap(5);
		layout.setHorizontalGroup(hGroup);

		// 创建GroupLayout的垂直连续组，，越先加入的ParallelGroup，优先级级别越高。
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGap(10);
		vGroup.addGroup(layout.createParallelGroup().addComponent(labelHead));
		vGroup.addGap(10);
		vGroup.addGroup(layout.createParallelGroup().addComponent(labelAccount).addComponent(accountField));
		vGroup.addGap(5);
		vGroup.addGroup(layout.createParallelGroup().addComponent(labelPassword).addComponent(passwordField));
		vGroup.addGroup(layout.createParallelGroup().addComponent(rememberButton));

		vGroup.addGroup(layout.createParallelGroup().addComponent(autoLogButton));
		vGroup.addGroup(layout.createParallelGroup(Alignment.TRAILING).addComponent(loginButton));
		vGroup.addGap(10);
		// 设置垂直组
		layout.setVerticalGroup(vGroup);

		// 添加监听者
		loginButton.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = getName();
				String password = getPassword();
				User user = DataProcessing.searchByName(name);
				if (DataProcessing.searchByName(name) == null) {
					JOptionPane.showMessageDialog(FrameLogin.this, "用户名错误!", "提示", JOptionPane.INFORMATION_MESSAGE);
				} else if (user.getPassword().equals(password)) {
					setVisible(false);
					// 登陆成功
					if (autoLogButton.isSelected() || rememberButton.isSelected()) {
						DataProcessing.cookies = new String[4];
						DataProcessing.cookies[0] = accountField.getText();
						DataProcessing.cookies[1] = passwordField.getText();
						if (autoLogButton.isSelected()) {
							DataProcessing.cookies[3] = "Auto";
						} else {
							DataProcessing.cookies[3] = "Remember";
						}
					} else {
						DataProcessing.cookies = null;
					}
					try {
						DataProcessing.updateCookies();
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(FrameLogin.this, "文件写入失败!", "提示",
								JOptionPane.INFORMATION_MESSAGE);
						System.exit(0);
					}
					login(user);
				} else {
					JOptionPane.showMessageDialog(FrameLogin.this, "密码错误!", "提示", JOptionPane.INFORMATION_MESSAGE);
				}

			}
		});

		rememberButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!rememberButton.isSelected()) {
					autoLogButton.setSelected(false);
				}
			}
		});

		autoLogButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (autoLogButton.isSelected()) {
					rememberButton.setSelected(true);
				}
			}
		});

		setSize(250, 220);
		Toolkit toolkit = getToolkit();
		Dimension dimension = toolkit.getScreenSize();
		int screenHeight = dimension.height;
		int screenWidth = dimension.width;
		int frm_Height = this.getHeight();
		int frm_width = this.getWidth();
		this.setLocation((screenWidth - frm_width) / 2, (screenHeight - frm_Height) / 2);

	}

	private void login(User user) {
		FrameUser frameUser;
		if (user.getRole().equalsIgnoreCase("Administrator")) {
			frameUser = new FrameAdministrator(user);
		} else if (user.getRole().equalsIgnoreCase("Operator")) {
			frameUser = new FrameOperator(user);
		} else {
			frameUser = new FrameBrowser(user);
		}
		frameUser.init();
	}

	// cookie处理初始化窗口
	public void initLoginWindow() {
		if (DataProcessing.cookies != null) {
			if (DataProcessing.searchByName(DataProcessing.cookies[0]).getPassword()
					.equals(DataProcessing.cookies[1])) {
				if (DataProcessing.cookies[3].equals("Auto")) {
					login(new User(DataProcessing.cookies[0], DataProcessing.cookies[1], DataProcessing.cookies[2]));
					return;
				} else {
					// 此时cookies[3]只可能是"Remember|Restart", "Auto|Restart", "Remember"
					accountField.setText(DataProcessing.cookies[0]);
					passwordField.setText(DataProcessing.cookies[1]);
					rememberButton.setSelected(true);
					if ("Restart".matches(DataProcessing.cookies[3])) {
						if ("Auto".matches(DataProcessing.cookies[3])) {
							autoLogButton.setSelected(true);
							DataProcessing.cookies[3] = new String("Auto");
						} else {
							DataProcessing.cookies[3] = new String("Remember");
						}
					}
				}
			} else {
				JOptionPane.showMessageDialog(FrameLogin.this, "密码已发生改动,请重新填写!", "提示", JOptionPane.INFORMATION_MESSAGE);
				accountField.setText(DataProcessing.cookies[0]);
			}
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public String getName() {
		return accountField.getText();
	}

	@SuppressWarnings("deprecation")
	public String getPassword() {
		return passwordField.getText();
	}

}
