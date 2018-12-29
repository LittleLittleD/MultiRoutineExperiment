package Client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;

public class FrameUploadFile extends FrameFunction {

	private static final long serialVersionUID = 1L;
	private JLabel labelID;
	private JLabel labelFileID;
	private JLabel labelFilePath;
	private JLabel labelFileDescription;
	private JTextField filePathField;
	private JTextArea fileDescriptionArea;
	private JPanel functionPanel;
	private JProgressBar fileDownProgressBar;
	private JButton sureButton;
	private JButton skimButton;
	private File toUploadFile;
	private Doc toUploadFileInfo;

	FrameUploadFile(User user) {
		super(user);

		labelID = new JLabel("" + DataProcessing.getDocNumber());
		labelFileID = new JLabel("文  档ID:    ");
		labelFilePath = new JLabel("文档路径:");
		labelFileDescription = new JLabel("文档描述:");

		filePathField = new JTextField(25);
		fileDescriptionArea = new JTextArea(30, 20);
		fileDescriptionArea.setLineWrap(true);

		sureButton = new JButton("确定");
		skimButton = new JButton("浏览");

		setTitle("上传文档");
		setSize(350, 300);
		setLocationRelativeTo(null);
		fileDownProgressBar = new JProgressBar();
		functionPanel = new JPanel();
		fileDownProgressBar.setPreferredSize(new Dimension(350, 48));
		this.getContentPane().add(BorderLayout.NORTH, fileDownProgressBar);
		this.getContentPane().add(BorderLayout.CENTER, functionPanel);

		// 文件描述框内敲下回车,表示选择上传
		fileDescriptionArea.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent keyEvent) {
				if (keyEvent.getKeyChar() == '\n') {
					// 按下了回车后 去掉文本中的回车 在完成上传
					fileDescriptionArea.setText(fileDescriptionArea.getText().replaceAll("\n", ""));
					sureButton.doClick();
				}
			}

			@Override
			public void keyReleased(KeyEvent keyEvent) {
			}

			@Override
			public void keyPressed(KeyEvent keyEvent) {
			}
		});

		// 为指定的 Container 创建 GroupLayout
		GroupLayout layout = new GroupLayout(functionPanel);
		functionPanel.setLayout(layout);

		// 创建GroupLayout的水平连续组，，越先加入的ParallelGroup，优先级级别越高。
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGap(5);// 添加间隔
		hGroup.addGroup(layout.createParallelGroup().addComponent(labelFileID).addComponent(labelFilePath)
				.addComponent(labelFileDescription));
		hGroup.addGap(5);
		hGroup.addGroup(layout.createParallelGroup().addComponent(labelID).addComponent(filePathField)
				.addComponent(fileDescriptionArea).addComponent(sureButton));
		hGroup.addGap(5);
		hGroup.addGroup(layout.createParallelGroup().addComponent(skimButton));
		layout.setHorizontalGroup(hGroup);

		// 创建GroupLayout的垂直连续组，，越先加入的ParallelGroup，优先级级别越高。
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGap(10);
		vGroup.addGroup(layout.createParallelGroup().addComponent(labelFileID).addComponent(labelID));
		vGroup.addGap(10);
		vGroup.addGroup(layout.createParallelGroup().addComponent(labelFilePath).addComponent(filePathField)
				.addComponent(skimButton));
		vGroup.addGap(10);
		vGroup.addGroup(
				layout.createParallelGroup().addComponent(labelFileDescription).addComponent(fileDescriptionArea));
		vGroup.addGroup(layout.createParallelGroup(Alignment.TRAILING).addComponent(sureButton));
		vGroup.addGap(10);
		// 设置垂直组
		layout.setVerticalGroup(vGroup);

//		pack();
		skimButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				int fileChooserResult = fileChooser.showDialog(FrameUploadFile.this, "选择文件");
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if (fileChooserResult == JFileChooser.APPROVE_OPTION) {
					toUploadFile = fileChooser.getSelectedFile();
					if (!toUploadFile.exists()) {
						JOptionPane.showMessageDialog(FrameUploadFile.this, "未选择", "提示",
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						filePathField.setText(toUploadFile.getAbsolutePath());
					}
				} else if (fileChooserResult == JFileChooser.CANCEL_OPTION) {
					JOptionPane.showMessageDialog(FrameUploadFile.this, "已取消选择", "提示", JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(FrameUploadFile.this, "文件选择框发生异常", "提示",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});

		// 添加监听者
		sureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (toUploadFile == null) {
					JOptionPane.showMessageDialog(FrameUploadFile.this, "未选择要上传的文件!", "提示",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				} else if (MyInfo.separator.matches(fileDescriptionArea.getText())) {
					JOptionPane.showMessageDialog(FrameUploadFile.this, "文件描述中不能出现" + MyInfo.separator, "提示",
							JOptionPane.INFORMATION_MESSAGE);
				}
				toUploadFileInfo = new Doc("" + DataProcessing.getDocNumber(), user.getName(),
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime()), "(上传者未写填写描述)",
						toUploadFile.getAbsolutePath().substring(toUploadFile.getAbsolutePath().lastIndexOf('\\') + 1));
				if (fileDescriptionArea.getText().length() != 0)
					toUploadFileInfo.setDescription(fileDescriptionArea.getText());
				new Thread() {
					@Override
					public void run() {
						try {
							// 建立连接
							DataRequestion dataRequestion;
							try {
								dataRequestion = new DataRequestion();
							} catch (UnknownHostException addressError) {
								JOptionPane.showMessageDialog(FrameUploadFile.this, "服务器地址错误!", "提示",
										JOptionPane.INFORMATION_MESSAGE);
								return;
							} catch (IOException connectError) {
								JOptionPane.showMessageDialog(FrameUploadFile.this, "服务器连接异常!", "提示",
										JOptionPane.INFORMATION_MESSAGE);
								return;
							}
							// 发送申请
							DataOutputStream fileOutput = new DataOutputStream(
									dataRequestion.uploadRequestion(toUploadFileInfo));
							DataInputStream fileInput = new DataInputStream(new FileInputStream(toUploadFile));
							// 先传输大小
							long fileSize = toUploadFile.length();
							fileOutput.writeLong(fileSize);

							fileOutput.flush();
							// 传输文件
							int length = 0;
							long curLength = 0;
							float currentProgress = 0;
							final int BytesEachTime = 10000;
							byte[] sendBytes = new byte[BytesEachTime];
							sureButton.setEnabled(false);
							skimButton.setEnabled(false);
							fileDownProgressBar.setValue(0);
							fileDownProgressBar.setMaximum(100);
							while ((length = fileInput.read(sendBytes, 0, sendBytes.length)) > 0) {
								fileOutput.write(sendBytes, 0, length);
								curLength = curLength + length;
								currentProgress = (float) ((double) curLength / (double) fileSize * 100.0);
								fileDownProgressBar.setValue((int) currentProgress);
								fileDownProgressBar.setString("上传已完成" + currentProgress + "%");
								fileDownProgressBar.setStringPainted(true);
							}
							// TODO 接受服务器返回的答复
							fileDownProgressBar.setValue(100);
							fileDownProgressBar.setString("上传完成");
							fileDownProgressBar.setStringPainted(true);
							fileOutput.flush();
							// 本地信息更新
							DataProcessing.insertDoc(toUploadFileInfo);
							JOptionPane.showMessageDialog(FrameUploadFile.this, "上传成功!", "提示",
									JOptionPane.INFORMATION_MESSAGE);
							setVisible(false);
							FrameUploadFile frameUploadFile = new FrameUploadFile(user);
							frameUploadFile.initial();
							// 关闭文件流
							fileInput.close();
							try {
								dataRequestion.close();
							} catch (IOException closeError) {
								JOptionPane.showMessageDialog(FrameUploadFile.this, "未成功断开与服务器的链接!即将关闭系统", "提示",
										JOptionPane.INFORMATION_MESSAGE);
								DataProcessing.systemQuit();
							}
						} catch (FileNotFoundException fileNotFoundException) {
							JOptionPane.showMessageDialog(FrameUploadFile.this, "路径错误!", "提示",
									JOptionPane.INFORMATION_MESSAGE);
							return;
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(FrameUploadFile.this, "上传失败!", "提示",
									JOptionPane.INFORMATION_MESSAGE);
							return;
						}
					}
				}.start();
			}
		});

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				FrameOperator frameOperator = new FrameOperator(user);
				frameOperator.init();
			};
		});
	}

	@Override
	public void initial() {
		setVisible(true);
	}
}
