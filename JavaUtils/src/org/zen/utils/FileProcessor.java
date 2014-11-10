package org.zen.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public abstract class FileProcessor {
	protected final static String LineSeparator = System.getProperty("line.separator");

	public void run(String fileDirectory) throws IOException {
		run(fileDirectory, "*", false);
	}

	public void run(String fileDirectory, String extFilter) throws IOException {
		run(fileDirectory, extFilter, false);
	}

	public void run(String fileDirectory, String extFilter, boolean recursive) throws IOException {
		_baseScriptDir = fileDirectory;

		FileLister lister = new FileLister(extFilter);
		lister.listFiles(fileDirectory, recursive);
		HashMap<String, List<File>> fileList = lister.getFileList();

		Iterator<String> ki = fileList.keySet().iterator();
		while (ki.hasNext()) {
			String dir = ki.next();

			List<File> files = fileList.get(dir);
			for (File f : files) {
				_outputFilePath = f.getAbsolutePath().replace(_baseScriptDir, _baseScriptDir + "_out");
				processFile(f);
			}
		}

	}

	abstract protected void processFile(File inFile) throws IOException;

	public void endProcess() {
	}

	protected String _outputFilePath;// default output folder: _baseScriptDir + "_out"
	protected String _baseScriptDir;
}
