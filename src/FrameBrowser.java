import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class FrameBrowser extends FrameUser {

	private static final long serialVersionUID = 1L;

	public FrameBrowser(User user) {
		super(user);
	}

	@Override
	public void init() {
		setFont(new Font("Helvetica", Font.PLAIN, 14));

		JPanel panel = new JPanel();
		GridLayout verticalLayout = new GridLayout(3, 1);
		panel.setLayout(verticalLayout);
		panel.add(downloadFileButton);
		panel.add(changePasswordButton);
		panel.add(logoutButton);
		getContentPane().add(panel);

		setTitle("‘ƒ¿¿‘±" + "---" + getName());
		setSize(360, 240);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setLocationRelativeTo(null);
	}

}
