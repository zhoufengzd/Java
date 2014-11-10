package org.zen.common.def;

public class Point<T>
{
	public Point()
	{
	}

	public Point(T x, T y)
	{
		setX(x);
		setY(y);
	}

	public T getX() {
		return _x;
	}

	public void setX(T x) {
		_x = x;
	}

	public T getY() {
		return _y;
	}

	public void setY(T y) {
		_y = y;
	}

	private T _x;
	private T _y;
}