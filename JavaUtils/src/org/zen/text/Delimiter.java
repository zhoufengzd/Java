package org.zen.text;

import java.util.Iterator;

/**
 * Free style to format lines by delimiter. Allow null values in the collection.
 * 
 * @author Feng Zhou
 *
 */
public class Delimiter {
	public static <E> String toString(Iterable<E> collection) {
		return toString(collection.iterator(), ",", null);
	}

	public static <E> String toString(Iterable<E> collection, String delimiter) {
		return toString(collection.iterator(), delimiter, null);
	}

	public static <E> String toString(Iterator<E> it, String delimiter, String valueFormat) {
		if (it == null)
			return null;

		StringBuilder buffer = new StringBuilder();
		while (it.hasNext())
			appendElement(buffer, it.next(), delimiter, valueFormat);

		return buffer.toString();
	}

	public static <E> String toString(E[] collection) {
		return toString(collection, ",", null);
	}

	public static <E> String toString(E[] collection, String delimiter) {
		return toString(collection, delimiter, null);
	}

	public static <E> String toString(E[] collection, String delimiter, String valueFormat) {
		if (collection == null)
			return null;

		StringBuilder buffer = new StringBuilder();
		for (E element : collection)
			appendElement(buffer, element, delimiter, valueFormat);

		return buffer.toString();
	}

	private static <E> void appendElement(StringBuilder buffer, E element, String delimiter, String valueFormat) {
		if (buffer.length() > 0)
			buffer.append(delimiter);

		if (element != null) {
			String formatted = (valueFormat == null) ? element.toString() : String.format(valueFormat, element.toString());
			buffer.append(formatted);
		}
	}
}
