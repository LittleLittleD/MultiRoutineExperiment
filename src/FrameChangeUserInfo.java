import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;

public class FrameChangeUserInfo extends FrameFunction {

	private static final long serialVersionUID = 1L;

	private boolean isSelf;
	private JButton buttonSure;
	private JLabel labelAccount;
	private JLabel labelPassword;
	private JLabel labelRole;
	private JTextField accountField;
	private JTextField passwordField;
	private JTextField roleFieldOnlyRead;
	private JComboBox<String> comboBoxRole;

	FrameChangeUserInfo(User user, User administrator) {
		super(user);
		setTitle("修改用戶信息");
		setSize(320, 200);
		Toolkit toolkit = getToolkit();
		Dimension dimension = toolkit.getScreenSize();
		int screenHeight = dimension.height;
		int screenWidth = dimension.width;
		int frm_Height = this.getHeight();
		int frm_width = this.getWidth();
		this.setLocation((screenWidth - frm_width) / 2, (screenHeight - frm_Height) / 2);

		isSelf = false;
		labelAccount = new JLabel("用户名:");
		labelPassword = new JLabel("密   码:");
		labelRole = new JLabel("角   色:");
		buttonSure = new JButton("确定");
		accountField = new JTextField();
		passwordField = new JTextField();
		String[] strRoles = { "Administrator", "Operator", "Browser" };
		comboBoxRole = new JComboBox<String>(strRoles);

		int roleJudge = 2;
		accountField.setText(user.getName());
		accountField.setEditable(false);
		if (user.getRole().equalsIgnoreCase("Administrator"))
			roleJudge = 0;
		else if (user.getRole().equalsIgnoreCase("Operator"))
			roleJudge = 1;
		comboBoxRole.setSelectedIndex(roleJudge);
		passwordField.setText(user.getPassword());
		if (user.getName().equals(administrator.getName())) {
			isSelf = true;
			roleFieldOnlyRead = new JTextField("Administrator");
			roleFieldOnlyRead.setEnabled(false);
		}

		// 为panelAddUser 创建 GroupLayout
		GroupLayout layout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);

		// 创建GroupLayout的水平连续组，，越先加入的ParallelGroup，优先级级别越高。
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGap(5);// 添加间隔
		hGroup.addGroup(layout.createParallelGroup().addComponent(labelAccount).addComponent(labelPassword)
				.addComponent(labelRole));
		hGroup.addGap(5);
		if (isSelf) {
			hGroup.addGroup(layout.createParallelGroup().addComponent(accountField).addComponent(passwordField)
					.addComponent(roleFieldOnlyRead));
		} else {
			hGroup.addGroup(layout.createParallelGroup().addComponent(accountField).addComponent(passwordField)
					.addComponent(comboBoxRole));
		}
		hGroup.addGroup(layout.createParallelGroup().addComponent(buttonSure));
		hGroup.addGap(5);
		layout.setHorizontalGroup(hGroup);

		// 创建GroupLayout的垂直连续组，，越先加入的ParallelGroup，优先级级别越高。
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGap(10);
		vGroup.addGroup(layout.createParallelGroup().addComponent(labelAccount).addComponent(accountField));
		vGroup.addGap(10);
		vGroup.addGroup(layout.createParallelGroup().addComponent(labelPassword).addComponent(passwordField));
		vGroup.addGap(10);
		if (isSelf) {
			vGroup.addGroup(layout.createParallelGroup().addComponent(labelRole).addComponent(roleFieldOnlyRead));
		} else {
			vGroup.addGroup(layout.createParallelGroup().addComponent(labelRole).addComponent(comboBoxRole));
		}
		vGroup.addGap(5);
		vGroup.addGroup(layout.createParallelGroup(Alignment.TRAILING).addComponent(buttonSure));
		vGroup.addGap(10);
		// 设置垂直组
		layout.setVerticalGroup(vGroup);

		// 添加监听者
		buttonSure.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (passwordField.getText() == null) {
					JOptionPane.showMessageDialog(FrameChangeUserInfo.this, "密码不能为空!", "提示",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				User user = new User(accountField.getText(), passwordField.getText(),
						(String) comboBoxRole.getSelectedItem());
				if (isSelf)
					administrator.setPassword(passwordField.getText());
				try {
					DataProcessing.updateUser(user);
					JOptionPane.showMessageDialog(FrameChangeUserInfo.this, "修改成功!", "提示",
							JOptionPane.INFORMATION_MESSAGE);
					setVisible(false);
					FrameUserManage frameUserManage = new FrameUserManage(administrator);
					frameUserManage.initial();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(FrameChangeUserInfo.this, "修改失败,请稍后重试!", "提示",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}

			}
		});

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				FrameUserManage frameUserManage = new FrameUserManage(administrator);
				frameUserManage.initial();
			};
		});
	}

	@Override
	public void initial() {
		setVisible(true);
	}

}
