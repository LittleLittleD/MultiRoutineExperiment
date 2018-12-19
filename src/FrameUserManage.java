import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.table.DefaultTableModel;

public class FrameUserManage extends FrameFunction {

	private static final long serialVersionUID = 1L;

	private FrameChangeUserInfo frameChangeUserInfo;
	private JTabbedPane tabbedPane;
	private JPanel panelUserList;
	private JPanel panelAddUser;
	private JPanel panelButtons;
	private JComboBox<String> comboBoxRole;
	private DefaultTableModel tableModel;
	private JTable table;
	private JButton buttonModify;
	private JButton buttonDelete;
	private JButton buttonAdd;
	private JLabel labelAccount;
	private JLabel labelPassword;
	private JLabel labelRole;
	private JTextField accountField;
	private JTextField passwordField;

//	private FramChangeUserInfo framChangeUserInfo;
	public FrameUserManage(User user) {
		super(user);
		setTitle("用户管理界面");
		setSize(400, 240);
		Toolkit toolkit = getToolkit();
		Dimension dimension = toolkit.getScreenSize();
		int screenHeight = dimension.height;
		int screenWidth = dimension.width;
		int frm_Height = this.getHeight();
		int frm_width = this.getWidth();
		this.setLocation((screenWidth - frm_width) / 2, (screenHeight - frm_Height) / 2);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		// 用户列表卡片
		buttonModify = new JButton("修改");
		buttonDelete = new JButton("删除");
		panelUserList = new JPanel();
		panelButtons = new JPanel();
		tabbedPane.addTab("用户列表", null, panelUserList, null);
		panelUserList.setLayout(new BorderLayout(5, 5));

		GridLayout verticalLayout = new GridLayout(1, 2);
		panelButtons.setLayout(verticalLayout);
		panelButtons.add(buttonModify);
		panelButtons.add(buttonDelete);
		panelUserList.add(BorderLayout.SOUTH, panelButtons);

		Enumeration<User> users = DataProcessing.getAllUser();
		Object[][] cellData = new Object[DataProcessing.getUserNumber()][3];
		{
			int i = 0;
			while (users.hasMoreElements()) {
				User tempUser = users.nextElement();
				cellData[i][0] = tempUser.getName();
				cellData[i][1] = tempUser.getPassword();
				cellData[i][2] = tempUser.getRole();
				i++;
			}
		}
		String[] columnNames = { "用户名", "密码", "角色" };

		tableModel = new DefaultTableModel(cellData, columnNames) {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table = new JTable();
		table.setModel(tableModel);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(table);
		panelUserList.add(BorderLayout.CENTER, scrollPane);

		buttonModify.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int currentRow = table.getSelectedRow();
				if (currentRow == -1) {
					JOptionPane.showMessageDialog(FrameUserManage.this, "请先选择一行", "提示",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				Object objectName = tableModel.getValueAt(currentRow, 0);
				Object objectPassword = tableModel.getValueAt(currentRow, 1);
				Object objectRole = tableModel.getValueAt(currentRow, 2);
				String name = (String) objectName;
				String password = (String) objectPassword;
				String role = (String) objectRole;
				User userToChange = new User(name, password, role);
				setVisible(false);
				frameChangeUserInfo = new FrameChangeUserInfo(userToChange, user);
				frameChangeUserInfo.initial();
			}
		});

		buttonDelete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int currentRow = table.getSelectedRow();
				if (currentRow == -1) {
					JOptionPane.showMessageDialog(FrameUserManage.this, "请先选择一行", "提示",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				Object objectName = tableModel.getValueAt(currentRow, 0);
				String name = (String) objectName;
				if (name.equals(user.getName())) {
					JOptionPane.showMessageDialog(FrameUserManage.this, "管理员不能刪除自己", "提示",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				try {
					DataProcessing.deleteUser(name);
					DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
					tableModel.removeRow(currentRow);
					JOptionPane.showMessageDialog(FrameUserManage.this, "删除成功", "提示", JOptionPane.INFORMATION_MESSAGE);
					return;
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(FrameUserManage.this, "删除失败,请稍后重试", "提示",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
		});

		// 新增用户卡片
		panelAddUser = new JPanel();
		tabbedPane.addTab("新增用户", null, panelAddUser, null);
		labelAccount = new JLabel("用户名:");
		labelPassword = new JLabel("密   码:");
		labelRole = new JLabel("角   色:");
		buttonAdd = new JButton("添加");
		accountField = new JTextField();
		passwordField = new JTextField();
		String[] strRoles = { "Administrator", "Operator", "Browser" };
		comboBoxRole = new JComboBox<String>(strRoles);

		// 为panelAddUser 创建 GroupLayout
		GroupLayout layout = new GroupLayout(panelAddUser);
		panelAddUser.setLayout(layout);

		// 创建GroupLayout的水平连续组，，越先加入的ParallelGroup，优先级级别越高。
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGap(5);// 添加间隔
		hGroup.addGroup(layout.createParallelGroup().addComponent(labelAccount).addComponent(labelPassword)
				.addComponent(labelRole));
		hGroup.addGap(5);
		hGroup.addGroup(layout.createParallelGroup().addComponent(accountField).addComponent(passwordField)
				.addComponent(comboBoxRole));
		hGroup.addGroup(layout.createParallelGroup().addComponent(buttonAdd));
		hGroup.addGap(5);
		layout.setHorizontalGroup(hGroup);

		// 创建GroupLayout的垂直连续组，，越先加入的ParallelGroup，优先级级别越高。
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGap(10);
		vGroup.addGroup(layout.createParallelGroup().addComponent(labelAccount).addComponent(accountField));
		vGroup.addGap(10);
		vGroup.addGroup(layout.createParallelGroup().addComponent(labelPassword).addComponent(passwordField));
		vGroup.addGap(10);
		vGroup.addGroup(layout.createParallelGroup().addComponent(labelRole).addComponent(comboBoxRole));
		vGroup.addGap(5);
		vGroup.addGroup(layout.createParallelGroup(Alignment.TRAILING).addComponent(buttonAdd));
		vGroup.addGap(10);
		// 设置垂直组
		layout.setVerticalGroup(vGroup);

		// 添加监听者
		buttonAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (DataProcessing.searchByName(accountField.getText()) != null) {
					JOptionPane.showMessageDialog(FrameUserManage.this, "用户名已存在!", "提示",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				} else {
					User user = new User(accountField.getText(), passwordField.getText(),
							(String) comboBoxRole.getSelectedItem());
					try {
						DataProcessing.insertUser(user);
						JOptionPane.showMessageDialog(FrameUserManage.this, "添加成功!", "提示",
								JOptionPane.INFORMATION_MESSAGE);
						setVisible(false);
						FrameUserManage frameUserManage = new FrameUserManage(user);
						frameUserManage.initial();
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(FrameUserManage.this, "添加失败,请稍后重试!", "提示",
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}

			}
		});
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				FrameAdministrator frameAdministrator = new FrameAdministrator(user);
				frameAdministrator.init();
			};
		});

	}

	@Override
	public void initial() {
		setVisible(true);
	}

}
