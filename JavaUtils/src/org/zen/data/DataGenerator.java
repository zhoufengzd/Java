package org.zen.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Random data generator
 */
public class DataGenerator {
	private Random _rdm = new Random(System.currentTimeMillis());

	public int[] getArray(int size, int min, int max) {
		int[] dt = new int[size];
		for (int j = 0; j < size; j++) {
			dt[j] = _rdm.nextInt(max - min + 1) + min;
		}
		return dt;
	}

	public List<Integer> getList(int size, int min, int max) {
		ArrayList<Integer> dt = new ArrayList<Integer>(size);
		for (int j = 0; j < size; j++) {
			dt.add(_rdm.nextInt(max - min + 1) + min);
		}
		return dt;
	}

	public Set<Integer> getSet(int size, int min, int max) {
		Set<Integer> dt = new HashSet<Integer>(size);
		while (dt.size() < size) {
			dt.add(_rdm.nextInt(max - min + 1) + min);
		}
		return dt;
	}

	public List<Character> getCharList(int size) {
		ArrayList<Character> dt = new ArrayList<Character>(size);
		int min = 'a', max = 'z';
		for (int j = 0; j < size; j++) {
			char c = (char) (_rdm.nextInt(max - min + 1) + min);
			dt.add(c);
		}
		return dt;
	}

	public Set<Character> getCharSet(int size) {
		Set<Character> dt = new HashSet<Character>(size);
		int min = 'a', max = 'z';
		while (dt.size() < size) {
			char c = (char) (_rdm.nextInt(max - min + 1) + min);
			dt.add(c);
		}
		return dt;
	}
}
