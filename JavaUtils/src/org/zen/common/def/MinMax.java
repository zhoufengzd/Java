package org.zen.common.def;

public class MinMax<T>
{
	public MinMax()
	{
	}

	public MinMax(T min, T max)
	{
		setMin(min);
		setMax(max);
	}

	public T getMin() {
		return _min;
	}

	public void setMin(T min) {
		_min = min;
	}

	public T getMax() {
		return _max;
	}

	public void setMax(T max) {
		_max = max;
	}

	private T _min;
	private T _max;
}