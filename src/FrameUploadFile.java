import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
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
	private static String fileServecePath = "d:\\Multithreading\\Files\\";
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

		sureButton = new JButton("确定");
		skimButton = new JButton("浏览");

		setTitle("上传文档");
		setSize(350, 300);
		Toolkit toolkit = getToolkit();
		Dimension dimension = toolkit.getScreenSize();
		int screenHeight = dimension.height;
		int screenWidth = dimension.width;
		int frm_Height = this.getHeight();
		int frm_width = this.getWidth();
		this.setLocation((screenWidth - frm_width) / 2, (screenHeight - frm_Height) / 2);

		fileDownProgressBar = new JProgressBar();
		functionPanel = new JPanel();
		fileDownProgressBar.setPreferredSize(new Dimension(350, 48));
		this.getContentPane().add(BorderLayout.NORTH, fileDownProgressBar);
		this.getContentPane().add(BorderLayout.CENTER, functionPanel);

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
		// 添加监听者
		sureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (toUploadFile == null) {
					JOptionPane.showMessageDialog(FrameUploadFile.this, "未选择要上传的文件!", "提示",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				toUploadFileInfo = new Doc("" + DataProcessing.getDocNumber(), user.getName(), new Date().getTime(),
						"(上传者未写填写描述)",
						toUploadFile.getAbsolutePath().substring(toUploadFile.getAbsolutePath().lastIndexOf('\\') + 1));
				if (fileDescriptionArea.getText() != null)
					toUploadFileInfo.setDescription(fileDescriptionArea.getText());
				new Thread() {
					@Override
					public void run() {
						try {
							FileInputStream fileInput;
							fileInput = new FileInputStream(filePathField.getText());

							FileOutputStream fileOutput;
							fileOutput = new FileOutputStream(fileServecePath + toUploadFileInfo.getID() + toUploadFile
									.getAbsolutePath().substring(toUploadFile.getAbsolutePath().lastIndexOf('.')));
							FileChannel readFileChannel = fileInput.getChannel();
							FileChannel writeFileChannel = fileOutput.getChannel();

							int currentProgress = 0;
							long fileSize = readFileChannel.size();
							if (fileSize == 0L) {
								JOptionPane.showMessageDialog(FrameUploadFile.this, "不能上传空文件!", "提示",
										JOptionPane.INFORMATION_MESSAGE);
								fileInput.close();
								fileOutput.close();
								return;
							}
							sureButton.setEnabled(false);
							skimButton.setEnabled(false);
							fileDownProgressBar.setValue(0);
							fileDownProgressBar.setMaximum(100);
							for (long fileContent = 0L, transferSize; fileContent <= fileSize; fileContent += 100L) {
								if (fileSize - fileContent >= 100L)
									transferSize = 100L;
								else {
									transferSize = fileSize - fileContent;
								}
								writeFileChannel.transferFrom(readFileChannel, fileContent, transferSize);
								currentProgress = (int) ((double) fileContent * 100.0 / (double) fileSize);
								fileDownProgressBar.setValue(currentProgress);
								fileDownProgressBar.setString("上传已完成" + currentProgress + "%");
								fileDownProgressBar.setStringPainted(true);
							}
							fileDownProgressBar.setValue(100);
							fileDownProgressBar.setString("上传已完成");
							fileDownProgressBar.setStringPainted(true);
							DataProcessing.insertDoc(toUploadFileInfo);
							JOptionPane.showMessageDialog(FrameUploadFile.this, "上传成功!", "提示",
									JOptionPane.INFORMATION_MESSAGE);
							setVisible(false);
							FrameUploadFile frameUploadFile = new FrameUploadFile(user);
							frameUploadFile.initial();
							fileInput.close();
							fileOutput.close();
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
