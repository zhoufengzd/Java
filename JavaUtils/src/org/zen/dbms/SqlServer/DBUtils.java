package org.zen.dbms.SqlServer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBUtils {

	@SuppressWarnings("unchecked")
	public static <T> ArrayList<T> GetColumnValue(ResultSet rs, int columnId) throws SQLException
	{
		if (rs == null)
			return null;

		ArrayList<T> result = new ArrayList<T>();
		while (rs.next())
			result.add((T) rs.getObject(columnId));

		return result;

	}

	public static ArrayList<String> GetTableCreationOrder(SqlSvrDataSource ds) throws SQLException
	{
		final String SqlText = "EXEC dbo.ListTblCreationOrder";
		return ds.executeList(SqlText);
	}
}
