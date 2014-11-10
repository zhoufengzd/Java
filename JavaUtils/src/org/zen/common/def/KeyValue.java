package org.zen.common.def;

public class KeyValue<T1, T2>
{
	public KeyValue()
	{
	}

	public KeyValue(T1 key, T2 value)
	{
		setKey(key);
		setValue(value);
	}

	public T1 getKey() {
		return _key;
	}

	public void setKey(T1 key) {
		_key = key;
	}

	public T2 getValue() {
		return _value;
	}

	public void setValue(T2 value) {
		_value = value;
	}

	private T1 _key;
	private T2 _value;
}