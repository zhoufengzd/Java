package org.zen.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.zen.common.def.Pair;

public class TreeHelper<E extends Comparable<E>> {
	public List<Pair<Node<E>, List<Node<E>>>> getPath(Tree<E> tree) {
		TraverseListener<E> pq = new TraverseListener<E>();
		tree.setTraverseListener(pq);
		tree.breadthFirstIterator();
		return pq.getPathList();
	}

	public List<Pair<Node<E>, List<Node<E>>>> getPath(Tree<E> tree, Collection<E> nodeDataSet) {
		TraverseListener<E> pq = new TraverseListener<E>(nodeDataSet);
		tree.setTraverseListener(pq);
		tree.breadthFirstIterator();
		return pq.getPathList();
	}

	public List<Pair<Node<E>, List<Node<E>>>> getPath(Trie<E> tree) {
		TraverseListener<E> pq = new TraverseListener<E>();
		tree.setTraverseListener(pq);
		tree.iterator();
		return pq.getPathList();
	}

	public boolean isCousins(Tree<E> tree, E x, E y) {
		List<E> nodeSet = Arrays.asList(x, y);

		List<Pair<Node<E>, List<Node<E>>>> pathList = getPath(tree, nodeSet);
		if (pathList.size() != 2) // Check to see if both nodes are on the tree
			return false;

		List<Node<E>> srcPath = pathList.get(0).getSecond(), tgtPath = pathList.get(1).getSecond();
		int parentNodeIndex = srcPath.size() - 1;
		if (srcPath == null || tgtPath == null)
			return false;

		if (srcPath.size() != tgtPath.size())
			return false;

		return !srcPath.get(parentNodeIndex).equals(tgtPath.get(parentNodeIndex));
	}
}
