package org.zen.dbms.SqlServer.mv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.zen.text.Delimiter;
import org.zen.utils.FileProcessor;
import org.zen.utils.IOReaderBuilder;

public class IdssXmlSqlReader extends FileProcessor {
	public IdssXmlSqlReader() {
	}

	public void endProcess() {
		try {
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("c:\\IDSS_update.txt"));

			Iterator<Map.Entry<String, String>> it = _procActions.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> pair = it.next();
				writer.write(pair.getKey());
				writer.write(",");
				writer.write(pair.getValue());
			}

			writer.write("\r\n-------------------\r\n");
			writer.write(Delimiter.toString(_sourceTables, "\r\n"));
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void processFile(File inFile) throws IOException {
		String inFileName = inFile.getName();
		if (inFileName.toLowerCase().equals("quality.idss_buildxml.sql")) {
			readIdssBuildXml(inFile);
			return;
		}

		if (_procActions == null) {
			_procActions = new HashMap<String, String>();
			_sourceTables = new HashSet<String>();
		}

		readIdssUpdateSql(inFile, inFileName);
	}

	// Read Quality.IDSS_BuildXml.sql
	private void readIdssBuildXml(File inFile) throws IOException {
		BufferedReader br = new BufferedReader(IOReaderBuilder.buildInReader(inFile.getAbsolutePath()));
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("c:\\IDSS_BuildXml.txt"));

		// Read through each idss measure / mv sub measures block
		// IdssMeasure --> (N)MvMeasure --> (N)DataElement
		// DataElement --> (1)GetValueProcedure
		String procPrefix = "exec quality.";
		int iProcPrefixLength = procPrefix.length();

		StringBuilder measureMappingLines = new StringBuilder();
		String line, quotedName, currentIdssMeasure, currentMvMeasure, dtElement, dtProc;
		currentIdssMeasure = currentMvMeasure = dtElement = dtProc = null;
		int iIndexLeft, iIndexRight;

		while ((line = br.readLine()) != null) {
			line = line.trim().toLowerCase();
			if (line.indexOf("--") > -1 || line.indexOf("begin") > -1 || line.indexOf("end") > -1)
				continue;

			// Update current Idss measure name
			iIndexLeft = line.indexOf('\'', 0);
			iIndexRight = line.indexOf('\'', iIndexLeft + 1);

			quotedName = (iIndexLeft > -1 && iIndexRight > -1) ? line.substring(iIndexLeft + 1, iIndexRight) : null;

			if (line.indexOf("if (@measure ") > -1) { // idss measure block
				// write out block data
				if (measureMappingLines.length() > 0) {
					writer.write(measureMappingLines.toString());
					measureMappingLines = new StringBuilder();
				}

				currentIdssMeasure = quotedName.substring(0, 3);
			}
			else if (line.indexOf("set @mv_measure = ") > -1) {
				currentMvMeasure = quotedName;
			}
			else if (line.indexOf("exec quality.") > -1) {
				dtElement = quotedName;
				dtProc = line.substring(iProcPrefixLength, line.indexOf(' ', iProcPrefixLength));
				measureMappingLines.append(currentIdssMeasure);
				measureMappingLines.append(',');
				measureMappingLines.append(currentMvMeasure);
				measureMappingLines.append(',');
				measureMappingLines.append(dtElement);
				measureMappingLines.append(',');
				measureMappingLines.append(dtProc);

				// any age / sex stratification?
				// exec Quality.IDSS_update_subadded @measure, @mv_measure,
				// @product_line, @submissionID, 'subadded'
				// exec Quality.IDSS_UOS_update_mm @measure, @mv_measure,
				// @product_line, @submissionID, 'mmm09', 0, 9, 'M'
				if (dtElement != null && line.indexOf(',', iIndexRight + 1) > -1)
					measureMappingLines.append(",Y");
				else
					measureMappingLines.append(",N");

				measureMappingLines.append("\r\n");
			}
		}
		br.close();

		if (measureMappingLines.length() > 0) {
			writer.write(measureMappingLines.toString());
			measureMappingLines = null;
		}

		writer.close();
	}

	// select COUNT(distinct memid)
	// from IDSS.HEDIS_2012_Effectiveness_Of_Care
	private void readIdssUpdateSql(File inFile, String inFileName) throws IOException {
		BufferedReader br = new BufferedReader(IOReaderBuilder.buildInReader(inFile.getAbsolutePath()));
		@SuppressWarnings("unused")
		String createKwd = "create ", procedureKwd = "procedure ", functionKwd = "function ", selectKwd = "select ", fromKwd = "from ";

		StringBuilder measureMappingLines = new StringBuilder();
		HashSet<String> tables = new HashSet<String>();
		String line, sqlLogicName, actionPhase;
		sqlLogicName = actionPhase = null;
		int iIndexLeft, iIndexRight;

		boolean inDefinitionBlock = false;
		while ((line = br.readLine()) != null) {
			line = line.trim().toLowerCase();
			if (!inDefinitionBlock) {
				if (line.indexOf(createKwd) > -1 && sqlLogicName == null) {
					iIndexLeft = line.indexOf('.', createKwd.length());
					iIndexRight = line.indexOf(' ', iIndexLeft + 1);
					if (iIndexRight < 0)
						iIndexRight = line.length();

					sqlLogicName = line.substring(iIndexLeft + 1, iIndexRight);
				}
				if (line.indexOf("as") == 0 || line.indexOf("returns ") == 0) {
					inDefinitionBlock = true;
				}
				continue;
			}
			else {
				// select COUNT(distinct patientid) from idss.Sample_Patient
				if (actionPhase == null) {
					iIndexLeft = line.indexOf(selectKwd);
					if (iIndexLeft > -1) {
						iIndexRight = line.indexOf(fromKwd, iIndexLeft + 1);
						actionPhase = line.substring(selectKwd.length(), (iIndexRight > -1) ? iIndexRight : line.length());
					}
				}

				// Check from only when we know there is a 'select'
				// defined somewhere already
				if (actionPhase != null) {
					iIndexLeft = line.indexOf(fromKwd);
					if (iIndexLeft > -1) {
						iIndexLeft += fromKwd.length();
						iIndexRight = line.indexOf(' ', iIndexLeft);
						String tableName = line.substring(iIndexLeft, (iIndexRight > -1) ? iIndexRight : line.length());

						if (tableName.length() > 0 && tableName.indexOf("idss_submission") < 0) {
							_sourceTables.add(tableName);
							tables.add(tableName);
						}
					}
				}

			}
		}
		br.close();

		measureMappingLines.append(sqlLogicName);
		measureMappingLines.append(',');
		measureMappingLines.append(actionPhase);
		measureMappingLines.append(',');

		if (tables.isEmpty())
			measureMappingLines.append("null");
		else
			measureMappingLines.append(Delimiter.toString(tables, " && "));
		measureMappingLines.append("\r\n");
		_procActions.put(inFileName, measureMappingLines.toString());
	}

	private HashMap<String, String> _procActions;
	private HashSet<String> _sourceTables;
}