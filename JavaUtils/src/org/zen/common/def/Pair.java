package org.zen.common.def;

public class Pair<T1, T2>
{
	public Pair()
	{
	}

	public Pair(T1 first, T2 second)
	{
		setFirst(first);
		setSecond(second);
	}

	public T1 getFirst() {
		return _first;
	}

	public void setFirst(T1 first) {
		_first = first;
	}

	public T2 getSecond() {
		return _second;
	}

	public void setSecond(T2 second) {
		_second = second;
	}

	private T1 _first;
	private T2 _second;
}