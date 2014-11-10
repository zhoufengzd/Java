package org.zen.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.zen.common.def.Pair;

/**
 * <p>
 * Record traverse paths between root and nodes queried or all paths.
 * 
 * @author Feng Zhou
 */
public class TraverseListener<E> {
	public TraverseListener() {
		_pathList = new ArrayList<Pair<Node<E>, List<Node<E>>>>();
	}

	public TraverseListener(Collection<E> queryTargets) {
		_tmpTargets = new ArrayList<E>(queryTargets);
		_pathList = new ArrayList<Pair<Node<E>, List<Node<E>>>>();
	}

	public List<Pair<Node<E>, List<Node<E>>>> getPathList() {
		return _pathList;
	}

	public boolean isValid(Node<E> node, List<? extends Node<E>> parentPath) {
		@SuppressWarnings("unchecked")
		List<Node<E>> path = (List<Node<E>>) parentPath;

		// Record all if no targets specified
		if (_tmpTargets == null) {
			_pathList.add(new Pair<Node<E>, List<Node<E>>>(node, path));
			return true;
		}

		// Record target node paths
		E ndData = node.getData();
		List<E> nodesFound = new ArrayList<E>();
		for (E nd : _tmpTargets) {
			if (ndData.equals(nd)) {
				nodesFound.add(nd);
				_pathList.add(new Pair<Node<E>, List<Node<E>>>(node, path));
			}
		}

		for (E nd : nodesFound)
			_tmpTargets.remove(nd);

		// abort the iteration if all nodes queried were found
		return (_tmpTargets.size() > 0);
	}

	private List<Pair<Node<E>, List<Node<E>>>> _pathList; // node --> parent path
	private List<E> _tmpTargets;
}