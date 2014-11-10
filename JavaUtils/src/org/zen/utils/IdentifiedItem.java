package org.zen.utils;

public class IdentifiedItem<Identifier, Item>
{
	public IdentifiedItem()
	{		
	}
	public IdentifiedItem(Identifier key, Item item)
	{
		_key = key;
		setValue(item);
	}
	
	public void setKey(Identifier key)
	{
		_key = key;
	}
	public Identifier getKey()
	{
		return _key;
	}

	public void setValue(Item value)
	{
		_value = value;
	}
	public Item getValue()
	{
		return _value;
	}

	private Identifier _key;
	private Item _value;
}
