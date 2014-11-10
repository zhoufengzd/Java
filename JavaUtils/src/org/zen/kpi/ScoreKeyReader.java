package org.zen.kpi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.zen.utils.FileProcessor;
import org.zen.utils.FileWriter;
import org.zen.utils.IOReaderBuilder;

public class ScoreKeyReader extends FileProcessor {
	final static int MaxNumberofLinesCheck = 2;
	final static String CreateScoreKeyTableBegin = "create table Quality.Scorekey_";
	final static String CreateScoreKeyTableEnd = "\n)\ngo\n";

	public ScoreKeyReader() {
		_scoreKeyTablDDLMap = new HashMap<String, String>();
	}
	
	protected void processFile(File scoreKeyFile) throws IOException {
		String srcFilePath = scoreKeyFile.getAbsolutePath();
		BufferedReader br = new BufferedReader(IOReaderBuilder.buildInReader(srcFilePath));
		String[] lines = new String[MaxNumberofLinesCheck];

		String line = null;
		int lineCount = 0;
		while ((line = br.readLine()) != null) {
			if (line.length() < 1)
				continue;

			if (lineCount == MaxNumberofLinesCheck)
				break;

			lines[lineCount++] = line;
		}

		String[] columnNames = lines[0].split(",");
		int maxColumnNameLength = 0;
		for(String colName: columnNames) {
			if (maxColumnNameLength < colName.length())
				maxColumnNameLength = colName.length();
		}		
		
		String[] columnTypes = new String[columnNames.length];
		for (int i = 1; i < MaxNumberofLinesCheck; i++) {
			String[] colValues = lines[i].split(",");

			for (int j = 0; j < columnNames.length; j++)
				updateValueType(columnTypes, j, colValues[j]);
		}
		
		String scoreKeyName = scoreKeyFile.getName();
		StringBuilder buffer = new StringBuilder();
		buffer.append(CreateScoreKeyTableBegin);
		buffer.append(scoreKeyName);
		buffer.append(" (\n ");
		for (int j = 0; j < columnNames.length; j++) {
			if (j > 0)
				buffer.append(",\n");
			
			buffer.append("\t");
			buffer.append(columnNames[j]);
			
			int paddingLength = maxColumnNameLength - columnNames[j].length();
			for (int m = 0; m < paddingLength; m++)
				buffer.append(' ');
			
			buffer.append(' ');
			buffer.append(columnTypes[j]);
		}
		buffer.append(CreateScoreKeyTableEnd);
		
		String ddl = buffer.toString();
		_scoreKeyTablDDLMap.put(scoreKeyName, ddl);
		FileWriter.write(srcFilePath.replaceFirst("\\.csv", "\\.sql"), ddl);
	}

	private void updateValueType(String[] columnTypes, int colIndex, String strValue) {

		final String DecimalTypeFmt = "decimal(%d,%d)";
		final String IntTypeFmt = "int";
		final String VarcharTypeFmt = "varchar(%d)";
		
		int valueLength = strValue.length();
		if (valueLength < 1)
			return;
		
		if (columnTypes[colIndex] != null)
			return;
		
		String typeDefinition = null;
		
		// try decimal first
		int index = strValue.indexOf('.');
		if (index > 0) {
			try {
				Float.parseFloat(strValue);
				int mainLength = index + 1;
				int floatLength = strValue.length() - index - 1;
				typeDefinition = String.format(DecimalTypeFmt, mainLength, floatLength);
				
				columnTypes[colIndex] = typeDefinition;
				return;
			} 
			catch (NumberFormatException nfe) {
			}
		}
		
		// check integer
		try {
			Integer.parseInt(strValue);
			columnTypes[colIndex] = IntTypeFmt;
			return;
		} 
		catch (NumberFormatException nfe) {
		}
		
		typeDefinition = String.format(VarcharTypeFmt, valueLength);
		columnTypes[colIndex] = typeDefinition;
	}
	
	private HashMap<String, String> _scoreKeyTablDDLMap;
}
