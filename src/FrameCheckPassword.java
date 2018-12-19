import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.GroupLayout.Alignment;

public class FrameCheckPassword extends JFrame {

	private static final long serialVersionUID = 1L;

	private JLabel labelOldPassword;
	private JButton buttonSure;
	private JPasswordField oldPasswordField;

	FrameCheckPassword(User user, FrameFunction framefunction) {

		labelOldPassword = new JLabel("当前密码:");
		buttonSure = new JButton("确定");
		oldPasswordField = new JPasswordField();

		setTitle("密码验证");

		GroupLayout layout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGap(5);
		hGroup.addGroup(layout.createParallelGroup().addComponent(labelOldPassword));
		hGroup.addGap(5);
		hGroup.addGroup(layout.createParallelGroup().addComponent(oldPasswordField).addComponent(buttonSure));
		hGroup.addGap(5);
		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGap(10);
		vGroup.addGroup(layout.createParallelGroup().addComponent(labelOldPassword).addComponent(oldPasswordField));

		vGroup.addGroup(layout.createParallelGroup(Alignment.TRAILING).addComponent(buttonSure));
		vGroup.addGap(10);

		buttonSure.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent eventCheck) {
				if (oldPasswordField.getText().equals(user.getPassword())) {
					framefunction.initial();
					setVisible(false);
				} else {
					JOptionPane.showMessageDialog(FrameCheckPassword.this, "密码错误!", "提示",
							JOptionPane.INFORMATION_MESSAGE);
					setVisible(false);
					@SuppressWarnings("unused")
					FrameCheckPassword frameCheckPassword = new FrameCheckPassword(user, framefunction);
				}

			}
		});
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(FrameCheckPassword.this,
						"此时退出,将退出系统,请问是否继续退出？", "询问", JOptionPane.YES_NO_OPTION)) {
					FrameCheckPassword.this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				} else {
					FrameCheckPassword.this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				}
			}
		});

		layout.setVerticalGroup(vGroup);
		setSize(300, 120);
		Toolkit toolkit = getToolkit();
		Dimension dimension = toolkit.getScreenSize();
		int screenHeight = dimension.height;
		int screenWidth = dimension.width;
		int frm_Height = this.getHeight();
		int frm_width = this.getWidth();
		this.setLocation((screenWidth - frm_width) / 2, (screenHeight - frm_Height) / 2);

		setVisible(true);

	}
}
