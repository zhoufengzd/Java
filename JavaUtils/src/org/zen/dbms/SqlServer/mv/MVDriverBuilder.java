package org.zen.dbms.SqlServer.mv;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.zen.utils.FileLister;
import org.zen.utils.FileWriter;

public class MVDriverBuilder {
	public MVDriverBuilder() {
	}

	public void BuildDriverFile(String baseSchemaDir, String databaseName) throws IOException {
		final String MessageFmt = "msg:%s\r\n";
		final String RunSqlFmt = "run:%s\\%s\r\n";
		final String ScriptBeginFmt = "use:MASTER\r\n" + "run:Create_%s.SQL\r\n" + "\r\n" + "msg:%s Schema\r\n" + "use:%s\r\n"
				+ "\r\n";
		final String ScriptEndFmt = "msg:%s Installation complete\r\n";

		final String[] dbObjectTypes = new String[] { "Schema", "User", "Role", "Default", "Synonym", "Rule",
				"Partition Function", "Partition Scheme", "Table", "View", "Trigger", "Function", "Stored Procedure", };

		if (_lister == null)
			_lister = new FileLister(".sql");

		StringBuilder driverText = new StringBuilder();
		driverText.append(String.format(ScriptBeginFmt, databaseName, databaseName, databaseName));
		for (String dbObjType : dbObjectTypes) {
			String scriptDir = baseSchemaDir + "\\" + databaseName + "\\" + dbObjType;
			_lister.listFiles(scriptDir);

			List<File> sqlFiles = _lister.getFileList().get(scriptDir);
			if (sqlFiles != null) {
				driverText.append(String.format(MessageFmt, dbObjType));
				for (File fl : sqlFiles)
					driverText.append(String.format(RunSqlFmt, dbObjType, fl.getName()));

				driverText.append("\r\n");
			}
			_lister.reset();
		}
		driverText.append(String.format(ScriptEndFmt, databaseName));

		String driverFilePath = baseSchemaDir + "\\" + databaseName + "\\driver.txt";
		FileWriter.write(driverFilePath, driverText.toString());
	}

	private FileLister _lister;
}
