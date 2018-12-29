package Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.TimeoutException;

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

		// 设置为 敲下回车即登陆
		this.getRootPane().setDefaultButton(loginButton);

		passwordField.requestFocus();

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
				String role = null;
				if (name.length() == 0 || password.length() == 0) {
					JOptionPane.showMessageDialog(FrameLogin.this, "请输入账号或密码!", "提示", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				// 联机版
				try {
					// 此显示登陆中
					loginButton.setText("登陆中...");
					loginButton.setEnabled(false);
					// 向服务器发送登陆请求
					DataRequestion dataRequestion = new DataRequestion();
					role = dataRequestion.loginRequestion(name, password);
					dataRequestion.close();
					if (role.matches("Administrator|Operator|Browser") == true) {
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
							DataProcessing.systemQuit();
						}
						login(new User(name, password, role));
						setVisible(false);
					} else {
						JOptionPane.showMessageDialog(FrameLogin.this, "账号异常!", "提示", JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (SocketException connectionFail) {
					JOptionPane.showMessageDialog(FrameLogin.this, "连接失败!", "提示", JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException connectionError) {
					JOptionPane.showMessageDialog(FrameLogin.this, "连接异常!", "提示", JOptionPane.INFORMATION_MESSAGE);
				} catch (TimeoutException timeoutException) {
					JOptionPane.showMessageDialog(FrameLogin.this, "登陆超时!", "提示", JOptionPane.INFORMATION_MESSAGE);
				} catch (DataException dataException) {
					JOptionPane.showMessageDialog(FrameLogin.this, "数据库信息有误", "提示", JOptionPane.INFORMATION_MESSAGE);
				} finally {
					loginButton.setText("登陆");
					loginButton.setEnabled(true);
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
		setResizable(false);
		setLocationRelativeTo(null);
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
			// 联机版
			if ("Restart".matches(DataProcessing.cookies[3])) {
				// 刚刚注销
				accountField.setText(DataProcessing.cookies[0]);
				passwordField.setText(DataProcessing.cookies[1]);
				rememberButton.setSelected(true);
				if ("Auto".matches(DataProcessing.cookies[3])) {
					autoLogButton.setSelected(true);
					DataProcessing.cookies[3] = new String("Auto");
				} else {
					DataProcessing.cookies[3] = new String("Remember");
				}
				setVisible(true);
			} else {
				// 自动登陆
				if (DataProcessing.cookies[3].matches("Auto|Remember")) {
					accountField.setText(DataProcessing.cookies[0]);
					passwordField.setText(DataProcessing.cookies[1]);
					rememberButton.setSelected(true);
					setVisible(true);
					if ("Auto".matches(DataProcessing.cookies[3])) {
						autoLogButton.setSelected(true);
						loginButton.setText("登陆中...");
						loginButton.setEnabled(false);
						String role = null;
						try {
							// 发送登陆申请
							DataRequestion dataRequestion = new DataRequestion();
							role = dataRequestion.loginRequestion(DataProcessing.cookies[0], DataProcessing.cookies[1]);
							dataRequestion.close();
							if (!role.equals("Wrong")) {
								login(new User(DataProcessing.cookies[0], DataProcessing.cookies[1], role));
								setVisible(false);
							} else {
								JOptionPane.showMessageDialog(FrameLogin.this, "密码已发生改动,请重新填写!", "提示",
										JOptionPane.INFORMATION_MESSAGE);
							}
						} catch (SocketException connectionFail) {
							JOptionPane.showMessageDialog(FrameLogin.this, "连接失败!", "提示",
									JOptionPane.INFORMATION_MESSAGE);
						} catch (IOException connectionError) {
							JOptionPane.showMessageDialog(FrameLogin.this, "连接异常!", "提示",
									JOptionPane.INFORMATION_MESSAGE);
						} catch (TimeoutException timeoutException) {
							JOptionPane.showMessageDialog(FrameLogin.this, "登陆超时!", "提示",
									JOptionPane.INFORMATION_MESSAGE);
						} catch (DataException dataException) {
							JOptionPane.showMessageDialog(FrameLogin.this, "数据库信息有误", "提示",
									JOptionPane.INFORMATION_MESSAGE);
						} finally {
							loginButton.setText("登陆");
							loginButton.setEnabled(true);
						}
					}
				}
			}

//			// 本地版
//			if (DataProcessing.searchByName(DataProcessing.cookies[0]).getPassword()
//					.equals(DataProcessing.cookies[1])) {
//				if (DataProcessing.cookies[3].equals("Auto")) {
//					login(new User(DataProcessing.cookies[0], DataProcessing.cookies[1], DataProcessing.cookies[2]));
//					return;
//				} else {
//					// 此时cookies[3]只可能是"Remember|Restart", "Auto|Restart", "Remember"
//					accountField.setText(DataProcessing.cookies[0]);
//					passwordField.setText(DataProcessing.cookies[1]);
//					rememberButton.setSelected(true);
//					if ("Restart".matches(DataProcessing.cookies[3])) {
//						if ("Auto".matches(DataProcessing.cookies[3])) {
//							autoLogButton.setSelected(true);
//							DataProcessing.cookies[3] = new String("Auto");
//						} else {
//							DataProcessing.cookies[3] = new String("Remember");
//						}
//					}
//				}
//			} else {
//				JOptionPane.showMessageDialog(FrameLogin.this, "密码已发生改动,请重新填写!", "提示", JOptionPane.INFORMATION_MESSAGE);
//				accountField.setText(DataProcessing.cookies[0]);
//			}
		} else {
			setVisible(true);
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public String getName() {
		return accountField.getText();
	}

	@SuppressWarnings("deprecation")
	public String getPassword() {
		return passwordField.getText();
	}

}
