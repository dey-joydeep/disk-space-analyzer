package org.jd.sa.panel.input;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jd.sa.listner.ProcessListner;

public class AnalyseActionPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1715001879615330960L;

	private static JButton _analyseButton;

	public AnalyseActionPanel() {
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.createAnalyseButton();
	}

	private void createAnalyseButton() {
		if (_analyseButton == null) {
			_analyseButton = new JButton("Scan");
			_analyseButton.addActionListener(this);
			_analyseButton.setActionCommand("analyse_btn");
			_analyseButton.setPreferredSize(new Dimension(90, 20));
			_analyseButton.setVisible(true);
			_analyseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
			this.add(_analyseButton);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (InputPanel.getSelectedPath() == null
				|| InputPanel.getSelectedPath().isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"Please select a path to begin analysis.", "Error",
					JOptionPane.ERROR_MESSAGE);
		} else {
			File root = new File(InputPanel.getSelectedPath());

			if (!root.exists()) {

				JOptionPane.showMessageDialog(this, "Path does not exist: "
						+ InputPanel.getSelectedPath(), "Error",
						JOptionPane.ERROR_MESSAGE);
			} else if (root.isFile()) {

				JOptionPane.showMessageDialog(
						this,
						"Given path cannot be a file: "
								+ InputPanel.getSelectedPath(), "Error",
						JOptionPane.ERROR_MESSAGE);

			} else {
				ProcessListner.processToExecute = 2;
			}
		}
	}

	protected static void startAnalysis() {
		_analyseButton.doClick();
	}
}
