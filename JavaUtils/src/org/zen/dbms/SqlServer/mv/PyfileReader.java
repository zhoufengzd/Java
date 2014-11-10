package org.zen.dbms.SqlServer.mv;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import org.zen.utils.FileProcessor;
import org.zen.utils.FileWriter;
import org.zen.utils.IOReaderBuilder;

/**
 * 
 * Python file reader Convert those print statement to 3.x format
 */
public class PyfileReader extends FileProcessor {
	public PyfileReader() {
	}

	public static void main(String[] args) {
		PyfileReader reader = new PyfileReader();
		try {
			reader.run("D:\\Projects\\Python\\PP4E-Examples-1.4\\Examples", ".py", true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void processFile(File inFile) throws IOException {
		fixUpdatePatchIssue(inFile);
		// replacePrint(inFile);
	}

	@SuppressWarnings("unused")
	private void replacePrint(File inFile) throws IOException {
		String println = "print ";
		try
		{
			String outFilePath = _outputFilePath;
			// String outFilePath = inFile.getAbsolutePath();
			BufferedReader br = new BufferedReader(IOReaderBuilder.buildInReader(inFile.getAbsolutePath()));

			StringBuilder buffer = new StringBuilder();
			String line = null, lineLowerCase = null;
			boolean updated = false;
			while ((line = br.readLine()) != null) {
				lineLowerCase = line.trim().toLowerCase();

				if (lineLowerCase.length() > 0 && lineLowerCase.indexOf("print ") == 0
						&& (lineLowerCase.indexOf('(') < 0 || lineLowerCase.indexOf('(') > println.length() + 4)) {
					updated = true;

					StringBuilder cvLineBuffer = new StringBuilder();
					char[] dt = line.toCharArray();
					int index = 0, leadingWhiteSpaceCount = 0, trimmedStringLength = lineLowerCase.length();
					char c;

					// leading space
					while (index < dt.length) {
						c = dt[index];

						if (c == ' ' || c == '\t') {
							cvLineBuffer.append(c);
							index++;
						}
						else {
							break;
						}
					}
					leadingWhiteSpaceCount = index;

					// print: add '('
					while (index < dt.length) {
						c = dt[index++];
						cvLineBuffer.append(c);
						if (c == 't') { // t in 'print'.
							cvLineBuffer.append('(');
							break;
						}
					}

					// end of line, add ')'
					boolean endParenthesisAdded = false;
					while (index < dt.length) {
						c = dt[index++];

						// Handle comment
						if (c == '#') {
							cvLineBuffer.append(')');
							cvLineBuffer.append(' ');
							cvLineBuffer.append(c);
							endParenthesisAdded = true;
							continue;
						}

						// Last non space char
						if (index == leadingWhiteSpaceCount + trimmedStringLength) { // no more to append
							if (c != ',' && c != ';')
								cvLineBuffer.append(c);

							if (!endParenthesisAdded && c != '\\') {
								cvLineBuffer.append(')');
								endParenthesisAdded = true;
							}
							break;
						}

						// Replace ','
						if (c == ',')
							c = '+';

						cvLineBuffer.append(c);
					}
					buffer.append(cvLineBuffer.toString());
					buffer.append("\r\n");

					if (!endParenthesisAdded) {
						buffer.append(br.readLine());
						buffer.append(")\r\n");
						endParenthesisAdded = true;
					}

					continue; // continue readLine loop
				}

				buffer.append(line);
				buffer.append("\r\n");
			}
			br.close();

			if (updated)
				FileWriter.write(outFilePath, buffer.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Fix update patch issues left by 2to3 package
	@SuppressWarnings("unused")
	private void fixUpdatePatchIssue(File inFile) throws IOException {
		try
		{
			BufferedReader br = new BufferedReader(IOReaderBuilder.buildInReader(inFile.getAbsolutePath()));

			StringBuilder buffer = new StringBuilder();
			String line = null, lineLowerCase = null;
			boolean updated = false;
			while ((line = br.readLine()) != null) {
				lineLowerCase = line.trim().toLowerCase();

				if (lineLowerCase.length() > 0) {
					if (lineLowerCase.indexOf("print((") == 0) {
						updated = true;
						line = removeLastParenthesis(line.replace("print((", "print("));
					}
					else if (lineLowerCase.indexOf("eval(input(") == 0) {
						updated = true;
						line = removeLastParenthesis(line.replace("eval(input(", "input("));
					}
				}
				buffer.append(line);
				buffer.append("\r\n");
			}
			br.close();

			if (updated)
				FileWriter.write(inFile.getAbsolutePath(), buffer.toString()); // _outputFilePath, inFile.getAbsolutePath();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String removeLastParenthesis(String line) {
		int fromIndex = line.indexOf('#');
		if (fromIndex < 0)
			fromIndex = line.length();

		int parenIndex = line.lastIndexOf(')', fromIndex);
		return line.substring(0, parenIndex) + line.substring(parenIndex + 1);
	}
}
