package org.zen.process;

import java.util.Collection;
import java.util.Iterator;

import org.zen.text.Delimiter;

public class SysLogger {
	public static <E> void println(Iterable<E> collection) {
		println(collection, ",");
	}

	public static <E> void println(Iterable<E> collection, String delimiter) {
		System.out.println(Delimiter.toString(collection, delimiter));
	}

	public static <E> void println(Iterator<E> it, String delimiter) {
		System.out.println(Delimiter.toString(it, delimiter, null));
	}

	public static <E> void printlns(Collection<Collection<E>> collections) {
		printlns(collections, ",");
	}

	public static <E> void printlns(Collection<Collection<E>> collections, String delimiter) {
		for (Collection<E> col : collections)
			println(col, delimiter);
	}
}
