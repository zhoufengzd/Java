package org.zen.collection;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TrieNode<E extends Comparable<E>> extends Node<E> {
	public TrieNode(E data) {
		super(data);
	}

	public Collection<TrieNode<E>> getChildren() {
		return _children;
	}

	public TrieNode<E> getChild(E childData) {
		return _childrenMap.get(childData);
	}

	public TrieNode<E> addChild(E childData) {
		TrieNode<E> child = _childrenMap.get(childData);
		if (child == null) {
			child = new TrieNode<E>(childData);
			addChild(child);
		}
		return child;
	}

	// Cautious: will override any existing child if any
	public TrieNode<E> addChild(TrieNode<E> child) {
		_children.add(child);
		_childrenMap.put(child.getData(), child);

		return child;
	}

	public TrieNode<E> removeChild(E childData) {
		TrieNode<E> child = _childrenMap.get(childData);
		if (child != null)
			return removeChild(child);

		return null;
	}

	public TrieNode<E> removeChild(TrieNode<E> child) {
		_children.remove(child);
		return _childrenMap.remove(child.getData());
	}

	public void clear() {
		_children.clear();
		_childrenMap.clear();
	}

	public boolean isWord() {
		return _isWord;
	}

	public void setWord(boolean isWord) {
		_isWord = isWord;
	}

	private Set<TrieNode<E>> _children = new HashSet<TrieNode<E>>();
	private Map<E, TrieNode<E>> _childrenMap = new HashMap<E, TrieNode<E>>();

	private boolean _isWord = false;
}
