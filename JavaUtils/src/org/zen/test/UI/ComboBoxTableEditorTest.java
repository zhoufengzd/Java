package org.zen.test.UI;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class ComboBoxTableEditorTest
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    public static void createAndShowGUI()
    {
		//  Build a table with data

		Object[][] data =
		{
			{"Color", "Red"},
			{"Fruit", "Banana"},
			{"Shape", "Square"},
		};
		String[] columnNames = {"Type","Value"};

		JTable table = new JTable(data, columnNames);
		table.setRowHeight(table.getRowHeight() + 6);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		JScrollPane scrollPane = new JScrollPane( table );

		//  Editor for first column

		String[] items = { "Color", "Fruit", "Shape" };
		DefaultCellEditor dce = new DefaultCellEditor(new JComboBox(items));
		table.getColumnModel().getColumn(0).setCellEditor(dce);

		//  Editor with multiple models for second column

		JComboBox comboBox = new JComboBox();
		comboBox.setRenderer( new PromptComboBoxRenderer("Select Value") );
		ComboBoxTableEditor editor = new ComboBoxTableEditor(comboBox, 0);
		table.getColumnModel().getColumn(1).setCellEditor( editor );

		String[] items1 = { "Red", "Blue", "Green" };
		editor.addModel("Color", items1);

		String[] items2 = { "Circle", "Square", "Triangle" };
		editor.addModel("Shape", items2);

		String[] items3 = { "Apple", "Orange", "Banana" };
		editor.addModel("Fruit", items3);

		//  Reset second column when first column changes
		//  (replaces TableModelListener)

		Action action = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				TableCellListener tcl = (TableCellListener)e.getSource();
   				int column = tcl.getColumn();

				if (column == 0)
				{
					int row = tcl.getRow();
					TableModel model = tcl.getTable().getModel();
	   				model.setValueAt(null, row, 1);
				}
	   		}
		};

		TableCellListener tcl = new TableCellListener(table, action);

		//  Button to display this source code

        JButton button = new JButton("Get The Test Code");
        button.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    JTextArea textArea = new JTextArea(10, 50);
                    JScrollPane scrollPane = new JScrollPane( textArea );

                    InputStream is = Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream( "ComboBoxTableEditorTest.java" );
                    InputStreamReader reader = new InputStreamReader(is);

                    BufferedReader br = new BufferedReader(reader);
                    textArea.read( br, null );
                    br.close();

                    Window window = SwingUtilities.windowForComponent((Component)e.getSource());
                    JDialog.setDefaultLookAndFeelDecorated(true);
                    JDialog dialog = new JDialog();
                    dialog.add(scrollPane);
                    dialog.pack();
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);
                }
                catch(Exception e2) { System.err.println(e2); }
            }
        });

		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("Combo Box Table Editor");
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.add( scrollPane );
		frame.add(button, BorderLayout.SOUTH);
		frame.setSize(300, 200);
		frame.setLocationRelativeTo( null );
		frame.setVisible(true);
    }
}
