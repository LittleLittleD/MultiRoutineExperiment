package Client;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.GroupLayout.Alignment;

public class FrameChangePassword extends FrameFunction {
	// 更改密码后,要求重新登陆

	private static final long serialVersionUID = 1L;

	private JLabel labelNewPassword1;
	private JLabel labelNewPassword2;

	private JButton buttonSure;

	private JPasswordField newPasswordField1;
	private JPasswordField newPasswordField2;

	public FrameChangePassword(User user) {
		super(user);
		labelNewPassword1 = new JLabel(" 新 密 码:");
		labelNewPassword2 = new JLabel("重输密码:");
		buttonSure = new JButton("确定修改");
		newPasswordField1 = new JPasswordField();
		newPasswordField2 = new JPasswordField();

		// 设置为 敲下回车即确定
		this.getRootPane().setDefaultButton(buttonSure);

		setTitle("更改密码");
		setSize(320, 160);
		Toolkit toolkit = getToolkit();
		Dimension dimension = toolkit.getScreenSize();
		int screenHeight = dimension.height;
		int screenWidth = dimension.width;
		int frm_Height = this.getHeight();
		int frm_width = this.getWidth();
		this.setLocation((screenWidth - frm_width) / 2, (screenHeight - frm_Height) / 2);

		GroupLayout layout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGap(5);// 添加间隔
		hGroup.addGroup(layout.createParallelGroup().addComponent(labelNewPassword1).addComponent(labelNewPassword2));
		hGroup.addGap(5);
		hGroup.addGroup(layout.createParallelGroup().addComponent(newPasswordField1).addComponent(newPasswordField2)
				.addComponent(buttonSure));
		hGroup.addGap(5);
		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGap(10);
		vGroup.addGroup(layout.createParallelGroup().addComponent(labelNewPassword1).addComponent(newPasswordField1));
		vGroup.addGap(5);
		vGroup.addGroup(layout.createParallelGroup().addComponent(labelNewPassword2).addComponent(newPasswordField2));
		vGroup.addGroup(layout.createParallelGroup(Alignment.TRAILING).addComponent(buttonSure));
		vGroup.addGap(10);

		layout.setVerticalGroup(vGroup);

		buttonSure.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				if (newPasswordField1.getText() == null) {
					JOptionPane.showMessageDialog(FrameChangePassword.this, "新密码不能为空!", "提示",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				try {
					if (newPasswordField1.getText().equals(newPasswordField2.getText())) {
						user.setPassword(newPasswordField1.getText());
						DataProcessing.updateUser(user);
						JOptionPane.showMessageDialog(FrameChangePassword.this, "修改成功", "提示",
								JOptionPane.INFORMATION_MESSAGE);
						JOptionPane.showMessageDialog(FrameChangePassword.this, "登陆超时,请重新启动", "提示",
								JOptionPane.INFORMATION_MESSAGE);
						DataProcessing.systemQuit();
					} else {
						JOptionPane.showMessageDialog(FrameChangePassword.this, "两次密码不一致!", "提示",
								JOptionPane.INFORMATION_MESSAGE);
						setVisible(false);
						FrameChangePassword frameChangePassword = new FrameChangePassword(user);
						frameChangePassword.initial();
					}
				} catch (IOException ioException) {
					JOptionPane.showMessageDialog(FrameChangePassword.this, "修改失败,请稍后重试.", "提示",
							JOptionPane.INFORMATION_MESSAGE);
					setVisible(false);
				}

			}
		});
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				FrameUser frameUser;
				if (user.getRole().equalsIgnoreCase("Administrator")) {
					frameUser = new FrameAdministrator(user);
				} else if (user.getRole().equalsIgnoreCase("Operator")) {
					frameUser = new FrameOperator(user);
				} else {
					frameUser = new FrameBrowser(user);
				}
				frameUser.init();
			};
		});
	}

	@Override
	public void initial() {

		setVisible(true);
	}
}
