package org.zen.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileFormatter extends FileProcessor {

	public FileFormatter() {
		_linebreak = "\r\n";
		_encoding = "UTF-8";
	}

	public void setEncoding(String encoding) {
		_encoding = encoding;
	}

	public void setLinebreak(String linebreak) {
		_linebreak = linebreak;
	}

	// Only keep line break if it's followed by space / tab
	protected void processFile(File inputFile) throws IOException {
		BufferedReader br = new BufferedReader(IOReaderBuilder.buildInReader(inputFile.getAbsolutePath()));
		String outputFilePath = FileNamingHelper.appendUpdate(inputFile.getAbsolutePath());

		String currentline = null;
		String previousline = null;

		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFilePath, true), _encoding);
		while ((currentline = br.readLine()) != null) {
			if (previousline != null && (currentline.length() > 0) && Character.isWhitespace(currentline.charAt(0)))
				writer.write(_linebreak);

			writer.write(currentline);
			previousline = currentline;
		}

		br.close();
		writer.close();
	}

	private String _linebreak;
	private String _encoding;
}
