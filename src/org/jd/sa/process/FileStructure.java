package org.jd.sa.process;

import java.io.File;
import java.util.List;

public class FileStructure {

	private File path;
	private boolean file;
	private boolean childPresent;
	private long size;
	private List<FileStructure> children;

	public File getPath() {
		return path;
	}

	public void setPath(File path) {
		this.path = path;
	}

	public boolean isFile() {
		return file;
	}

	public void setFile(boolean file) {
		this.file = file;
	}

	public boolean isChildPresent() {
		return childPresent;
	}

	public void setChildPresent(boolean childPresent) {
		this.childPresent = childPresent;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public List<FileStructure> getChildren() {
		return children;
	}

	public void setChildren(List<FileStructure> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return "FileStructure [" + (path != null ? "path=" + path + ", " : "")
				+ ", file=" + file + ", childPresent=" + childPresent
				+ ", size=" + size + ", "
				+ (children != null ? "children=" + children : "") + "]";
	}
}
