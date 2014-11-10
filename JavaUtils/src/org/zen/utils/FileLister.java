package org.zen.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class FileLister {
	public FileLister() {
		_fileList = new HashMap<String, List<File>>();
	}

	public FileLister(String nameExtensionFilter) {
		if (nameExtensionFilter != null && nameExtensionFilter != "*")
			_filter = new FilenameExtFilter(nameExtensionFilter);

		_fileList = new HashMap<String, List<File>>();
	}

	public void reset() {
		_fileList.clear();
	}

	public void listFiles(String fileDirectory) throws IOException {
		listFiles(fileDirectory, false);
	}

	public void listFiles(String fileDirectory, boolean recursive) throws IOException {
		File fileDir = new File(fileDirectory);
		if (!fileDir.exists())
			return;

		File[] files = fileDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (recursive && file.isDirectory())
				listFiles(file.getAbsolutePath(), recursive);

			if (_filter == null || _filter.accept(fileDir, file.getName())) {
				List<File> subList = GenericUtil.getValueList(_fileList, fileDir.getAbsolutePath().toLowerCase());
				subList.add(file);
			}
		}
	}

	/**
	 * 
	 * @return directory name --> files map
	 */
	public HashMap<String, List<File>> getFileList() {
		return _fileList;
	}

	private class FilenameExtFilter implements FilenameFilter {
		public FilenameExtFilter(String extension) {
			_extension = extension.toLowerCase();
		}

		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(_extension);
		}

		private String _extension;
	}

	private FilenameExtFilter _filter;
	private HashMap<String, List<File>> _fileList;
}