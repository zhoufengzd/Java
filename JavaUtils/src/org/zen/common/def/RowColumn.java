package org.zen.common.def;

public class RowColumn<T>
{
	public RowColumn()
	{
	}

	public RowColumn(T row, T column)
	{
		setRow(row);
		setColumn(column);
	}

	public T getRow() {
		return _row;
	}

	public void setRow(T row) {
		_row = row;
	}

	public T getColumn() {
		return _column;
	}

	public void setColumn(T column) {
		_column = column;
	}

	private T _row;
	private T _column;
}