package org.jd.sa.process;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Analyzer extends Thread {

	private String _path;
	private long _sizeScanned = 0;
	private boolean _stopSignal = false;
	private FileStructure _fileStructure;

	private long _totalItems;

	private int _currProgress = 0;
	private String _currentPathInProcess;

	public Analyzer() {
		this._currProgress = 0;
		this._currentPathInProcess = "";
	}

	private FileStructure analyzePath(File path) {
		FileStructure structure = null;

		if (!_stopSignal) {
			structure = new FileStructure();
			_currentPathInProcess = path.getAbsolutePath();
			structure.setPath(path);

			if (path.isFile()) {
				structure.setFile(true);
				try {
					structure.setSize(Files.size(Paths.get(path.toURI())));

					_sizeScanned++;
					_currProgress = (int) ((_sizeScanned * 100) / _totalItems);

				} catch (IOException e) {
					System.out.println("Could not read file: "
							+ path.getAbsolutePath());
				}
			} else {
				// structure.setPath(path);

				File[] files = path.listFiles();

				if (files != null && files.length > 0) {

					List<FileStructure> subStructureList = new ArrayList<>();

					long dirSize = 0L;
					for (int i = 0; i < files.length; i++) {

						if (!files[i].canRead()) {
							System.err.println("Could not read path: "
									+ files[i].getAbsolutePath());
							continue;
						}

						FileStructure subStructure = analyzePath(files[i]);
						if (subStructure == null)
							continue;
						dirSize += subStructure.getSize();
						subStructureList.add(subStructure);
					}
					structure.setSize(dirSize);
					structure.setChildren(subStructureList);
					structure.setChildPresent(subStructureList.size() > 0);
				}
			}
		}
		return structure;
	}

	@Override
	public void run() {
		File f = new File(this._path);
		this._fileStructure = analyzePath(f);
		this._currentPathInProcess = null;
	}

	public FileStructure getFileStructure() {
		return this._fileStructure;
	}

	public void setPath(String path) {
		this._path = path;
	}

	public void terminate() {
		this._stopSignal = true;
	}

	public void setTotalItems(long totalItems) {
		this._totalItems = totalItems;
	}

	public int getCurrProgress() {
		return _currProgress;
	}

	public String getCurrentPathInProcess() {
		return _currentPathInProcess;
	}
}
