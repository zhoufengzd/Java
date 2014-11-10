package org.zen.dbms.SqlServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import org.zen.utils.*;

public class TableDDLReader extends FileProcessor
{
	protected void processFile(File ddlFile) throws IOException
	{
		String[] nameParts = ddlFile.getName().split("\\.");
		String objName = "[" + nameParts[0] + "].[" + nameParts[1] + "]";

		StringBuffer sqlText = patchDDL(ddlFile, objName);
		FileWriter.write(ddlFile.getAbsolutePath(), sqlText.toString());
	}

	private StringBuffer patchDDL(File ddlFile, String objName) throws IOException
	{
		final String GuardFmtBegin = 
				"GO\r\n" +
				"\r\n" +
				"IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'%s') AND type in (N'U'))\r\n" + 
				"BEGIN\r\n" + 
				"\r\n" +
				"---------------------------------------------------------\r\n" +
				"-- Create Table... \r\n";
		final String GuardFmtEnd = 
				"-- End 'Create Table'\r\n" + 
				"---------------------------------------------------------\r\n" + 
				"END\r\n";

		BufferedReader br = new BufferedReader(IOReaderBuilder.buildInReader(ddlFile.getAbsolutePath()));
		StringBuffer sqlText = new StringBuffer();
		String line = null;

		while ((line = br.readLine()) != null)
		{
			if (line.equals("GO"))
				continue;

			if (line.indexOf("-- Create Table ") == 0) 	// create DDL lines started
			{
				sqlText.append(String.format(GuardFmtBegin, objName));
				continue;
			}
			
			if (line.indexOf("--") == 0) // comment line? add an extra line
				sqlText.append("\r\n");

			sqlText.append(line).append("\r\n");
		}
		sqlText.append(GuardFmtEnd);
		
		br.close();
		return sqlText;
	}
}
