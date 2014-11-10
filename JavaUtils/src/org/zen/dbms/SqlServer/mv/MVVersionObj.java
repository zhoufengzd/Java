package org.zen.dbms.SqlServer.mv;

public class MVVersionObj extends MVGuidObj {
	public MVVersionObj() {
	}
	public MVVersionObj(int majorVersion, int minorVersion) {
		_majorVersion = majorVersion;
		_minorVersion = minorVersion;
	}
	public MVVersionObj(int majorVersion, int minorVersion, String strUID) {
		_majorVersion = majorVersion;
		_minorVersion = minorVersion;
		setUID(strUID);
	}

	public int getMajorVersion() {
		return _majorVersion;
	}
	public String getMajorVersionString() {
		return String.format("Major_Version=\"%d\"", _majorVersion);
	}
	public void setMajorVersion(int majorVersion) {
		_majorVersion = majorVersion;
	}
	public int getMinorVersion() {
		return _minorVersion;
	}
	public String getMinorVersionString() {
		return String.format("Minor_Version=\"%d\"", _minorVersion);
	}
	public void setMinorVersion(int minorVersion) {
		_minorVersion = minorVersion;
	}

	public MVGuidObj getReferenceObj() {
		return _referenceObj;
	}
	public void setReferenceObj(MVGuidObj refObj) {
		_referenceObj = refObj;
	}

	private int _majorVersion;
	private int _minorVersion;

	private MVGuidObj _referenceObj;
}
