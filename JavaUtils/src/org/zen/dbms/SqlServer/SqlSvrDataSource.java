package org.zen.dbms.SqlServer;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SqlSvrDataSource {
	public static final int DefaultPort = 1433;
	public static final int DefaultTimeOutShort = 30; // 30 seconds
	public static final int DefaultTimeOutLong = 600; // 10 minutes

	public SqlSvrDataSource(String server, String user, String password) {
		setConnectionProperties(server, null, user, password, DefaultPort);
	}

	public SqlSvrDataSource(String server, String database, String user, String password) {
		setConnectionProperties(server, database, user, password, DefaultPort);
	}

	public void connect() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
		boolean result = false;
		try {
			checkDriverRegistry();
			_conn = DriverManager.getConnection(buildConnectionString(), _user, _password);
			result = true;

			if (_database != null)
				executeNonQuery("USE " + _database);
		}
		finally {
			if (!result)
				_conn = null;
		}
	}

	public void disConnect() {
		if (_conn != null) {
			try {
				if (!_conn.isClosed())
					_conn.close();
			}
			catch (Exception ex) {
			}
			_conn = null;
		}
	}

	public void executeNonQuery(String sqlText) throws SQLException {
		executeNonQuery(sqlText, DefaultTimeOutShort, false);
	}

	public void executeNonQuery(String sqlText, int cmdTimeOut) throws SQLException {
		executeNonQuery(sqlText, cmdTimeOut, false);
	}

	public void executeNonQuery(String sqlText, int cmdTimeOut, boolean ignoreError) throws SQLException {
		if (sqlText == null || sqlText.length() == 0)
			return;

		try {
			Statement cmd = buildSqlCommand(sqlText, cmdTimeOut);
			cmd.execute(sqlText);
		}
		catch (SQLException ex) {
			if (!ignoreError)
				throw ex;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T executeScalar(String sqlText) throws SQLException {
		ResultSet rs = executeDataSet(sqlText, DefaultTimeOutShort);
		if (rs.next())
			return (T) rs.getObject(1);

		return null;
	}

	public <T> ArrayList<T> executeList(String sqlText) throws SQLException {
		return executeList(sqlText, 1);
	}

	@SuppressWarnings("unchecked")
	public <T> ArrayList<T> executeList(String sqlText, int columnId) throws SQLException
	{
		ResultSet rs = executeDataSet(sqlText, DefaultTimeOutShort);
		if (rs == null)
			return null;

		ArrayList<T> result = new ArrayList<T>();
		while (rs.next())
			result.add((T) rs.getObject(columnId));

		return result;
	}

	// public DbDataReader ExecuteReader(String sqlText, int cmdTimeOut = 30)
	// {
	// if (String.IsNullOrEmpty(sqlText))
	// return null;
	//
	// if (!CheckConnection())
	// return null;
	//
	// using (Statement cmd = buildSqlCommand(sqlText, cmdTimeOut))
	// return cmd.ExecuteReader();
	// }
	public ResultSet executeDataSet(String sqlText) throws SQLException {
		return executeDataSet(sqlText, DefaultTimeOutShort);
	}

	public ResultSet executeDataSet(String sqlText, int cmdTimeOut) throws SQLException {
		if (sqlText == null || sqlText.length() == 0)
			return null;

		Statement cmd = buildSqlCommand(sqlText, cmdTimeOut);
		return cmd.executeQuery(sqlText);
	}

	// public DataTable BuildDataTable(String tblName)
	// {
	// const String SelectTblSqlFmt = @"SELECT TOP 1 * FROM [{0}]";
	// if (!CheckConnection())
	// return null;
	//
	// DataTable tbl = new DataTable(tblName);
	// using (Statement cmd = buildSqlCommand(String.Format(SelectTblSqlFmt,
	// tblName)))
	// {
	// SqlDataReader reader = cmd.ExecuteReader(CommandBehavior.SchemaOnly);
	// DataTable schema = reader.GetSchemaTable();
	//
	// DataColumnCollection columns = tbl.Columns;
	// foreach (DataRow row in schema.Rows)
	// {
	// String colName = row["ColumnName"].toString();
	// Type colType = row["DataType"] as Type;
	// boolean readOnly = (boolean)row["IsReadOnly"];
	//
	// DataColumn col = new DataColumn(colName, colType);
	// col.ReadOnly = readOnly;
	// columns.Add(col);
	// }
	// reader.Close();
	// }
	//
	// return tbl;
	// }

	// / <summary>
	// / Optional: use acceptedColumns to filter out unwanted columns
	// / </summary>
	// public int Load(DataTable dataTable, ICollection<String> acceptedColumns
	// = null)
	// {
	// if (dataTable == null || dataTable.Rows.Count < 1) // nothing to load
	// return 0;
	//
	// using (SqlBulkCopy bc = new SqlBulkCopy(_conn,
	// SqlBulkCopyOptions.KeepNulls | SqlBulkCopyOptions.KeepIdentity, null))
	// {
	// bc.BatchSize = dataTable.Rows.Count;
	// bc.DestinationTableName = dataTable.TableName;
	// bc.BulkCopyTimeout = 0;
	//
	// SqlBulkCopyColumnMappingCollection colMappings = bc.ColumnMappings;
	// foreach(DataColumn col in dataTable.Columns)
	// {
	// if (col.ReadOnly || (acceptedColumns != null &&
	// !acceptedColumns.Contains(col.ColumnName)))
	// continue;
	//
	// colMappings.Add(col.ColumnName, col.ColumnName);
	// }
	//
	// bc.WriteToServer(dataTable);
	// bc.Close();
	// }
	//
	// return dataTable.Rows.Count;
	// }

	// #region Private functions
	private void setConnectionProperties(String server, String database, String user, String password, int port) {
		_server = server;
		if (server != null && server.length() > 0) {
			int index = server.indexOf('\\');
			if (index >= 0) {
				_instance = server.substring(index + 1, server.length());
				_server = server.substring(0, index);
			}
		}

		_database = database;
		_user = user;
		_password = password;
		_port = port;
	}

	private void checkDriverRegistry() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
		if (_driver != null)
			return;

		boolean result = false;
		try {
			if (_driver == null)
				_driver = (Driver) Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();

			DriverManager.registerDriver(_driver);
			result = true;
		}
		finally {
			if (!result)
				_driver = null;
		}
	}

	private String buildConnectionString() {
		StringBuffer url = new StringBuffer("jdbc:jtds:sqlserver://");
		url.append(_server);
		url.append(":");
		url.append(_port);

		if (_instance != null && _instance.length() != 0) {
			url.append(";instance=");
			url.append(_instance);
		}

		return url.toString();
	}

	private Statement buildSqlCommand(String sqlText, int cmdTimeOut) throws SQLException {
		Statement cmd = _conn.createStatement();
		cmd.setQueryTimeout(cmdTimeOut);

		return cmd;
	}

	private static Driver _driver;
	private Connection _conn;
	private String _server;
	private String _instance;
	private String _database;
	private String _user;
	private String _password;
	private int _port;

}