package org.zen.text;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexPro {
	public static boolean isEmptyLine(String line) {
		if (line.length() == 0)
			return true;

		return _ptnEmptyLine.matcher(line).find();
	}

	public static HashMap<String, String> getKeyValuePair(String line) {
		if (line.length() == 0)
			return null;

		HashMap<String, String> map = new HashMap<String, String>();
		Matcher m = _ptnKeyValuePair.matcher(line);
		while (m.find()) {
			int iLength = m.group().length();
			if (iLength > 1) {
				String x = m.group(1);
				String y = m.group(2);
			}

		}
		return map;
	}

	private static Pattern _ptnEmptyLine = Pattern.compile(RegexPatterns.EmptyLine);
	private static Pattern _ptnKeyValuePair = Pattern.compile(RegexPatterns.KeyValuePair);
}
