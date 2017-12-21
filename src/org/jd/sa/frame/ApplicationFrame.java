package org.jd.sa.frame;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jd.sa.resource.IconLoader;
import org.jd.sa.resource.IconLoader.Icons;
import org.jd.sa.resource.Resources;

public class ApplicationFrame extends JFrame {

	private static final long serialVersionUID = -6831118001875748606L;

	public ApplicationFrame() {
		this.setTitle("Disk Space Analyzer (-Joydeep Dey)");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setResizable(false);
		this.setSize(getPreferredSize());
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height
				/ 2 - this.getSize().height / 2);
		this.setIconImage(IconLoader.getIconImage(Icons.FRAME_ICON));
		this.pack();
	}

	public void addPanel(JPanel panel) {
		this.getContentPane().add(panel);
		this.validate();
		this.repaint();
	}

	public Dimension getPreferredSize() {
		return new Dimension(Resources.FRAME_WIDTH, Resources.FRAME_HEIGHT);
	}

	public void removePanel(JPanel panel) {
		this.remove(panel);
		this.validate();
		this.repaint();
	}
}
