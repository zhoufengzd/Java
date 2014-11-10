package org.zen.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * @author My version of LinkedHashSet before I know about it or available.<>
 */
public class UnsortedSet<itemT> implements Set<itemT>
{
	public UnsortedSet()
	{
		_list = new ArrayList<itemT>();
		_set = new HashSet<itemT>();
	}

	public int size()
	{
		return _list.size();
	}

	public boolean isEmpty()
	{
		return _list.isEmpty();
	}

	public boolean contains(Object element)
	{
		return _set.contains(element);
	}

	public boolean add(itemT element)
	{
		if (_set.contains(element))
			return false;

		_set.add(element);
		_list.add(element);
		return true;
	}

	public boolean remove(Object element)
	{
		if (!_set.contains(element))
			return false;

		_set.remove(element);
		_list.remove(element);
		return true;
	}

	public Iterator<itemT> iterator()
	{
		return _list.iterator();
	}

	public boolean containsAll(Collection<?> c)
	{
		return _set.containsAll(c);
	}

	public boolean addAll(Collection<? extends itemT> c)
	{
		boolean modified = false;
		for (itemT item : c)
		{
			if (add(item))
				modified = true;
		}

		return modified;
	}

	public boolean removeAll(Collection<?> c)
	{
		boolean modified = false;
		for (Object item : c)
		{
			if (remove(item))
				modified = true;
		}

		return modified;
	}

	@SuppressWarnings("unchecked")
	public boolean retainAll(Collection<?> c)
	{
		_list.clear();

		boolean modified = false;
		for (Object item : c)
		{
			_list.add((itemT) item);
			if (!contains(item))
			{
				modified = true;
				_set.remove(item);
			}
		}

		return modified;
	}

	public void clear()
	{
		_list.clear();
		_set.clear();
	}

	public Object[] toArray()
	{
		return _list.toArray();
	}

	public <T> T[] toArray(T[] a)
	{
		return _list.toArray(a);
	}

	private ArrayList<itemT> _list;
	private HashSet<itemT> _set;

}