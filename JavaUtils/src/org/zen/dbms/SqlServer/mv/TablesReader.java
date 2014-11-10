package org.zen.dbms.SqlServer.mv;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;

import org.zen.utils.FileProcessor;
import org.zen.utils.IOReaderBuilder;

/**
 * 
 * MV tables.txt reader
 * 
 */
public class TablesReader extends FileProcessor {
	final int PrefixTagLength = 4;

	public TablesReader() {
		_tblList = new LinkedHashSet<String>();
	}

	// Returns table list
	public LinkedHashSet<String> getTableList() {
		return _tblList;
	}

	@Override
	protected void processFile(File inFile) throws IOException {
		try
		{
			BufferedReader br = new BufferedReader(IOReaderBuilder.buildInReader(inFile.getAbsolutePath()));

			String line = null, lineLowerCase = null;
			while ((line = br.readLine()) != null) {
				lineLowerCase = line.trim().toLowerCase();

				if (lineLowerCase.length() < 1)
					continue;
				if (lineLowerCase.indexOf(':') == 0)
					continue;

				_tblList.add(line);
			}
			br.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private LinkedHashSet<String> _tblList;
}
