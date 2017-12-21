package org.jd.sa.panel.input;

import javax.swing.JPanel;

public class InputPanel extends JPanel {

	private static final long serialVersionUID = 1715001879615330960L;

	private static String selectedPath;

	public static final InputPanel getInstance() {
		return new InputPanel();
	}

	private InputPanel() {
		this.add(new FileSelectorPanel());
		this.add(new AnalyseActionPanel());
	}

	public static String getSelectedPath() {
		return selectedPath;
	}

	public static void setSelectedPath(String selectedPath) {
		InputPanel.selectedPath = selectedPath;
	}
}
