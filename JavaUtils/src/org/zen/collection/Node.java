package org.zen.collection;

/**
 * 
 * <p>
 * It's a value wrapper with optional Id. If two nodes value equals, we treat them as equal.
 * 
 * @author Feng Zhou
 */
public class Node<E> {
	public Node() {
	}

	public Node(E data) {
		setData(data);
	}

	public E getData() {
		return _data;
	}

	public void setData(E data) {
		_data = data;
	}

	public int getNodeId() {
		return _nodeId;
	}

	public void setNodeId(int nodeId) {
		_nodeId = nodeId;
	}

	@Override
	public int hashCode() {
		return _data == null ? 0 : _data.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;

		@SuppressWarnings("unchecked")
		Node<E> objNd = (Node<E>) obj;
		return getData().equals(objNd.getData());
	}

	@Override
	public String toString() {
		return _data == null ? "" : _data.toString();
	}

	private E _data;
	private int _nodeId; // optional
}