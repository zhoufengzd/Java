package org.zen.dbms.SqlServer.mv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;

import org.zen.dbms.SqlServer.SqlSvrDataSource;
import org.zen.utils.ToKenScriptBuilder;

public class ComparisonScriptBuilder {
	static String QueryColumnsSqlFmt = "EXEC [%s].[Util].[ListColumns] '%s'"; // database, table
	static String GetColumnListSqlFmt =
			"SELECT [ColumnName] FROM [%s].[Util].[TableColumns] \r\n" +
					"  WHERE [ColumnName] NOT LIKE 'Updated%%' AND [ColumnName] NOT LIKE 'ICD_VERSION' \r\n" +
					"    AND [IsIdentity] <> 1 AND [IsComputed] <> 1"; //%s: database

	public ComparisonScriptBuilder(String templateFilePath) {
		_comparisonTemplate = templateFilePath;
	}

	public void buildFromDriverFile(String driverFileDir, String outputDir) {
		ToKenScriptBuilder scb = new ToKenScriptBuilder(_comparisonTemplate);

		try {
			DriverReader reader = new DriverReader();
			reader.run(driverFileDir, "driver.txt");
			HashMap<String, ArrayList<File>> scripts = reader.getDBFileList();

			// CIGNASRV\\DEV1, mvdell7-64bit\\JFK
			SqlSvrDataSource ds = new SqlSvrDataSource("CIGNASRV\\DEV1", "HSMUser", "HSM@r00t1");
			ds.connect();

			for (String db : scripts.keySet()) {
				ArrayList<File> dbScripts = scripts.get(db);
				for (File fl : dbScripts) {
					String tblName = (fl.getName().toLowerCase().replace(".sql", ""));

					// Skip tmp tables
					if (tblName.indexOf("_tmp") > 0 || tblName.indexOf("_temp") > 0 || tblName.indexOf("tt_") > -1)
						continue;

					// Build comparison file
					HashMap<String, String> tokenValues = new HashMap<String, String>();
					tokenValues.put("$$DATABASE$$", db);

					tokenValues.put("$$TABLE_NAME$$", tblName);
					ds.executeNonQuery(String.format(QueryColumnsSqlFmt, db, tblName), 100, true);

					ArrayList<String> columns = ds.executeList(String.format(GetColumnListSqlFmt, db));
					StringBuilder colBuffer = null;
					for (String col : columns) {
						if (colBuffer == null) {
							colBuffer = new StringBuilder();
							colBuffer.append(col);
						}
						else {
							colBuffer.append(", ");
							colBuffer.append(col);
						}
					}

					if (colBuffer != null) {
						tokenValues.put("$$COLUMNS$$", colBuffer.toString());
						scb.buildScript(tokenValues, outputDir + "\\" + tblName.replace('.', '_') + ".sql");
					}
					else {
						System.out.println("Invalid table name: " + tblName);
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void buildFromTableFile(String tblFileDir, String dbName) {
		buildFromTableFile(tblFileDir, dbName, tblFileDir);
	}

	public void buildFromTableFile(String tblFileDir, String dbName, String outputDir) {
		try {
			TablesReader reader = new TablesReader();
			reader.run(tblFileDir, "tables.txt");
			LinkedHashSet<String> tbls = reader.getTableList();

			doBuildFromTables(tbls, dbName, outputDir);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void doBuildFromTables(Collection<String> tbls, String dbName, String outputDir) {
		ToKenScriptBuilder scb = new ToKenScriptBuilder(_comparisonTemplate);

		try {
			// CIGNASRV\\DEV1, mvdell7-64bit\\JFK
			SqlSvrDataSource ds = new SqlSvrDataSource("CIGNASRV\\DEV1", "HSMUser", "HSM@r00t1");
			ds.connect();

			for (String tblName : tbls) {

				// Build comparison file
				HashMap<String, String> tokenValues = new HashMap<String, String>();
				tokenValues.put("$$DATABASE$$", dbName);

				tokenValues.put("$$TABLE_NAME$$", tblName);
				ds.executeNonQuery(String.format(QueryColumnsSqlFmt, dbName, tblName), 100, true);

				ArrayList<String> columns = ds.executeList(String.format(GetColumnListSqlFmt, dbName));
				StringBuilder colBuffer = null;
				for (String col : columns) {
					if (colBuffer == null) {
						colBuffer = new StringBuilder();
						colBuffer.append(col);
					}
					else {
						colBuffer.append(", ");
						colBuffer.append(col);
					}
				}

				if (colBuffer != null) {
					tokenValues.put("$$COLUMNS$$", colBuffer.toString());
					scb.buildScript(tokenValues, outputDir + "\\" + tblName.replace('.', '_') + ".sql");
				}
				else {
					System.out.println("Invalid table name: " + tblName);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String _comparisonTemplate;
}
