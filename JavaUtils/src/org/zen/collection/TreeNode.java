package org.zen.collection;

/**
 * Binary tree node.
 * 
 * @author Feng Zhou
 *
 * @param <E>
 */
public class TreeNode<E> extends Node<E> {

	public TreeNode(E data) {
		super(data);
	}

	public TreeNode<E> getLeftChild() {
		return _leftChild;
	}

	public void setLeftChild(TreeNode<E> leftChild) {
		_leftChild = leftChild;
	}

	public TreeNode<E> getRightChild() {
		return _rightChild;
	}

	public void setRightChild(TreeNode<E> rightChild) {
		_rightChild = rightChild;
	}

	private TreeNode<E> _leftChild;
	private TreeNode<E> _rightChild;
}
