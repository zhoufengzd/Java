package org.zen.common.def;

public class LeftRight<T>
{
	public LeftRight()
	{
	}

	public LeftRight(T left, T right)
	{
		setLeft(left);
		setRight(right);
	}

	public T getLeft() {
		return _left;
	}

	public void setLeft(T left) {
		_left = left;
	}

	public T getRight() {
		return _right;
	}

	public void setRight(T right) {
		_right = right;
	}

	private T _left;
	private T _right;
}