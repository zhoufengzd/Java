package org.zen.collection;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import org.zen.common.def.Pair;

/**
 * A generic binary tree class with limited API.
 * <p>
 * It's not intended to replace java standard collection but to show the basic concept.
 * <p>
 * It provides inOrder, preOrder, and postOrder iterator and breadthFirst iterator.
 * <p>
 * Default iterator is in-order
 * 
 * @author Feng Zhou
 * @date 10/2/2014
 *
 */
public class Tree<E> implements Iterable<TreeNode<E>> {
	public Tree() {
	}

	public Tree(TreeNode<E> root) {
		_root = root;
	}

	/**
	 * Build balanced tree from data. If the data is sorted, the result is BST.
	 * 
	 * @param data
	 *            .
	 */
	public Tree(Collection<E> data) {
		List<E> lst = new ArrayList<E>(data);
		_root = buildTree(lst, 0, lst.size() - 1);
	}

	public void setTraverseListener(TraverseListener<E> listener) {
		_traverseListener = listener;
	}

	public TreeNode<E> root() {
		return _root;
	}

	public int height() {
		return getHeight(_root);
	}

	@Override
	public Iterator<TreeNode<E>> iterator() {
		return inOrderIterator();
	}

	/**
	 * In-Order: left child -> root -> right child.
	 */
	public Iterator<TreeNode<E>> inOrderIterator() {
		List<TreeNode<E>> lst = new ArrayList<TreeNode<E>>();
		traverse(_root, EmptyPath, lst, TraverseMode.InOrder);
		return lst.iterator();
	}

	/**
	 * Pre-Order: root -> left child -> right child.
	 */
	public Iterator<TreeNode<E>> preOrderIterator() {
		List<TreeNode<E>> lst = new ArrayList<TreeNode<E>>();
		traverse(_root, EmptyPath, lst, TraverseMode.PreOrder);
		return lst.iterator();
	}

	/**
	 * Post-Order: left child -> right child -> root.
	 */
	public Iterator<TreeNode<E>> postOrderIterator() {
		List<TreeNode<E>> lst = new ArrayList<TreeNode<E>>();
		traverse(_root, EmptyPath, lst, TraverseMode.PostOrder);
		return lst.iterator();
	}

	/**
	 * Breadth-First: root --> left child, right child. Iterative traverse.
	 */
	public Iterator<TreeNode<E>> breadthFirstIterator() {
		List<TreeNode<E>> lst = traverseBreadthFirst();
		return lst.iterator();
	}

	/**
	 * Depth-First: root --> left child, right child. Iterative traverse.
	 */
	public Iterator<TreeNode<E>> depthFirstIterator() {
		List<TreeNode<E>> lst = traverseDepthFirst();
		return lst.iterator();
	}

	/**
	 * 
	 * @param data
	 * @param startIndex
	 * @param end
	 *            : last index to use in the array
	 * @return
	 */
	private TreeNode<E> buildTree(List<E> dt, int startIndex, int endIndex) {
		if (startIndex > endIndex)
			return null;

		int mid = (startIndex + endIndex) / 2;
		// System.out.printf("start = %d, end = %d, mid= %d, value=%s\n", startIndex, endIndex, mid, dt.get(mid));
		TreeNode<E> root = new TreeNode<E>(dt.get(mid));
		root.setLeftChild(buildTree(dt, startIndex, mid - 1));
		root.setRightChild(buildTree(dt, mid + 1, endIndex));

		return root;
	}

	private enum TraverseMode {
		InOrder, PreOrder, PostOrder, BreadthFirst
	}

	private void traverse(TreeNode<E> cur, List<TreeNode<E>> parentPath, List<TreeNode<E>> nodesVisited, TraverseMode mode) {
		if (cur == null)
			return;

		if (_traverseListener != null && !_traverseListener.isValid(cur, parentPath))
			return;

		List<TreeNode<E>> nextParentPath = new ArrayList<TreeNode<E>>(parentPath);
		nextParentPath.add(cur);
		if (mode == TraverseMode.InOrder) {
			traverse(cur.getLeftChild(), nextParentPath, nodesVisited, mode);
			nodesVisited.add(cur);
			traverse(cur.getRightChild(), nextParentPath, nodesVisited, mode);
		}
		else if (mode == TraverseMode.PreOrder) {
			nodesVisited.add(cur);
			traverse(cur.getLeftChild(), nextParentPath, nodesVisited, mode);
			traverse(cur.getRightChild(), nextParentPath, nodesVisited, mode);
		}
		else if (mode == TraverseMode.PostOrder) {
			traverse(cur.getLeftChild(), nextParentPath, nodesVisited, mode);
			traverse(cur.getRightChild(), nextParentPath, nodesVisited, mode);
			nodesVisited.add(cur);
		}
	}

	private List<TreeNode<E>> traverseBreadthFirst() {
		List<TreeNode<E>> lst = new ArrayList<TreeNode<E>>();

		Queue<Pair<TreeNode<E>, List<TreeNode<E>>>> workItems = new ArrayDeque<Pair<TreeNode<E>, List<TreeNode<E>>>>();
		workItems.add(new Pair<TreeNode<E>, List<TreeNode<E>>>(_root, EmptyPath));

		while (!workItems.isEmpty()) {
			Pair<TreeNode<E>, List<TreeNode<E>>> cur = workItems.poll();
			if (_traverseListener != null && !_traverseListener.isValid(cur.getFirst(), cur.getSecond()))
				break;

			TreeNode<E> nd = cur.getFirst();
			lst.add(nd);

			List<TreeNode<E>> parentPath = new ArrayList<TreeNode<E>>(cur.getSecond());
			parentPath.add(nd);

			TreeNode<E> left = nd.getLeftChild();
			TreeNode<E> right = nd.getRightChild();
			if (left != null)
				workItems.add(new Pair<TreeNode<E>, List<TreeNode<E>>>(left, parentPath));
			if (right != null)
				workItems.add(new Pair<TreeNode<E>, List<TreeNode<E>>>(right, parentPath));
		}

		return lst;
	}

	private List<TreeNode<E>> traverseDepthFirst() {
		List<TreeNode<E>> lst = new ArrayList<TreeNode<E>>();

		Stack<Pair<TreeNode<E>, List<TreeNode<E>>>> workItems = new Stack<Pair<TreeNode<E>, List<TreeNode<E>>>>();
		workItems.push(new Pair<TreeNode<E>, List<TreeNode<E>>>(_root, EmptyPath));

		while (!workItems.isEmpty()) {
			Pair<TreeNode<E>, List<TreeNode<E>>> cur = workItems.pop();
			if (_traverseListener != null && !_traverseListener.isValid(cur.getFirst(), cur.getSecond()))
				break;

			TreeNode<E> nd = cur.getFirst();
			lst.add(nd);

			List<TreeNode<E>> parentPath = new ArrayList<TreeNode<E>>(cur.getSecond());
			parentPath.add(nd);

			// Push right child first, so left child will be first to pick up.
			// This will simulate In-Order.
			TreeNode<E> left = nd.getLeftChild();
			TreeNode<E> right = nd.getRightChild();
			if (right != null)
				workItems.push(new Pair<TreeNode<E>, List<TreeNode<E>>>(right, parentPath));
			if (left != null)
				workItems.push(new Pair<TreeNode<E>, List<TreeNode<E>>>(left, parentPath));
		}

		return lst;
	}

	private int getHeight(TreeNode<E> cur) {
		if (cur == null)
			return 0;

		int leftHeight = getHeight(cur.getLeftChild());
		int rightHeight = getHeight(cur.getRightChild());

		return ((leftHeight < rightHeight) ? rightHeight : leftHeight) + 1;
	}

	private final List<TreeNode<E>> EmptyPath = new ArrayList<TreeNode<E>>();

	private TreeNode<E> _root;
	private TraverseListener<E> _traverseListener; // Optional traversal listener
}
