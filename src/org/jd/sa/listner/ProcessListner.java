package org.jd.sa.listner;

import javax.swing.JPanel;

import org.jd.sa.frame.ApplicationFrame;
import org.jd.sa.panel.input.InputPanel;
import org.jd.sa.panel.output.OutputPanel;
import org.jd.sa.panel.process.ProcessPanel;

public class ProcessListner extends Thread {

	public static volatile int processToExecute = 1;

	@Override
	public void run() {
		listenProcessChange();
	}

	private void listenProcessChange() {
		int currProcess = 0;
		JPanel activePanel = null;
		InputPanel inputPanel = null;
		ProcessPanel processPanel = null;
		ApplicationFrame appFrame = new ApplicationFrame();

		while (true) {
			if (currProcess == processToExecute)
				continue;

			currProcess = processToExecute;
			switch (currProcess) {
			case 1:
				if (activePanel != null)
					appFrame.removePanel(activePanel);
				if (inputPanel == null)
					inputPanel = InputPanel.getInstance();
				activePanel = inputPanel;
				appFrame.addPanel(inputPanel);
				appFrame.pack();
				break;
			case 2:
				if (activePanel != null)
					appFrame.removePanel(activePanel);
				if (processPanel == null)
					processPanel = new ProcessPanel();
				activePanel = processPanel;
				appFrame.addPanel(processPanel);
				processPanel.startProcess();
				appFrame.pack();
				break;
			case 3:
				if (activePanel != null)
					appFrame.removePanel(activePanel);
				OutputPanel outputPanel = new OutputPanel();
				appFrame.addPanel(outputPanel);
				appFrame.pack();
				outputPanel.initializePanel();
				activePanel = outputPanel;
				appFrame.pack();
				break;
			default:
				break;
			}
		}
	}
}
