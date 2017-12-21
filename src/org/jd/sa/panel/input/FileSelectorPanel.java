package org.jd.sa.panel.input;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class FileSelectorPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1715001879615330960L;

	private JTextField field = null;

	public FileSelectorPanel() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.createFileSelectLabel();
		this.createPathSelectField();
		this.createPathSelectButton();
	}

	private void createFileSelectLabel() {
		JLabel jLabel = new JLabel("Please provide folder path to analyse");
		jLabel.setAlignmentX(LEFT_ALIGNMENT);
		this.add(jLabel);
	}

	private void createPathSelectField() {
		field = new JTextField();

		// TODO: remove below method
		InputPanel.setSelectedPath("C:\\test\\");

		field.setAlignmentX(LEFT_ALIGNMENT);
		field.setVisible(true);
		field.setColumns(30);
		this.add(field);
		field.addKeyListener(new TreeKeyListner());
		field.requestFocus();

		field.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				setPath();
			}

			public void removeUpdate(DocumentEvent e) {
				setPath();
			}

			public void insertUpdate(DocumentEvent e) {
				setPath();
			}

			public void setPath() {
				InputPanel.setSelectedPath(field.getText());
			}
		});
	}

	private class TreeKeyListner extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {

			if (e.getKeyCode() == 10) {
				AnalyseActionPanel.startAnalysis();
			}
		}
	}

	private void createPathSelectButton() {
		JButton pathButton = new JButton("Select Path");
		pathButton.addActionListener(this);
		pathButton.setActionCommand("path_selector_btn");
		pathButton.setPreferredSize(new Dimension(100, 20));
		pathButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this.add(pathButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			String path = null;
			if (field != null && field.getText() != null
					&& !field.getText().isEmpty()) {
				path = field.getText();
			}
			fileChooser(path);
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
	}

	private void fileChooser(String path) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {

		UIManager.put("FileChooser.openDialogTitleText", "Choose Folder Path");
		UIManager.put("FileChooser.lookInLabelText", "Look In");
		UIManager.put("FileChooser.openButtonText", "Select");
		UIManager.put("FileChooser.cancelButtonText", "Cancel");
		UIManager.put("FileChooser.fileNameLabelText", "File Name");
		UIManager.put("FileChooser.folderNameLabelText", "Folder Name");
		UIManager.put("FileChooser.filesOfTypeLabelText", "Type Files");
		UIManager
				.put("FileChooser.openButtonToolTipText", "Open Selected Path");
		UIManager.put("FileChooser.cancelButtonToolTipText", "Cancel");
		UIManager.put("FileChooser.fileNameHeaderText", "Folder Name");
		UIManager.put("FileChooser.upFolderToolTipText", "Up One Level");
		UIManager.put("FileChooser.homeFolderToolTipText", "Desktop");
		UIManager.put("FileChooser.newFolderToolTipText", "Create New Folder");
		UIManager.put("FileChooser.listViewButtonToolTipText", "List");
		UIManager.put("FileChooser.newFolderButtonText", "Create New Folder");
		UIManager.put("FileChooser.renameFileButtonText", "Rename File");
		UIManager.put("FileChooser.deleteFileButtonText", "Delete File");
		UIManager.put("FileChooser.filterLabelText", "Type Files");
		UIManager.put("FileChooser.detailsViewButtonToolTipText", "Details");
		UIManager.put("FileChooser.fileSizeHeaderText", "Size");
		UIManager.put("FileChooser.fileDateHeaderText", "Date Modified");

		JFileChooser chooser = new JFileChooser();

		if (path == null)
			path = ".";

		chooser.setCurrentDirectory(new java.io.File(path));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		SwingUtilities.updateComponentTreeUI(chooser);

		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			InputPanel.setSelectedPath((chooser.getSelectedFile()
					.getAbsolutePath()));
			field.setText(InputPanel.getSelectedPath());
		}
	}
}
