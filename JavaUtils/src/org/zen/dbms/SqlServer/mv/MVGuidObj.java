package org.zen.dbms.SqlServer.mv;

import java.util.UUID;

/**
 * Holds name / guid pair. If return auto generated new UUID if it's not set
 *
 */
public class MVGuidObj {
	public MVGuidObj() {
	}
	public MVGuidObj(String name) {
		_name = name;
	}
	public MVGuidObj(String name, String strUID) {
		_name = name;
		_uid = strUID;
	}

	public String getName() {
		return _name;
	}
	public void setName(String name) {
		_name = name;
	}
	public String getUID() {
		if (_uid == null)
			_uid = UUID.randomUUID().toString().toUpperCase();
		return _uid;
	}
	public void setUID(String strUID) {
		_uid = strUID;
	}

	private String _name;
	private String _uid;
}
