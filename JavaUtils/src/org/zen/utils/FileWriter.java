package org.zen.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileWriter {
	public static void write(String filePath, String text) throws IOException {
		write(filePath, text, "UTF-8", false);
	}

	public static void write(String filePath, String text, String encoding) throws IOException {
		write(filePath, text, encoding, false);
	}

	public static void write(String filePath, String text, String encoding, boolean append) throws IOException {
		File fl = new File(filePath);
		fl.getParentFile().mkdirs();

		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fl, append), encoding);
		writer.write(text);
		writer.close();
	}
}
