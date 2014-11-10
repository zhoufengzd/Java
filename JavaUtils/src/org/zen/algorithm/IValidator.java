package org.zen.algorithm;

import java.util.Collection;

public interface IValidator<E> {
	/**
	 * By default, let it pass
	 * 
	 * @param col
	 */
	default boolean isValid(Collection<E> col) {
		return true;
	}

	/**
	 * By default, let it pass
	 * 
	 * @param element
	 */
	default boolean isValid(E element) {
		return true;
	}
}
