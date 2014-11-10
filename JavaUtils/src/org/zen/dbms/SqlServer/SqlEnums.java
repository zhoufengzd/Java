package org.zen.dbms.SqlServer;

public class SqlEnums
{
	public enum SecurityAction
	{
		// note: sequence is important if multiple words are present
		GRANT, REVOKE, DENY
	}
	
	public enum DDLAction
	{
		// note: sequence is important if multiple words are present
		ALTER, CREATE, DROP,
	}

	public enum DMLAction
	{
		// note: sequence is important if multiple words are present
		INSERT, DELETE, UPDATE, EXEC, SELECT, TRUNCATE,
	}

	public enum MiscAction
	{
		// note: sequence is important if multiple words are present
		PRINT, SET, 
	}
	
	public enum DBObjectType
	{
		UNKNOWN, TABLE, FUNCTION, PROCEDURE, VIEW, SCHEMA, ROLE, INDEX, PARTITION, PERMISSION
	}	
}
