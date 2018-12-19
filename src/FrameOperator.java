import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class FrameOperator extends FrameUser {

	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private FrameUploadFile frameUploadFile;
	private JButton uploadFileButton;

	public FrameOperator(User user) {
		super(user);
		frameUploadFile = new FrameUploadFile(user);
		uploadFileButton = new JButton("上传文档");

		uploadFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frameUploadFile.initial();
				setVisible(false);
			}
		});
	}

	@Override
	public void init() {
		setFont(new Font("Helvetica", Font.PLAIN, 14));

		JPanel panel = new JPanel();
		GridLayout verticalLayout = new GridLayout(4, 1);
		panel.setLayout(verticalLayout);
		panel.add(uploadFileButton);
		panel.add(downloadFileButton);
		panel.add(changePasswordButton);
		panel.add(logoutButton);
		getContentPane().add(panel);

		setTitle("操作员" + "---" + getName());
		setSize(360, 320);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setLocationRelativeTo(null);
	}

}
