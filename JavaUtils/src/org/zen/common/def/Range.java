package org.zen.common.def;

public class Range<T>
{
	public Range()
	{
	}

	public Range(T begin, T end)
	{
		setBegin(begin);
		setEnd(end);
	}

	public T getBegin() {
		return _begin;
	}

	public void setBegin(T begin) {
		_begin = begin;
	}

	public T getEnd() {
		return _end;
	}

	public void setEnd(T end) {
		_end = end;
	}

	private T _begin;
	private T _end;
}