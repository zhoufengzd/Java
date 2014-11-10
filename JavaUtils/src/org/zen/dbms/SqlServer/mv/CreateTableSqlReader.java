package org.zen.dbms.SqlServer.mv;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

import org.zen.dbms.SqlServer.SqlBlock;
import org.zen.dbms.SqlServer.SqlBlockManager;
import org.zen.dbms.SqlServer.SqlBlockManager.ScriptType;
import org.zen.dbms.SqlServer.SqlEnums;
import org.zen.dbms.SqlServer.SqlEnums.DBObjectType;
import org.zen.utils.FileProcessor;
import org.zen.utils.FileWriter;
import org.zen.utils.IOReaderBuilder;

// Read Create Table blocks from sql files, either in procedure or in table DDL file, and split out the pure table block
public class CreateTableSqlReader extends FileProcessor {
	final static String CreateTablePrefix = "CREATE TABLE ";
	final static int CreateTablePrefixLength = CreateTablePrefix.length();

	public CreateTableSqlReader() {
		_blockManager = new SqlBlockManager();
		_defaultDB = "MV_RESULTS";
	}

	public void writeLog(String outputDir) {
		writeLog(outputDir, false);
	}

	// Write sql log in each table file and reset cached
	public void writeLog(String outputDir, boolean reset) {
		try {
			_blockManager.writeSql(outputDir, true); // true: sort file path
			if (reset == true)
				_blockManager = new SqlBlockManager();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Write sql log in one file
	public void writePatchSql(String filePath, boolean reset) {
		final String CreateIfNotExistsFmt = "IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE [object_id] = OBJECT_ID(N'%s'))\r\n";

		HashMap<String, HashMap<DBObjectType, HashMap<String, SqlBlock>>> allScripts = _blockManager.getScripts(ScriptType.Create);
		Collection<SqlBlock> tblScripts = allScripts.get(_defaultDB).get(DBObjectType.TABLE).values();

		StringBuilder buffer = new StringBuilder();
		TreeSet<String> tblList = new TreeSet<String>();
		for (SqlBlock sb : tblScripts) {
			String tblName = sb.getObjectName();
			if (tblName.indexOf("#") > -1 || tblName.indexOf("TT_") == 0 || tblName.indexOf("_TMP") > -1 || tblName.indexOf("_TEMP") > -1)
				continue;

			tblList.add(tblName);

			buffer.append(String.format(CreateIfNotExistsFmt, tblName));
			buffer.append(sb.getSqlText());
			buffer.append("\r\n");
		}

		try {
			StringBuilder tbnamesBuffer = new StringBuilder();
			for (String tbl : tblList) {
				tbnamesBuffer.append(tbl);
				tbnamesBuffer.append("\r\n");
			}
			FileWriter.write(filePath.toLowerCase().replace(".sql", ".tables.txt"), tbnamesBuffer.toString());
			FileWriter.write(filePath, buffer.toString());
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		if (reset == true)
			_blockManager = new SqlBlockManager();
	}

	public void endProcess() {
	}

	protected void processFile(File inFile) throws IOException {
		final String DropTblSqlFmt = "IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'%s') AND type in (N'U')) " + LineSeparator +
				"	DROP TABLE %s " + LineSeparator + "GO" + LineSeparator + LineSeparator;
		final String DropTmpTblSqlFmt = "IF OBJECT_ID(N'tempdb..%s') IS NOT NULL " + LineSeparator +
				"	DROP TABLE %s " + LineSeparator + "GO" + LineSeparator + LineSeparator;
		final String CreateIfNotExistsFmt = "IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE [object_id] = OBJECT_ID(N'%s'))\r\n";

		BufferedReader br = new BufferedReader(IOReaderBuilder.buildInReader(inFile.getAbsolutePath()));

		String line = null, lineUpperCase = null;
		String tblName = null;
		StringBuilder sqlText = null;
		while ((line = br.readLine()) != null) {
			lineUpperCase = line.trim().toUpperCase().replace("  ", " ");

			if (lineUpperCase.length() < 1) {
				tryBuildSqlBlock(inFile, sqlText, tblName);
				sqlText = null;

				continue;
			}

			if (lineUpperCase.indexOf("END") == 0 || lineUpperCase.indexOf("ELSE") == 0) {
				tryBuildSqlBlock(inFile, sqlText, tblName);
				sqlText = null;

				continue;
			}

			if (sqlText != null) {
				if (lineUpperCase.indexOf("(") != 0 && lineUpperCase.indexOf(")") != 0)
					sqlText.append('\t');
				sqlText.append(lineUpperCase);
				sqlText.append(LineSeparator);
			}

			if (lineUpperCase.indexOf(CreateTablePrefix) == 0) {
				int endTblNameLoc = lineUpperCase.indexOf('(', CreateTablePrefixLength);
				if (endTblNameLoc < 0)
					endTblNameLoc = lineUpperCase.length();
				tblName = lineUpperCase.substring(CreateTablePrefixLength, endTblNameLoc);

				sqlText = new StringBuilder();
				if (tblName.indexOf("#") > -1)
					tblName = "TT_" + tblName.replace("#", "");
				/*
				if (tblName.indexOf("#") > -1) {
					sqlText.append(String.format(DropTmpTblSqlFmt, tblName, tblName));
					tblName = "TT_" + tblName.replace("#", "");
				}
				else {
					sqlText.append(String.format(DropTblSqlFmt, tblName, tblName));
				}
				*/

				sqlText.append(lineUpperCase);
				sqlText.append(LineSeparator);
			}
		}

		tryBuildSqlBlock(inFile, sqlText, tblName);
		sqlText = null;

		br.close();
	}

	private void tryBuildSqlBlock(File inFile, StringBuilder sqlText, String tblName) {
		if (sqlText == null)
			return;

		SqlBlock sb = new SqlBlock();
		sb.addSource(inFile.getAbsolutePath().replace(_baseScriptDir, ""));
		sb.setDatabase(_defaultDB);

		sqlText.append("GO");
		sqlText.append(LineSeparator);
		sb.addSqlText(sqlText.toString());

		sb.setObjectType(DBObjectType.TABLE);
		sb.setObjectName(tblName);
		sb.setActionDDL(SqlEnums.DDLAction.CREATE);

		_blockManager.add(sb);
	}

	private SqlBlockManager _blockManager;
	private String _defaultDB;
}
