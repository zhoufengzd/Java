package org.zen.dbms.SqlServer;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.zen.dbms.SqlServer.SqlEnums.*;
import org.zen.utils.IdentifiedItem;

public interface LineChecker
{
	boolean IsMatched(SqlBlock sb, String line);
}

class SecurityMatcher implements LineChecker 
{
	public SecurityMatcher()
	{
		_patternsSecurity = new ArrayList<IdentifiedItem<SecurityAction,Pattern>>();
		
		for (SecurityAction action : SecurityAction.values())
		{
			_patternsSecurity.add(new IdentifiedItem<SecurityAction,Pattern>(
					action,
					Pattern.compile(action.toString() + "\\s+(\\S+)", Pattern.CASE_INSENSITIVE
							| Pattern.UNICODE_CASE)));
		}		
	}
	
	public boolean IsMatched(SqlBlock sb, String line)
	{
		for(IdentifiedItem<SecurityAction, Pattern> pair: _patternsSecurity)
		{
			Pattern ptn = pair.getValue();
			Matcher m = ptn.matcher(line);
			
			if (m.find())
			{
				sb.setActionSecurity(pair.getKey());
				return true;
			}
		}

		return false;
	}
	
	private ArrayList<IdentifiedItem<SecurityAction, Pattern>> _patternsSecurity;	
}

class DDLMatcher implements LineChecker 
{
	public DDLMatcher()
	{
		_patternsDDL = new ArrayList<IdentifiedItem<DDLAction,Pattern>>();
		
		String identifier = "(\\S+)\\s*";
		for (DDLAction action : DDLAction.values())
		{
			_patternsDDL.add(new IdentifiedItem<DDLAction,Pattern>(
					action,
					Pattern.compile(action.toString() + "\\s+(UNIQUE\\s+)?(CLUSTERED\\s+)?(NONCLUSTERED\\s+)?(\\w+)\\s+" + identifier, Pattern.CASE_INSENSITIVE
							| Pattern.UNICODE_CASE)));
		}	
	}
	
	public boolean IsMatched(SqlBlock sb, String line)
	{
		for(IdentifiedItem<DDLAction, Pattern> pair: _patternsDDL)
		{
			Pattern ptn = pair.getValue();
			Matcher m = ptn.matcher(line);
			
			if (m.find())
			{
				sb.setActionDDL(pair.getKey());
				try
				{
					sb.setObjectType(DBObjectType.valueOf(m.group(4).toUpperCase()));
				}
				catch(java.lang.IllegalArgumentException ex)
				{
					sb.setObjectType(DBObjectType.UNKNOWN);
				}

				String dbObjectName = m.group(5);
				if (dbObjectName == null)
				{
					sb.setObjectName("UNKNOWN");
				}
				else if (dbObjectName.indexOf('#') > -1) // put temporary object DDL as DML
				{
					sb.setActionDDL(null);
					sb.setActionDML(DMLAction.UPDATE);
				}
				else
				{
					sb.setObjectName(dbObjectName);				
				}
				
				return true;
			}
		}

		return false;
	}
	
	private ArrayList<IdentifiedItem<DDLAction, Pattern>> _patternsDDL;
}

class DMLMatcher implements LineChecker 
{
	public DMLMatcher()
	{
		_patternsDML = new ArrayList<IdentifiedItem<DMLAction,Pattern>>();

		for (DMLAction action : DMLAction.values())
		{
			_patternsDML.add(new IdentifiedItem<DMLAction,Pattern>(
					action,
					Pattern.compile(action.toString() + "\\s*", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)));
		}	}
	
	public boolean IsMatched(SqlBlock sb, String line)
	{
		for(IdentifiedItem<DMLAction, Pattern> pair: _patternsDML)
		{
			Pattern ptn = pair.getValue();
			Matcher m = ptn.matcher(line);
			
			if (m.find())
			{
				sb.setActionDML(pair.getKey());
				sb.setObjectType(DBObjectType.TABLE);
				return true;
			}
		}

		return false;
	}
	
	private ArrayList<IdentifiedItem<DMLAction, Pattern>> _patternsDML;	
}

class MiscMatcher implements LineChecker 
{
	public MiscMatcher()
	{
		_ptnSetOptions = Pattern.compile("^\\s*SET\\s+\\w+\\s+[ON|OFF]", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		_ptnPrint = Pattern.compile("(^\\s*)(PRINT\\s+\\w+)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	}	
	public boolean IsMatched(SqlBlock sb, String line)
	{
		if (_ptnSetOptions.matcher(line).find())
		{
			sb.setActionMisc(MiscAction.SET);
			return true;
		}

		if (_ptnPrint.matcher(line).find())
		{
			sb.setActionMisc(MiscAction.PRINT);
			return true;
		}

		return false;
	}
	
	private Pattern _ptnSetOptions;
	private Pattern _ptnPrint;
}

class IfMatcher implements LineChecker 
{
	public IfMatcher()
	{
		Pattern ptnIfExistsLine = Pattern.compile("^\\s*(IF\\s+)(NOT\\s+)?(EXISTS\\s*)(\\()?([^\\(\\)])+(\\))?", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Pattern ptnIfISLine = Pattern.compile("^\\s*(IF\\s+)(\\S+\\s+)(IS\\s+)(NOT\\s+)?(NULL\\s+)(EXECUTE|EXEC\\s+\\(?'?)?", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		
		_patterns = new ArrayList<Pattern>();
		_patterns.add(ptnIfExistsLine);
		_patterns.add(ptnIfISLine);
	}

	public boolean IsMatched(SqlBlock sb, String line)
	{
		_actionPhrase = null;
		for(Pattern ptn: _patterns)
		{
			Matcher m = ptn.matcher(line);		
			if (m.find())
			{
				_actionPhrase = line.substring(m.group().length());
				return true;
			}
		}

		return false;
	}
	
	public String getActionPhrase()
	{
		return _actionPhrase;
	}
	
	private ArrayList<Pattern> _patterns;
	private String _actionPhrase;
}