package org.zen.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GenericUtil {
	public static <TKey, TValueKey, TValue> HashMap<TValueKey, TValue> getValueMap(
			HashMap<TKey, HashMap<TValueKey, TValue>> map, TKey tk) {
		HashMap<TValueKey, TValue> tv = map.get(tk);
		if (tv == null) {
			tv = new HashMap<TValueKey, TValue>();
			map.put(tk, tv);
		}
		return tv;
	}

	public static <TKey, TValue> List<TValue> getValueList(HashMap<TKey, List<TValue>> map, TKey tk) {
		List<TValue> tv = map.get(tk);
		if (tv == null) {
			tv = new ArrayList<TValue>();
			map.put(tk, tv);
		}
		return tv;
	}

	public static <TKey, TValue> Set<TValue> getValueSet(Map<TKey, Set<TValue>> map, TKey tk) {
		Set<TValue> tv = map.get(tk);
		if (tv == null) {
			tv = new HashSet<TValue>();
			map.put(tk, tv);
		}
		return tv;
	}
}
