package org.zen.utils;

public class FileNamingHelper {

	public static String appendUpdate(String fileName) {
		return appendUpdate(fileName, ".updated");
	}

	public static String appendUpdate(String fileName, String updatedPostfix) {
		int lastDot = fileName.lastIndexOf(".");
		if (lastDot != -1) {
			String fileNamebase = fileName.substring(0, lastDot);
			String fileExt = fileName.substring(lastDot);
			return fileNamebase + updatedPostfix + fileExt;
		}
		else {
			return fileName + updatedPostfix;
		}
	}

	public static String changeExtension(String fileName, String newExtension) {
		int lastDot = fileName.lastIndexOf(".");
		if (lastDot != -1) {
			return fileName.substring(0, lastDot) + newExtension;
		}
		else {
			return fileName + newExtension;
		}
	}
}
