package org.zen.dbms.SqlServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.zen.dbms.SqlServer.SqlEnums.DBObjectType;
import org.zen.dbms.SqlServer.SqlEnums.DDLAction;
import org.zen.utils.GenericUtil;

/**
 * SqlBlockManager: Keep sql blocks by type and writes out sql scripts in order
 * Maintains original file source in order
 * 
 */
public class SqlBlockManager
{
	public enum ScriptType {
		Create, Drop, AlterTable, UpdateTable, Security
	};

	public SqlBlockManager()
	{
		_dbCreateScripts = new HashMap<String, HashMap<DBObjectType, HashMap<String, SqlBlock>>>();
		_dbDropScripts = new HashMap<String, HashMap<DBObjectType, HashMap<String, SqlBlock>>>();
		_dbTableAlterScripts = new HashMap<String, HashMap<DBObjectType, HashMap<String, SqlBlock>>>();
		_dbDMLScripts = new HashMap<String, HashMap<DBObjectType, HashMap<String, SqlBlock>>>();
		_dbSecurityScripts = new HashMap<String, HashMap<DBObjectType, HashMap<String, SqlBlock>>>();

		_dbDMLSourceMap = new HashMap<String, SqlBlock>();
	}

	public void add(SqlBlock sb)
	{
		String database = sb.getDatabase();

		if (sb.getActionDDL() != null)
		{
			DDLAction action = sb.getActionDDL();
			DBObjectType dbObjectType = sb.getObjectType();

			if (action == DDLAction.CREATE)
			{
				addSqlBlock(getLocalMap(_dbCreateScripts, database, dbObjectType), sb);
			}
			else if (action == DDLAction.ALTER)
			{
				if (dbObjectType == DBObjectType.FUNCTION || dbObjectType == DBObjectType.PROCEDURE || dbObjectType == DBObjectType.VIEW)
				{
					sb.setActionDDL(DDLAction.CREATE);
					addSqlBlock(getLocalMap(_dbCreateScripts, database, dbObjectType), sb);
				}
				else // ALTER TABLE
				{
					addSqlBlock(getLocalMap(_dbTableAlterScripts, database, dbObjectType), sb, false);
				}
			}
			else // DROP
			{
				addSqlBlock(getLocalMap(_dbDropScripts, database, dbObjectType), sb);
			}
		}
		else if (sb.getActionDML() != null)
		{
			addSqlBlock(getLocalMap(_dbDMLScripts, database, DBObjectType.TABLE), sb, false, false);
		}
		else if (sb.getActionSecurity() != null)
		{
			addSqlBlock(getLocalMap(_dbSecurityScripts, database, DBObjectType.PERMISSION), sb, false, false);
		}
	}

	/**
	 * @return map: database --> type (Table / View / etc) --> name --> object
	 */
	public HashMap<String, HashMap<DBObjectType, HashMap<String, SqlBlock>>> getScripts(ScriptType st)
	{
		switch (st)
		{
		case Create:
			return _dbCreateScripts;
		case Drop:
			return _dbDropScripts;
		case AlterTable:
			return _dbTableAlterScripts;
		case UpdateTable:
			return _dbDMLScripts;
		case Security:
			return _dbSecurityScripts;
		default:
			return null;
		}
	}

	public void writeSql(String outputDir) throws IOException {
		writeSql(outputDir, true);
	}

	public void writeSql(String outputDir, boolean sortSourceFiles) throws IOException
	{
		_sortSourceFiles = sortSourceFiles;
		for (ScriptType st : ScriptType.values())
		{
			String typedOutputDir = outputDir + "\\" + st.toString();
			HashMap<String, HashMap<DBObjectType, HashMap<String, SqlBlock>>> scripts = getScripts(st);
			writeScripts(scripts, typedOutputDir);
		}
	}

	//#region private functions
	private void addSqlBlock(HashMap<String, SqlBlock> sqlBlocks, SqlBlock sb)
	{
		addSqlBlock(sqlBlocks, sb, true, true);
	}

	private void addSqlBlock(HashMap<String, SqlBlock> sqlBlocks, SqlBlock sb, boolean overwrite)
	{
		addSqlBlock(sqlBlocks, sb, overwrite, true);
	}

	//#region private functions
	private void addSqlBlock(HashMap<String, SqlBlock> sqlBlocks, SqlBlock sb, boolean overwrite, boolean referenceByObjName)
	{
		_count++;

		if (sb.getSourceList().size() != 1)
			return;

		String objName = sb.getObjectName();
		String srcPath = sb.getSourceList().toArray()[0].toString();

		// Check reference
		SqlBlock oldSb = null;
		if (!referenceByObjName)
			oldSb = _dbDMLSourceMap.get(srcPath);
		else if (objName != null)
			oldSb = sqlBlocks.get(objName);

		if (oldSb == null)
		{
			sqlBlocks.put((objName != null) ? objName : Integer.toString(_count), sb);
			sb.setId(_count);
		}
		else
		{
			// oldSb.setId(_count); // Need this?
			oldSb.addSource(srcPath);

			if (overwrite)
				oldSb.replaceSqlText(sb.getSqlText(false));
			else
				oldSb.addSqlText(sb.getSqlText(false)); // Update SQL text
		}

		if (!referenceByObjName)
			_dbDMLSourceMap.put(srcPath, (oldSb == null) ? sb : oldSb);
	}

	private HashMap<String, SqlBlock> getLocalMap(HashMap<String, HashMap<DBObjectType, HashMap<String, SqlBlock>>> container, String database, DBObjectType dbObjectType)
	{
		return GenericUtil.getValueMap(GenericUtil.getValueMap(container, database), dbObjectType);
	}

	private void writeScripts(HashMap<String, HashMap<DBObjectType, HashMap<String, SqlBlock>>> scripts, String outputDir) throws IOException
	{
		Iterator<Map.Entry<String, HashMap<DBObjectType, HashMap<String, SqlBlock>>>> dbIter = scripts.entrySet().iterator();
		while (dbIter.hasNext())
		{
			Map.Entry<String, HashMap<DBObjectType, HashMap<String, SqlBlock>>> dbPair = dbIter.next();
			String database = dbPair.getKey();

			Iterator<Map.Entry<DBObjectType, HashMap<String, SqlBlock>>> objTypeIter = dbPair.getValue().entrySet().iterator();
			while (objTypeIter.hasNext())
			{
				Map.Entry<DBObjectType, HashMap<String, SqlBlock>> objTypePair = objTypeIter.next();
				DBObjectType objectType = objTypePair.getKey();
				String dbObjectDir = outputDir + "\\" + database + "\\" + objectType.toString();
				(new File(dbObjectDir)).mkdirs();

				Iterator<Map.Entry<String, SqlBlock>> objIter = objTypePair.getValue().entrySet().iterator();
				while (objIter.hasNext())
				{
					Map.Entry<String, SqlBlock> objPair = objIter.next();
					SqlBlock sb = objPair.getValue();

					String fileName = sb.getObjectName();
					if (fileName == null)
						fileName = formatSourceName(sb);

					String filePath = dbObjectDir + '\\' + fileName + ".sql";
					writeSqlBlock(sb, filePath, true);
				}
			}
		}
	}

	private String formatSourceName(SqlBlock sb)
	{
		StringBuilder buffer = new StringBuilder();
		String[] names = sb.getSourceList().toArray()[0].toString().split("\\\\");

		String patchVersion = names[names.length - 3];
		String fileName = names[names.length - 1];
		buffer.append(patchVersion.replace("Patch", "")); // patch version
		buffer.append('.');
		buffer.append(fileName.substring(0, fileName.lastIndexOf('.')));
		return buffer.toString();
	}

	private void writeSqlBlock(SqlBlock sb, String filePath, boolean formatBlock) throws IOException
	{
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8");
		writer.write(sb.getSqlText(formatBlock, _sortSourceFiles));
		writer.close();
	}

	//#endregion

	// database --> type (Table / View / etc) --> name --> object
	private HashMap<String, HashMap<DBObjectType, HashMap<String, SqlBlock>>> _dbCreateScripts;
	private HashMap<String, HashMap<DBObjectType, HashMap<String, SqlBlock>>> _dbDropScripts;
	private HashMap<String, HashMap<DBObjectType, HashMap<String, SqlBlock>>> _dbTableAlterScripts;

	private HashMap<String, HashMap<DBObjectType, HashMap<String, SqlBlock>>> _dbDMLScripts;
	private HashMap<String, HashMap<DBObjectType, HashMap<String, SqlBlock>>> _dbSecurityScripts;
	private HashMap<String, SqlBlock> _dbDMLSourceMap; // file --> Sql Blocks
	private int _count = 0;
	private boolean _sortSourceFiles = false;
}
