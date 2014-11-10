package org.zen.dbms.SqlServer;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import org.zen.dbms.SqlServer.SqlEnums.DBObjectType;
import org.zen.dbms.SqlServer.SqlEnums.DDLAction;

public class SqlBlock {
	public SqlBlock() {
		_sourceFiles = new LinkedHashSet<String>();
		_sqlTexts = new ArrayList<String>();
	}

	public void addSource(String sourceFile) {
		_sourceFiles.add(sourceFile);
	}

	public Set<String> getSourceList() {
		return _sourceFiles;
	}

	public void setDatabase(String database) {
		_database = database;
	}

	public String getDatabase() {
		return _database;
	}

	public void setObjectName(String target) {
		_dbObject = cleanObjectName(target);
	}

	public String getObjectName() {
		return _dbObject;
	}

	public void setObjectType(SqlEnums.DBObjectType objectType) {
		_objectType = objectType;
	}

	public SqlEnums.DBObjectType getObjectType() {
		return _objectType;
	}

	public void setActionSecurity(SqlEnums.SecurityAction actionSecurity) {
		_actionSecurity = actionSecurity;
	}

	public SqlEnums.SecurityAction getActionSecurity() {
		return _actionSecurity;
	}

	public void setActionDDL(SqlEnums.DDLAction action) {
		_actionDDL = action;
	}

	public SqlEnums.DDLAction getActionDDL() {
		return _actionDDL;
	}

	public void setActionDML(SqlEnums.DMLAction actionDML) {
		_actionDML = actionDML;
	}

	public SqlEnums.DMLAction getActionDML() {
		return _actionDML;
	}

	public void setActionMisc(SqlEnums.MiscAction actionMisc) {
		_ActionMisc = actionMisc;
	}

	public SqlEnums.MiscAction getActionMisc() {
		return _ActionMisc;
	}

	public void addSqlText(String sqlText) {
		_sqlTexts.add(sqlText);
	}

	public void replaceSqlText(String sqlText) {
		_sqlTexts.clear();
		_sqlTexts.add(sqlText);
	}

	public String getSqlText() {
		return getSqlText(false, false);
	}

	public String getSqlText(boolean addHeader) {
		return getSqlText(addHeader, addHeader ? true : false);
	}

	// Note: sortSourceFiles is only effective when addHeader is true
	public String getSqlText(boolean addHeader, boolean sortSourceFiles) {
		StringBuilder buffer = new StringBuilder();

		if (addHeader) {
			logSourceFiles(buffer, sortSourceFiles ? (new TreeSet<String>(_sourceFiles)) : _sourceFiles);
			formatDropHeader(buffer);
		}

		for (int index = 0; index < _sqlTexts.size(); index++) {
			if (index > 0)
				buffer.append("GO\r\n\r\n");

			String sql = _sqlTexts.get(index);
			buffer.append(sql);
		}

		return buffer.toString();
	}

	private void logSourceFiles(StringBuilder buffer, Iterable<String> sourceFiles) {
		for (String src : sourceFiles) {
			buffer.append("-- ");
			buffer.append(src);
			buffer.append("\r\n");
		}
		buffer.append("\r\n");
	}

	private void formatDropHeader(StringBuilder buffer) {
		if (_objectType == DBObjectType.FUNCTION || _objectType == DBObjectType.PROCEDURE || _objectType == DBObjectType.VIEW) {
			if (_actionDDL == DDLAction.CREATE || _actionDDL == DDLAction.ALTER) {
				final String DROP_FORMAT = "IF EXISTS (SELECT 1 FROM sys.objects WHERE [object_id] = OBJECT_ID(N'%s'))\r\n" + "  DROP %s %s\r\nGO\r\n\r\n";

				buffer.append(String.format(DROP_FORMAT, _dbObject, _objectType, _dbObject));
			}
		}
	}

	private String cleanObjectName(String rawName) {
		@SuppressWarnings("unused")
		int dotCount = 0;
		StringBuilder name = new StringBuilder();
		for (int i = 0; i < rawName.length(); i++) {
			char c = rawName.charAt(i);
			if (c == '[' || c == ']')
				continue;

			if (c == '.')
				dotCount++;
			else if (c < 'A' || c > 'z' || c == '\\' || c == '^' || c == '`')
				break;

			name.append(c);
		}

		return name.toString();
	}

	public void setId(int id) {
		_id = id;
	}

	public int getId() {
		return _id;
	}

	private LinkedHashSet<String> _sourceFiles;
	private String _database;
	private String _dbObject;
	private SqlEnums.DBObjectType _objectType;

	private SqlEnums.SecurityAction _actionSecurity;
	private SqlEnums.DDLAction _actionDDL;
	private SqlEnums.DMLAction _actionDML;
	private SqlEnums.MiscAction _ActionMisc;
	private ArrayList<String> _sqlTexts;

	private int _id;
}
