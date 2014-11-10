package org.zen.collection;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import org.zen.common.def.Pair;

public class Trie<E extends Comparable<E>> implements Iterable<TrieNode<E>> {
	public Trie() {
		_root = new TrieNode<E>(null);
	}

	public Trie(Collection<Collection<E>> words) {
		_root = new TrieNode<E>(null);
		for (Collection<E> wd : words)
			doAddWord(wd);
	}

	public void setTraverseListener(TraverseListener<E> listener) {
		_traverseListener = listener;
	}

	public TrieNode<E> root() {
		return _root;
	}

	/**
	 * 
	 * @return maximum tree height. It's costly since it uses recursive calls.
	 */
	public int height() {
		return getHeight(_root);
	}

	public void addWord(Iterable<E> wd) {
		doAddWord(wd);
	}

	public boolean find(Collection<E> wd) {
		TrieNode<E> nd = _root;
		for (E c : wd) {
			TrieNode<E> child = nd.getChild(c);
			if (child == null)
				return false;

			nd = child;
		}

		return nd.isWord();
	}

	@Override
	public Iterator<TrieNode<E>> iterator() {
		return breadthFirstIterator();
	}

	/**
	 * Breadth-First: root --> left child, right child. Iterative traverse.
	 */
	public Iterator<TrieNode<E>> breadthFirstIterator() {
		List<TrieNode<E>> lst = traverseBreadthFirst();
		return lst.iterator();
	}

	/**
	 * Depth-First: root --> left child, right child. Iterative traverse.
	 */
	public Iterator<TrieNode<E>> depthFirstIterator() {
		List<TrieNode<E>> lst = traverseDepthFirst();
		return lst.iterator();
	}

	private List<TrieNode<E>> traverseBreadthFirst() {
		List<TrieNode<E>> lst = new ArrayList<TrieNode<E>>();
		Queue<Pair<TrieNode<E>, List<TrieNode<E>>>> workItems = new ArrayDeque<Pair<TrieNode<E>, List<TrieNode<E>>>>();
		workItems.add(new Pair<TrieNode<E>, List<TrieNode<E>>>(_root, EmptyPath));

		while (!workItems.isEmpty()) {
			Pair<TrieNode<E>, List<TrieNode<E>>> cur = workItems.poll();
			if (_traverseListener != null && !_traverseListener.isValid(cur.getFirst(), cur.getSecond()))
				break;

			TrieNode<E> nd = cur.getFirst();
			lst.add(nd);

			List<TrieNode<E>> parentPath = new ArrayList<TrieNode<E>>(cur.getSecond());
			parentPath.add(nd);

			for (TrieNode<E> child : nd.getChildren())
				workItems.add(new Pair<TrieNode<E>, List<TrieNode<E>>>(child, parentPath));
		}
		return lst;
	}

	private List<TrieNode<E>> traverseDepthFirst() {
		List<TrieNode<E>> lst = new ArrayList<TrieNode<E>>();

		Stack<Pair<TrieNode<E>, List<TrieNode<E>>>> workItems = new Stack<Pair<TrieNode<E>, List<TrieNode<E>>>>();
		workItems.push(new Pair<TrieNode<E>, List<TrieNode<E>>>(_root, EmptyPath));

		while (!workItems.isEmpty()) {
			Pair<TrieNode<E>, List<TrieNode<E>>> cur = workItems.pop();
			if (_traverseListener != null && !_traverseListener.isValid(cur.getFirst(), cur.getSecond()))
				break;

			TrieNode<E> nd = cur.getFirst();
			lst.add(nd);

			List<TrieNode<E>> parentPath = new ArrayList<TrieNode<E>>(cur.getSecond());
			parentPath.add(nd);

			for (TrieNode<E> child : nd.getChildren())
				workItems.push(new Pair<TrieNode<E>, List<TrieNode<E>>>(child, parentPath));
		}

		return lst;
	}

	private int getHeight(TrieNode<E> cur) {
		if (cur == null)
			return 0;

		int maxHeight = 0;
		int childHeight = 0;
		for (TrieNode<E> child : cur.getChildren()) {
			childHeight = getHeight(child) + 1;
			if (maxHeight < childHeight)
				maxHeight = childHeight;
		}

		return maxHeight;
	}

	private void doAddWord(Iterable<E> wd) {
		TrieNode<E> nd = _root;
		for (E c : wd) {
			TrieNode<E> child = nd.getChild(c);
			if (child == null) {
				child = nd.addChild(c);
			}

			nd = child;
		}

		nd.setWord(true);
	}

	private final List<TrieNode<E>> EmptyPath = new ArrayList<TrieNode<E>>();

	private TrieNode<E> _root;
	private TraverseListener<E> _traverseListener; // Optional traversal listener

}
