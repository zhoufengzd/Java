package org.zen.dbms.SqlServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SqlScriptReader extends SqlScriptProcessor {
	public SqlScriptReader(String outputDir) {
		_dbscripts = new HashMap<String, ArrayList<SqlBlock>>();
		_outputDir = outputDir;
	}

	public void endProcess() {
		try {
			_blockManager.writeSql(_outputDir, false);
			// false: keep the file sequence so we know how the objects are modified
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Write script logs after each read
	@Override
	protected void endRead(File inFile) throws IOException {
		writeLog(_dbscripts, _baseScriptDir, inFile);
		_dbscripts.clear();
	}

	protected void processSqlBlock(SqlBlock sb) {
		if (sb == null)
			return;

		String currentDB = sb.getDatabase();
		ArrayList<SqlBlock> dbSqlBlocks = _dbscripts.get(currentDB);
		if (dbSqlBlocks == null) {
			dbSqlBlocks = new ArrayList<SqlBlock>();
			_dbscripts.put(currentDB, dbSqlBlocks);
		}

		dbSqlBlocks.add(sb);
		_blockManager.add(sb);
	}

	private void writeLog(HashMap<String, ArrayList<SqlBlock>> dbscripts, String scriptDir, File patchFile) throws IOException {
		String fileName = patchFile.getName();
		String logDir = scriptDir + "\\log\\" + fileName.substring(0, fileName.indexOf('.'));
		(new File(logDir)).mkdirs();

		Iterator<Map.Entry<String, ArrayList<SqlBlock>>> it = dbscripts.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, ArrayList<SqlBlock>> pair = it.next();
			ArrayList<SqlBlock> sqlBatches = pair.getValue();

			for (int i = 0; i < sqlBatches.size(); i++) {
				SqlBlock sb = sqlBatches.get(i);

				String logFile = logDir + '\\' + i + ".sql";
				OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(logFile), "UTF-8");

				String action = null;
				if (sb.getActionDDL() != null)
					action = sb.getActionDDL().toString();
				if (action == null && sb.getActionDML() != null)
					action = sb.getActionDML().toString();
				if (action == null && sb.getActionMisc() != null)
					action = sb.getActionMisc().toString();
				if (action == null && sb.getActionSecurity() != null)
					action = sb.getActionSecurity().toString();

				if (action != null)
					writer.write("-- " + action + " -- \r\n");
				writer.write(sb.getSqlText(false));
				writer.close();
			}
		}
	}

	private SqlBlockManager _blockManager;
	private HashMap<String, ArrayList<SqlBlock>> _dbscripts; // database ==> scripts
	private String _outputDir;
}
