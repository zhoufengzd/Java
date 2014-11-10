package org.zen.dbms.SqlServer;

import java.sql.SQLException;

public class SqlScriptRunner extends SqlScriptProcessor {
	public SqlScriptRunner(SqlSvrDataSource ds) {
		_dataSource = ds;
	}

	protected void processSqlBlock(SqlBlock sb) {
		if (sb == null)
			return;

		try {
			String sbDatabase = sb.getDatabase();
			if (_currentDB == null || (sbDatabase != null && !_currentDB.equals(sbDatabase))) {
				_dataSource.executeNonQuery("USE " + sbDatabase);
				_currentDB = sbDatabase;
			}
			_dataSource.executeNonQuery(sb.getSqlText(false));
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private SqlSvrDataSource _dataSource;
	private String _currentDB;
}
