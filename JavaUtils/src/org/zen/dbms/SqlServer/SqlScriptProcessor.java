package org.zen.dbms.SqlServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.zen.utils.FileProcessor;
import org.zen.utils.IOReaderBuilder;

/**
 * Reads Sql script and parse into batch sql blocks. Derived class must override
 * processSqlBlock()
 * 
 */
public abstract class SqlScriptProcessor extends FileProcessor {
	public SqlScriptProcessor() {
		_pattern = new SqlScriptPatterns();
	}

	protected void processFile(File inFile) throws IOException {
		readFile(inFile);
		endRead(inFile);
	}

	/**
	 * Parse sql script, and consume SqlBlock in processSqlBlock()
	 */
	protected void readFile(File inFile) throws IOException {
		BufferedReader br = new BufferedReader(IOReaderBuilder.buildInReader(inFile.getAbsolutePath()));

		String filePath = inFile.getAbsolutePath();
		String currentDB = "";
		StringBuffer sqlText = new StringBuffer();
		ArrayList<String> actionLines = new ArrayList<String>();

		String line = null;
		boolean inComment = false;
		while ((line = br.readLine()) != null) {
			if (_pattern.isEmptyLine(line))// no bother to check empty line
			{
				appendLine(sqlText, line);
				continue;
			}

			// deal with comments first
			if (inComment) {
				if (line.indexOf("*/") > -1)
					inComment = false;
				appendLine(sqlText, line);
				continue;
			}

			if (line.indexOf("/*") > -1) {
				if (line.indexOf("*/") < 0)
					inComment = true;
				appendLine(sqlText, line);
				continue;
			}

			if (line.indexOf("--") > -1) {
				appendLine(sqlText, line);
				continue;
			}

			if (line.indexOf("PRINT ") > -1) // print statement
			{
				appendLine(sqlText, line);
				continue;
			}

			// Batch delimiter 
			if (_pattern.isBatchDelimiter(line)) {
				SqlBlock sb = buildSqlBlock(sqlText, actionLines, currentDB, filePath);
				processSqlBlock(sb);
				continue;
			}

			// Switch database
			String dbName = _pattern.checkDBContext(line);
			if (dbName != null && !dbName.equals(currentDB)) {
				SqlBlock sb = buildSqlBlock(sqlText, actionLines, currentDB, filePath);
				processSqlBlock(sb);

				currentDB = dbName;
				continue;
			}

			appendLine(sqlText, line);
			actionLines.add(line);
		}

		SqlBlock sb = buildSqlBlock(sqlText, actionLines, currentDB, filePath);
		processSqlBlock(sb);

		br.close();
	}

	abstract void processSqlBlock(SqlBlock sb);

	protected void endRead(File inFile) throws IOException {
	}

	private SqlBlock buildSqlBlock(StringBuffer sqlText, ArrayList<String> actionLines, String currentDB, String filePath) {
		if (sqlText.length() == 0)
			return null;

		SqlBlock sb = new SqlBlock();
		sb.addSource(filePath.replace(_baseScriptDir, ""));
		sb.setDatabase(currentDB);
		sb.addSqlText(sqlText.toString());

		// What's the action? Alter? Create? Drop? Set?
		_pattern.checkAction(sb, actionLines);

		sqlText.setLength(0);
		actionLines.clear();

		return sb;
	}

	private void appendLine(StringBuffer sqlText, String line) {
		if (sqlText.length() > 0 || line.length() > 0)
			sqlText.append(line).append("\r\n");
	}

	private SqlScriptPatterns _pattern;
}
