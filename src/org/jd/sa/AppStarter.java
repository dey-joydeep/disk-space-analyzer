package org.jd.sa;

import org.jd.sa.listner.ProcessListner;
import org.jd.sa.resource.Resources;

public class AppStarter {

	public static void main(String[] args) {
		Resources.setLookAndFeel();
		ProcessListner listner = new ProcessListner();
		listner.start();
	}
}
