package org.zen.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.zen.utils.GenericUtil;

/**
 * 
 * @author English word list
 */
public class Dictionary {
	private static Dictionary _gbDictionary = new Dictionary(
			"D:\\Projects\\Java\\JavaUtils\\src\\org\\zen\\data\\en_GB.dictionary");
	private static Dictionary _usDictionary = new Dictionary(
			"D:\\Projects\\Java\\JavaUtils\\src\\org\\zen\\data\\en_US.dictionary");

	private Dictionary() {
	}

	private Dictionary(String filePath) {
		_filePath = filePath;
	}

	public static Dictionary getGBDictionary() {
		return _gbDictionary;
	}

	public static Dictionary getUSDictionary() {
		return _usDictionary;
	}

	public Set<String> getWordList() throws IOException {
		checkDictionary();
		return _wordList;
	}

	public Map<Integer, Set<String>> getSizeMap() throws IOException {
		checkDictionary();
		return _sizeMap;
	}

	private void checkDictionary() throws IOException
	{
		if (_sizeMap != null)
			return;

		_wordList = new HashSet<String>();

		BufferedReader br = new BufferedReader(new FileReader(_filePath));
		String line;
		while ((line = br.readLine()) != null) {
			if (line.indexOf('\'', 0) < 0)
				_wordList.add(line);
		}
		br.close();

		_sizeMap = new HashMap<Integer, Set<String>>();
		for (String wd : _wordList) {
			Set<String> s = GenericUtil.getValueSet(_sizeMap, wd.length());
			s.add(wd);
		}
	}

	private String _filePath;
	private Set<String> _wordList;
	private Map<Integer, Set<String>> _sizeMap;
}
