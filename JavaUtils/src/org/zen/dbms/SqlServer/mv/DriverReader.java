package org.zen.dbms.SqlServer.mv;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.zen.utils.FileProcessor;
import org.zen.utils.IOReaderBuilder;

/**
 * 
 * MV driver.txt reader
 * 
 */
public class DriverReader extends FileProcessor {
	final int PrefixTagLength = 4;

	public DriverReader() {
		_dbFileList = new HashMap<String, ArrayList<File>>();
	}

	// Returns db name --> Files list
	public HashMap<String, ArrayList<File>> getDBFileList() {
		return _dbFileList;
	}

	@Override
	protected void processFile(File inFile) throws IOException {
		try
		{
			String baseDir = inFile.getParent();
			BufferedReader br = new BufferedReader(IOReaderBuilder.buildInReader(inFile.getAbsolutePath()));

			String line = null, lineLowerCase = null;
			ArrayList<File> flist = null;
			while ((line = br.readLine()) != null) {
				lineLowerCase = line.trim().toLowerCase();

				//msg:Installing Quality Module
				if (lineLowerCase.indexOf("msg:") == 0)
					continue;

				//use:MV_RESULTS
				if (lineLowerCase.indexOf("use:") == 0) {
					String dbName = lineLowerCase.substring(PrefixTagLength);
					flist = _dbFileList.get(dbName);
					if (flist == null) {
						flist = new ArrayList<File>();
						_dbFileList.put(dbName, flist);
					}
					continue;
				}

				//sql:SET ANSI_PADDING ON
				if (lineLowerCase.indexOf("sql:") == 0)
					continue;

				// run:Insert_KPI_Configuration.sql
				if (lineLowerCase.indexOf("run:") == 0)
					flist.add(new File(baseDir + "\\" + lineLowerCase.substring(PrefixTagLength)));
			}
			br.close();
		}
		catch (Exception e) {
			e.printStackTrace();

		}
	}

	private HashMap<String, ArrayList<File>> _dbFileList;
}
