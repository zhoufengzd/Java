package org.zen.dbms.SqlServer.mv;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.zen.utils.FileProcessor;
import org.zen.utils.IOReaderBuilder;

public class CreateCmdReader extends FileProcessor {
	public CreateCmdReader() {
		_dbMap = new HashMap<String, String>();
	}

	// Return: File Path --> DB Used
	public HashMap<String, String> getDBMap() {
		return _dbMap;
	}

	public void endProcess() {
	}

	protected void processFile(File inFile) throws IOException {
		BufferedReader br = new BufferedReader(IOReaderBuilder.buildInReader(inFile.getAbsolutePath()));

		String line = null;
		while ((line = br.readLine().toUpperCase()) != null) {
			line.trim();

			if (line.length() < 1)
				continue;

			if (line.indexOf("IF NOT DEFINED DB_") > -1 && line.indexOf(" SET ") > -1) {
				int ind = line.indexOf("=");
				if (ind > -1) {
					String currentDB = line.substring(ind + 1, line.length());
					_dbMap.put(inFile.getAbsolutePath().toLowerCase(), currentDB);
					break;
				}
			}
		}

		br.close();
	}

	private HashMap<String, String> _dbMap;
}
