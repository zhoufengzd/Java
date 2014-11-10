package org.zen.dbms.SqlServer;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple sql server script parser. Understands 'GO' and basic DDL / DML
 * keywords
 * 
 */
public class SqlScriptPatterns
{
	public SqlScriptPatterns()
	{
		InitPatterns();
	}

	public boolean isBatchDelimiter(String line)
	{
		return _ptnDelimiter.matcher(line).find();
	}

	public boolean isEmptyLine(String line)
	{
		if (line.length() == 0)
			return true;

		return _ptnEmptyLine.matcher(line).find();
	}

	public String checkDBContext(String line)
	{
		Matcher m = _ptnUseDB.matcher(line);
		return m.find() ? m.group(4).toUpperCase() : null;
	}

	// Detect if the script tries to DDL (CREATE ....), DML (UPDATE, SELECT, ...), 
	//   or else (SET ..., PRINT...)
	public void checkAction(SqlBlock sb, ArrayList<String> sqlLines)
	{
		final int MaxCheckSize = 8;
		final int ActionLineMinLength = 4;

		int checkSize = sqlLines.size() < MaxCheckSize ? sqlLines.size() : MaxCheckSize;
		if (checkSize == 0)
			return;

		for (int i = 0; i < checkSize; i++)
		{
			String line = sqlLines.get(i);
			if (_ifMatcher.IsMatched(sb, line))
				sqlLines.set(i, _ifMatcher.getActionPhrase());
		}

		for (LineChecker checker : _ptnActionList)
		{
			for (int i = 0; i < checkSize; i++)
			{
				String line = sqlLines.get(i);
				if (line.length() < ActionLineMinLength)
					continue;

				if (checker.IsMatched(sb, line))
					return;
			}
		}
	}

	private void InitPatterns()
	{
		_ptnActionList = new ArrayList<LineChecker>();
		_ptnActionList.add(new SecurityMatcher());
		_ptnActionList.add(new DDLMatcher());
		_ptnActionList.add(new DMLMatcher());
		_ptnActionList.add(new MiscMatcher());

		_ifMatcher = new IfMatcher();
		_ptnUseDB = Pattern.compile("(^\\s*)(USE )(\\[?)(\\w+)(]?)(\\s*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		_ptnDelimiter = Pattern.compile("(^\\s*)GO\\s*$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		_ptnEmptyLine = Pattern.compile("^\\s*$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	}

	private Pattern _ptnUseDB;
	private Pattern _ptnDelimiter;
	private Pattern _ptnEmptyLine;
	private IfMatcher _ifMatcher;
	private ArrayList<LineChecker> _ptnActionList;

}
