package org.jd.sa.panel.output;

import java.io.File;

public class OutputStructure {

	private File path;
	private long size;
	private String type;

	public OutputStructure(File path, long size, String type) {
		this.path = path;
		this.size = size;
		this.type = type;
	}

	public OutputStructure() {
	}

	public File getPath() {
		return path;
	}

	public void setPath(File path) {
		this.path = path;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
