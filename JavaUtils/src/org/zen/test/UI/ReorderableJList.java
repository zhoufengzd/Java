package org.zen.test.UI;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.*;

@SuppressWarnings("serial")
public class ReorderableJList extends JList implements DragSourceListener, DropTargetListener, DragGestureListener
{
	static DataFlavor LocalObjectFlavor;
	static
	{
		try
		{
			LocalObjectFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	static DataFlavor[] SupportedFlavors = { LocalObjectFlavor };

	public ReorderableJList()
	{
		super();
		setCellRenderer(new ReorderableListCellRenderer());
		setModel(new DefaultListModel());
		_dragSource = new DragSource();
		_dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
		_dropTarget = new DropTarget(this, this);
	}

	public ReorderableJList(Object[] listData)
	{
		super(listData);
		setCellRenderer(new ReorderableListCellRenderer());
		setModel(new DefaultListModel());
		_dragSource = new DragSource();
		DragGestureRecognizer dgr = _dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
		_dropTarget = new DropTarget(this, this);
		
//		JList list = new ReorderableJList();
//		DefaultListModel defModel = new DefaultListModel();
//		list.setModel(defModel);
//		String[] listItems = { "Chris", "Joshua", "Daniel", "Michael", "Don", "Kimi", "Kelly", "Keagan" };
//		Iterator it = Arrays.asList(listItems).iterator();
//		while (it.hasNext())
//			defModel.addElement(it.next());
				
	}
	
	// DragGestureListener
	public void dragGestureRecognized(DragGestureEvent dge)
	{
		System.out.println("dragGestureRecognized");
		// find object at this x,y
		Point clickPoint = dge.getDragOrigin();
		int index = locationToIndex(clickPoint);
		if (index == -1)
			return;
		Object target = getModel().getElementAt(index);
		Transferable trans = new RJLTransferable(target);
		_draggedIndex = index;
		_dragSource.startDrag(dge, Cursor.getDefaultCursor(), trans, this);
	}

	// DragSourceListener events
	public void dragDropEnd(DragSourceDropEvent dsde)
	{
		System.out.println("dragDropEnd()");
		_dropTargetCell = null;
		_draggedIndex = -1;
		repaint();
	}

	public void dragEnter(DragSourceDragEvent dsde)
	{
	}

	public void dragExit(DragSourceEvent dse)
	{
	}

	public void dragOver(DragSourceDragEvent dsde)
	{
	}

	public void dropActionChanged(DragSourceDragEvent dsde)
	{
	}

	// DropTargetListener events
	public void dragEnter(DropTargetDragEvent dtde)
	{
		System.out.println("dragEnter");
		if (dtde.getSource() != _dropTarget)
		{
			dtde.rejectDrag();
		}
		else
		{
			dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
			System.out.println("accepted dragEnter");
		}
	}

	public void dragExit(DropTargetEvent dte)
	{
	}

	// dragOver() listed below
	public void dragOver(DropTargetDragEvent dtde)
	{
		// figure out which cell it's over, no drag to self
		if (dtde.getSource() != _dropTarget)
			dtde.rejectDrag();
		
		Point dragPoint = dtde.getLocation();
		int index = locationToIndex(dragPoint);
		if (index == -1)
			_dropTargetCell = null;
		else
			_dropTargetCell = getModel().getElementAt(index);
		repaint();
	}

	// drop() listed below
	public void dropActionChanged(DropTargetDragEvent dtde)
	{
	}

	// main() method to test - listed below

	// RJLTransferable listing below
	class RJLTransferable implements Transferable
	{
		Object object;

		public RJLTransferable(Object o)
		{
			object = o;
		}

		public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException
		{
			if (isDataFlavorSupported(df))
				return object;
			else
				throw new UnsupportedFlavorException(df);
		}

		public boolean isDataFlavorSupported(DataFlavor df)
		{
			return (df.equals(LocalObjectFlavor));
		}

		public DataFlavor[] getTransferDataFlavors()
		{
			return SupportedFlavors;
		}
	}

	// ReorderableListCellRendering listing below
	@SuppressWarnings("serial")
	class ReorderableListCellRenderer extends DefaultListCellRenderer
	{
		boolean isTargetCell;
		boolean isLastItem;

		public ReorderableListCellRenderer()
		{
			super();
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus)
		{
			isTargetCell = (value == _dropTargetCell);
			isLastItem = (index == list.getModel().getSize() - 1);
			boolean showSelected = isSelected & (_dropTargetCell == null);
			return super.getListCellRendererComponent(list, value, index, showSelected, hasFocus);
		}

		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			if (isTargetCell)
			{
				g.setColor(Color.black);
				g.drawLine(0, 0, getSize().width, 0);
			}
		}
	}

	public void drop(DropTargetDropEvent dtde)
	{
		System.out.println("drop()!");
		if (dtde.getSource() != _dropTarget)
		{
			System.out.println("rejecting for bad source (" + dtde.getSource().getClass().getName() + ")");
			dtde.rejectDrop();
			return;
		}
		Point dropPoint = dtde.getLocation();
		int index = locationToIndex(dropPoint);
		System.out.println("drop index is " + index);
		boolean dropped = false;
		try
		{
			if ((index == -1) || (index == _draggedIndex))
			{
				System.out.println("dropped onto self");
				dtde.rejectDrop();
				return;
			}
			dtde.acceptDrop(DnDConstants.ACTION_MOVE);
			System.out.println("accepted");
			Object dragged = dtde.getTransferable().getTransferData(LocalObjectFlavor);
			// move items - note that indicies for insert will
			// change if [removed] source was before target
			System.out.println("drop " + _draggedIndex + " to " + index);
			boolean sourceBeforeTarget = (_draggedIndex < index);
			System.out.println("source is" + (sourceBeforeTarget ? "" : " not") + " before target");
			System.out.println("insert at " + (sourceBeforeTarget ? index - 1 : index));
			DefaultListModel mod = (DefaultListModel) getModel();
			mod.remove(_draggedIndex);
			mod.add((sourceBeforeTarget ? index - 1 : index), dragged);
			dropped = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		dtde.dropComplete(dropped);
	}

	private DragSource _dragSource;
	private DropTarget _dropTarget;
	private Object _dropTargetCell;
	private int _draggedIndex = -1;
	
	public static void main(String[] args)
	{
//		JList list = new ReorderableJList();
//		DefaultListModel defModel = new DefaultListModel();
//		list.setModel(defModel);
		String[] listItems = { "Chris", "Joshua", "Daniel", "Michael", "Don", "Kimi", "Kelly", "Keagan" };
//		Iterator it = Arrays.asList(listItems).iterator();
//		while (it.hasNext())
//			defModel.addElement(it.next());
		
		JList list = new ReorderableJList(listItems);
		
		// show list
		JScrollPane scroller = new JScrollPane(list, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JFrame frame = new JFrame("Checkbox JList");
		frame.getContentPane().add(scroller);
		frame.pack();
		frame.setVisible(true);
	}
}