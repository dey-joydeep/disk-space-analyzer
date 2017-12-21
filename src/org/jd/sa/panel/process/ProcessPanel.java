package org.jd.sa.panel.process;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Paths;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.jd.sa.listner.ProcessListner;
import org.jd.sa.panel.input.InputPanel;
import org.jd.sa.process.Analyzer;
import org.jd.sa.process.FileStructure;
import org.jd.sa.resource.Resources;

public class ProcessPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 6633954912193611430L;

	private Analyzer _analyzer;
	private JLabel _headerLabel;
	private JTextArea _pathText;
	private JTextField _etField;
	private JProgressBar _progressBar;
	private JButton _cancelButton;

	private long _staredAt;
	private boolean _isTerminated;
	private int _prevProgress;

	public static FileStructure fileStructure;

	public ProcessPanel() {
		this.setLayout(null);
		this.createStartTimeField();
		this.createDurationLabel();
		this.createDurationField();
		this.createHeaderLabel();
		this.createCancelButton();
	}

	private void createStartTimeField() {
		JLabel stLabel = new JLabel("Started at: "
				+ Resources.getFormattedCurrentDate());
		stLabel.setVisible(true);
		stLabel.setBounds(5, 10, 200, 15);
		this.add(stLabel);
	}

	private void createDurationLabel() {
		JLabel stLabel = new JLabel("Time elapsed: ");
		stLabel.setVisible(true);
		stLabel.setBounds(5, 30, 85, 25);
		this.add(stLabel);
	}

	private void createDurationField() {
		this._etField = new JTextField(5);
		this._etField.setVisible(true);
		this._etField.setOpaque(false);
		this._etField.setEnabled(false);
		this._etField.setBorder(BorderFactory.createEmptyBorder());
		this._etField.setBackground(new Color(0, 0, 0, 0));
		this._etField.setDisabledTextColor(Color.BLACK);
		this._etField.setBounds(90, 30, 85, 25);
		this.add(this._etField);
	}

	private void createHeaderLabel() {
		this._headerLabel = new JLabel("Calculating total files...");
		this._headerLabel.setVisible(true);
		this._headerLabel.setBounds(5, 65, 135, 25);
		this.add(this._headerLabel);
	}

	private void createProgressBar() {
		this._progressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
		this._progressBar.setBounds(5, 95, Resources.FRAME_WIDTH - 30, 15);
		this._progressBar.setValue(0);
		this._progressBar.setStringPainted(true);
		this._progressBar.setVisible(true);
		this.add(this._progressBar);
	}

	private void createPathLabel() {
		this._pathText = new JTextArea(5, 5);
		this._pathText.setVisible(true);
		this._pathText.setLineWrap(true);
		this._pathText.setWrapStyleWord(true);
		this._pathText.setOpaque(false);
		this._pathText.setEnabled(false);
		this._pathText.setDisabledTextColor(Color.BLACK);
		this._pathText.setBounds(5, 125, Resources.FRAME_WIDTH - 30, 95);
		this.add(this._pathText);
	}

	private void createCancelButton() {
		this._cancelButton = new JButton("Cancel");
		this._cancelButton.addActionListener(this);
		this._cancelButton.setActionCommand("cancel_btn");
		this._cancelButton.setPreferredSize(new Dimension(90, 20));
		this._cancelButton.setVisible(true);
		this._cancelButton.setBounds(Resources.FRAME_WIDTH - 125, 230, 100, 20);
		this._cancelButton.setToolTipText("Cancel the scanning");
		this.add(this._cancelButton);
	}

	public void startProcess() {
		this._prevProgress = -1;
		this._isTerminated = false;
		this._staredAt = System.currentTimeMillis();
		this._analyzer = new Analyzer();
		this.updateDuration();
		long totalItems = Resources.getTotalItem(Paths.get(InputPanel
				.getSelectedPath()));
		if (totalItems == -1) {
			reset();
			ProcessListner.processToExecute = 1;
		} else {
			this._analyzer.setTotalItems(totalItems);
			this.updateComponent();
			this._analyzer.setPath(InputPanel.getSelectedPath());
			this._analyzer.start();
			this.updateContents();
		}
	}

	private void updateComponent() {
		_headerLabel.setText("Scanning...");
		this.createProgressBar();
		this.createPathLabel();
		this.validate();
		this.repaint();
	}

	private void updateDuration() {
		new Thread() {
			public void run() {
				while (_analyzer.getCurrentPathInProcess() != null
						&& !_isTerminated) {
					long duration = System.currentTimeMillis() - _staredAt;
					_etField.setText(Resources.getDurationFormatted(duration));
				}
			}
		}.start();
	}

	private void updateContents() {
		new Thread() {
			public void run() {
				while (_analyzer.getCurrentPathInProcess() != null) {
					_pathText.setText(_analyzer.getCurrentPathInProcess());

					int currentProgress = _analyzer.getCurrProgress();
					if (currentProgress > _prevProgress) {
						_prevProgress = currentProgress;
						_progressBar.setValue(currentProgress);
					}
				}

				reset();

				if (_isTerminated) {
					ProcessListner.processToExecute = 1;
				} else {
					fileStructure = _analyzer.getFileStructure();
					ProcessListner.processToExecute = 3;
				}
			}
		}.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Resources.terminateScan();
		if (this._analyzer != null && this._analyzer.isAlive())
			this._analyzer.terminate();
		_isTerminated = true;
	}

	private void reset() {
		_headerLabel.setText("Calculating total files...");
		this._etField.setText("");
		if (_progressBar != null)
			this.remove(_progressBar);
		if (_pathText != null)
			this.remove(_pathText);
		this.validate();
		this.repaint();
	}
}
