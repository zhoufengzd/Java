package org.zen.dbms.SqlServer.mv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import org.zen.utils.FileProcessor;
import org.zen.utils.FileWriter;
import org.zen.utils.IOReaderBuilder;
import org.zen.utils.ObjSerializer;
import org.zen.utils.XmlLineReader;

// Read Create Table blocks from sql files, either in procedure or in table DDL file, and split out the pure table block
public class MeasureXmlReader extends FileProcessor {
	final String HedisYearPrefix = "HEDIS_2014_";
	final int HedisYearPrefixLength = HedisYearPrefix.length();

	final String MesureUIDPrefix = "Measure_UID=\"";
	final int MesureUIDPrefixLength = MesureUIDPrefix.length();

	final int UIDLength = (new String("AEE9677D-0FE6-4EF9-B4BC-85BCE07A223C")).length();
	final String[] ICD10Measures = { "ABA", "ASM1", "CBP", "CDC1", "CDC3", "CMC1", "HPV", "LBP", "PBH", "URI", "W150", };

	final int MVMeasureLineNumber = 2;
	final String ICD10_Prefix = "ICD10_";

	final String MeasureUidLog = "measureUid.ser";
	final String MeasureVersionUidLog = "measureVersionUid.ser";
	final String ServiceUidLog = "serviceUid.ser";
	final String ServiceVersionUidLog = "serviceVersionUid.ser";

	final String MeasureNamesLog = "MeasureNamesLog.ser";
	final String ServiceNamesLog = "ServiceNamesLog.ser";
	final String MeasureUidMapLog = "MeasureUidMapLog.ser";
	final String ServiceUidMapLog = "ServiceUidMapLog.ser";

	public MeasureXmlReader(String serviceListFile) {
		loadIcdServiceNames(serviceListFile);
		prepareMeasureSvcMaps();
	}

	protected void processFile(File inFile) {
		try {

			String inFileName = inFile.getName();
			int versionCharIndex = inFileName.lastIndexOf("_v");
			String oldMeasureName = inFileName.substring(0, versionCharIndex);

			BufferedReader br = new BufferedReader(IOReaderBuilder.buildInReader(inFile.getAbsolutePath()));

			String filePathOld = inFile.getParent() + "_log\\" + inFileName;
			String filePathNew = inFile.getParent() + "_log\\ICD10_" + oldMeasureName + "_v1_0.xml";
			StringBuilder buffer = new StringBuilder();

			String line = null, lineTrimmed = null, updatedLine = null;
			int lineNumber = 0;

			XmlLineReader attributeReader = new XmlLineReader();
			HashMap<String, String> attributes = null;
			String svcName = null;
			MVGuidObj oldMeasure = null, newMeasure = null, oldSvc = null, newSvc = null;
			MVVersionObj oldMV = null, newMV = null, oldSvcV = null, newSvcV = null;

			while ((line = br.readLine()) != null) {
				++lineNumber;

				updatedLine = line;
				lineTrimmed = line.trim();

				if (lineTrimmed.isEmpty())
					continue;

				if (lineNumber == MVMeasureLineNumber) {
					// <MvMeasure Is_System="0" Is_Composite="0" Owner="MV" Name="HEDIS_2014_ABA" 
					//	Number_Id="HEDIS_2014_ABA" Measure_UID="AEE9677D-0FE6-4EF9-B4BC-85BCE07A223C" Source="REGULAR">
					attributes = attributeReader.readAttributes(lineTrimmed);
					oldMeasure = new MVGuidObj(attributes.get("Name"), attributes.get("Measure_UID"));
					_measureNames.add(oldMeasure.getName());

					newMeasure = new MVGuidObj(ICD10_Prefix + oldMeasure.getName(), _measureUidLog.get(oldMeasure.getName()));
					_measureUidLog.put(oldMeasure.getName(), newMeasure.getUID());
					_measureUidMap.put(oldMeasure.getUID(), newMeasure.getUID());
					assert (oldMeasureName.equals(oldMeasure.getName()));

					updatedLine = line.replace(oldMeasure.getName(), newMeasure.getName()).replace(oldMeasure.getUID(), newMeasure.getUID());
				}
				else if (lineTrimmed.indexOf("<MvMeasureVersion ") == 0) {
					// <MvMeasureVersion Is_System="0" Major_Version="1" Minor_Version="23" Data_Duration="null" 
					//		Clinical_Rationale="This measure implements ..." Sequence_Number="null" Is_Enabled="1" 
					//		Measure_Version_UID="DD178644-0E00-42B7-A939-69BFF02BB51B" Measure_Type_Id="1">
					attributes = attributeReader.readAttributes(lineTrimmed);
					oldMV = new MVVersionObj(Integer.parseInt(attributes.get("Major_Version")), Integer.parseInt(attributes.get("Minor_Version")), attributes.get("Measure_Version_UID"));
					newMV = new MVVersionObj(1, 0, _measureVersionUidLog.get(oldMeasure.getName()));
					_measureVersionUidLog.put(oldMeasure.getName(), newMV.getUID());

					// input is not well formatted, so do the dumb string replace to make it like the old line
					updatedLine = line.replace(oldMV.getMajorVersionString(), newMV.getMajorVersionString()).replace(oldMV.getMinorVersionString(), newMV.getMinorVersionString()).replace(oldMV.getUID(), newMV.getUID());
				}
				else if (lineTrimmed.indexOf("<MvMeasureSpecification ") == 0) {
					// MvMeasureSpecification Is_System="0" Technical_Specification="&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; 
					// replace service name, service uid
					updatedLine = line.replace(oldMeasure.getName(), newMeasure.getName());
					for (String svc : _icdServicesOld.keySet()) {
						oldSvc = _icdServicesOld.get(svc);
						newSvc = _icdServicesNew.get(svc);

						updatedLine = updatedLine.replace(oldSvc.getName(), newSvc.getName()).replace(oldSvc.getUID(), newSvc.getUID());
					}

					for (String oldMeasureUid : _measureUidMap.keySet())
						updatedLine = updatedLine.replace(oldMeasureUid, _measureUidMap.get(oldMeasureUid));

					for (String oldSvcUid : _serviceUidMap.keySet())
						updatedLine = updatedLine.replace(oldSvcUid, _serviceUidMap.get(oldSvcUid));
				}
				else if (lineTrimmed.indexOf("<MvMeasure ") == 0) {
					updatedLine = line.replace(oldMeasure.getName(), newMeasure.getName()).replace(oldMeasure.getUID(), newMeasure.getUID());
				}
				else if (lineTrimmed.indexOf("<MvServiceVersion ") == 0) {
					//<MvServiceVersion Is_System="0" Major_Version="1" Minor_Version="0" Effective_Date="" Key_Words="HEDIS_2014_BMI_ICD9CM" 
					//	Description="HEDIS_2014_BMI_ICD9CM" 
					//	Design_Rationale="null" Service_Version_UID="AFDBE6F6-2E67-4AB3-AD0E-6C09022E8573">
					attributes = attributeReader.readAttributes(lineTrimmed);
					svcName = attributes.get("Key_Words");
					// if (svcName.indexOf("ICD9") > -1 || _globalIcdServiceList.contains(svcName)) 
					{
						oldSvcV = _icdServiceVersionsOld.get(svcName);
						if (oldSvcV == null) {
							oldSvcV = new MVVersionObj(Integer.parseInt(attributes.get("Major_Version")), Integer.parseInt(attributes.get("Minor_Version")), attributes.get("Service_Version_UID"));
							_icdServiceVersionsOld.put(svcName, oldSvcV);
						}
						newSvcV = _icdServiceVersionsNew.get(svcName);
						if (newSvcV == null) {
							newSvcV = new MVVersionObj(1, 0);
							_icdServiceVersionsNew.put(svcName, newSvcV);
						}

						oldSvc = _icdServicesOld.get(svcName);
						if (oldSvc == null) {
							oldSvc = new MVGuidObj(svcName, null);
							_icdServicesOld.put(svcName, oldSvc);
						}
						newSvc = _icdServicesNew.get(svcName);
						if (newSvc == null) {
							newSvc = new MVGuidObj(ICD10_Prefix + svcName, null);
							_icdServicesNew.put(svcName, newSvc);
						}
						_serviceNames.add(svcName);

						updatedLine = line.replace(oldSvcV.getMajorVersionString(), newSvcV.getMajorVersionString()).replace(oldSvcV.getMinorVersionString(), newSvcV.getMinorVersionString()).replace(oldSvcV.getUID(), newSvcV.getUID());
						updatedLine = updatedLine.replace(oldSvc.getName(), newSvc.getName());
					}
				}
				else if (lineTrimmed.indexOf("<MvService ") == 0) {
					//<MvService Is_System="0" Owner="MV" Name="HEDIS_2014_BMI_ICD9CM" Is_Enabled="1" Is_Composite="0" 
					// Service_UID="1894B449-205F-4578-9055-99944D3267B2"/>
					attributes = attributeReader.readAttributes(lineTrimmed);
					svcName = attributes.get("Name");

					// Replace all services
					///if (_globalIcdServiceList.contains(svcName)) {
					{
						oldSvc = _icdServicesOld.get(svcName);
						if (oldSvc == null) {
							oldSvc = new MVGuidObj(svcName, null);
							_icdServicesOld.put(svcName, oldSvc);
						}
						oldSvc.setUID(attributes.get("Service_UID"));
						newSvc = _icdServicesNew.get(svcName);
						if (newSvc == null) {
							newSvc = new MVGuidObj(ICD10_Prefix + svcName, null);
							_icdServicesNew.put(svcName, newSvc);
						}

						_serviceNames.add(svcName);
						_serviceUidMap.put(oldSvc.getUID(), newSvc.getUID());
						updatedLine = line.replace(oldSvc.getName(), newSvc.getName()).replace(oldSvc.getUID(), newSvc.getUID());
					}
				}
				//<MvMeasureImport Name="CDC Denominator" Description="Denominator Member" ExportMeasureUID="85C65479-718F-4FEC-9F6E-BFC9353C4980"/>
				else if (lineTrimmed.indexOf("<MvMeasureImport ") == 0) {
					for (String oldMeasureUid : _measureUidMap.keySet())
						updatedLine = updatedLine.replace(oldMeasureUid, _measureUidMap.get(oldMeasureUid));
				}
				else if (lineTrimmed.indexOf("<MvPath Value=\"Code Tables") == 0) {
					updatedLine = line.replace("<MvPath Value=\"Code Tables", "<MvPath Value=\"Code Tables\\ICD10Test");
				}
				else if (lineTrimmed.indexOf("<MvPath Value=\"Measures\\") == 0) {
					if (lineTrimmed.indexOf("<MvPath Value=\"Measures\\ICD10Test\\\"/>") == 0)
						continue;
					// updatedLine = line.replace("<MvPath Value=\"Measures\\", "<MvPath Value=\"Measures\\ICD10Test\\");
					updatedLine = "            <MvPath Value=\"Measures\\ICD10Test\\\"/>";
				}

				buffer.append(updatedLine);
				buffer.append('\n');
			}

			br.close();

			FileWriter.write(filePathNew, buffer.toString(), "Cp1252", false);
			FileWriter.write(filePathOld, buffer.toString(), "Cp1252", false);

			writeUidLog();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadIcdServiceNames(String serviceListFile) {
		_globalIcdServiceList = new HashSet<String>();

		try {
			Scanner sc = new Scanner(new File(serviceListFile));
			while (sc.hasNextLine()) {
				String svcName = sc.nextLine();
				if (!svcName.isEmpty())
					_globalIcdServiceList.add(svcName);
			}
			sc.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void writeUidLog() throws IOException {
		for (String svcName : _icdServicesNew.keySet())
			_svcUidLog.put(svcName, _icdServicesNew.get(svcName).getUID());

		for (String svcName : _icdServiceVersionsNew.keySet())
			_svcVersionUidLog.put(svcName, _icdServiceVersionsNew.get(svcName).getUID());

		ObjSerializer.write(_logRootDir + "\\" + MeasureUidLog, _measureUidLog);
		ObjSerializer.write(_logRootDir + "\\" + MeasureVersionUidLog, _measureVersionUidLog);
		ObjSerializer.write(_logRootDir + "\\" + MeasureUidMapLog, _measureUidMap);
		ObjSerializer.write(_logRootDir + "\\" + ServiceUidLog, _svcUidLog);
		ObjSerializer.write(_logRootDir + "\\" + ServiceVersionUidLog, _svcVersionUidLog);
		ObjSerializer.write(_logRootDir + "\\" + ServiceUidMapLog, _serviceUidMap);

		ObjSerializer.write(_logRootDir + "\\" + MeasureNamesLog, _measureNames);
		ObjSerializer.write(_logRootDir + "\\" + ServiceNamesLog, _serviceNames);
	}

	private void prepareMeasureSvcMaps() {
		try {
			_icdServicesOld = new HashMap<String, MVGuidObj>();
			_icdServicesNew = new HashMap<String, MVGuidObj>();
			_icdServiceVersionsOld = new HashMap<String, MVVersionObj>();
			_icdServiceVersionsNew = new HashMap<String, MVVersionObj>();

			_measureUidLog = ObjSerializer.read(_logRootDir + "\\" + MeasureUidLog);
			if (_measureUidLog == null)
				_measureUidLog = new HashMap<String, String>();

			_measureVersionUidLog = ObjSerializer.read(_logRootDir + "\\" + MeasureVersionUidLog);
			if (_measureVersionUidLog == null)
				_measureVersionUidLog = new HashMap<String, String>();

			_measureUidMap = ObjSerializer.read(_logRootDir + "\\" + MeasureUidMapLog);
			if (_measureUidMap == null)
				_measureUidMap = new HashMap<String, String>();

			_svcUidLog = ObjSerializer.read(_logRootDir + "\\" + ServiceUidLog);
			if (_svcUidLog == null)
				_svcUidLog = new HashMap<String, String>();
			for (String svcName : _svcUidLog.keySet())
				_icdServicesNew.put(svcName, new MVGuidObj(ICD10_Prefix + svcName, _svcUidLog.get(svcName)));

			_svcVersionUidLog = ObjSerializer.read(_logRootDir + "\\" + ServiceVersionUidLog);
			if (_svcVersionUidLog == null)
				_svcVersionUidLog = new HashMap<String, String>();
			for (String svcName : _svcVersionUidLog.keySet())
				_icdServiceVersionsNew.put(svcName, new MVVersionObj(1, 0, _svcVersionUidLog.get(svcName)));

			_serviceUidMap = ObjSerializer.read(_logRootDir + "\\" + ServiceUidMapLog);
			if (_serviceUidMap == null)
				_serviceUidMap = new HashMap<String, String>();

			//
			_measureNames = ObjSerializer.read(_logRootDir + "\\" + MeasureNamesLog);
			if (_measureNames == null)
				_measureNames = new HashSet<String>();
			_serviceNames = ObjSerializer.read(_logRootDir + "\\" + ServiceNamesLog);
			if (_serviceNames == null)
				_serviceNames = new HashSet<String>();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Note: In maps below, all measure names / service names are original names without 'ICD10_' prefix
	private HashSet<String> _globalIcdServiceList;
	private HashMap<String, MVGuidObj> _icdServicesOld; // service name --> service UID (old)
	private HashMap<String, MVGuidObj> _icdServicesNew; // service name --> service UID (new, generated, or retrieved from log)
	private HashMap<String, MVVersionObj> _icdServiceVersionsOld; // service name --> service version UID (old)
	private HashMap<String, MVVersionObj> _icdServiceVersionsNew; // service name --> service version UID (generated)

	// Java serialization does not support write object directly, so converted to string map instead.
	private String _logRootDir = "D:\\SourceCode\\4.1.0\\HSE_FZ\\Tests\\ICD_Converter\\Working\\UidLog";
	private HashMap<String, String> _measureUidLog; // measure name --> measure UID (generated)
	private HashMap<String, String> _measureVersionUidLog; // measure name --> measure version UID (generated)
	private HashMap<String, String> _measureUidMap; // old measure UID --> new measure UID
	private HashMap<String, String> _svcUidLog; // service name --> service UID (generated)
	private HashMap<String, String> _svcVersionUidLog; // service name --> service version UID (generated)
	private HashMap<String, String> _serviceUidMap; // old service UID --> new service UID

	private HashSet<String> _measureNames;
	private HashSet<String> _serviceNames;
}
