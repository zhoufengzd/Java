package org.zen.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

class LogonDlg extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1;
	private static int LABEL_WIDTH = 10;
	private static int TEXT_FIELD_WIDTH = 20;
	private static int BTN_HEIGHT = 10;

	public LogonDlg(String[] inputFields)
	{
		_inputFields = inputFields;
		initComponents();
	}

	public void actionPerformed(ActionEvent e)
	{
		JButton source = (JButton) e.getSource();
		if (source == _btnOK)
		{
			_confirmed = true;
		}
		else
		{
			_confirmed = false;
			_inputFieldMap.clear();
		}
		this.setVisible(false);
	}

	public boolean isConfirmed()
	{
		return _confirmed;
	}

	public String getInput(String fieldName)
	{
		JTextField fld = _inputFieldMap.get(fieldName);
		return fld == null ? null : fld.getText();
	}

	private void initComponents()
	{
		_inputFieldMap = new HashMap<String, JTextField>();

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(0, 2));
		int numPairs = _inputFields.length;
		for (int i = 0; i < numPairs; i++)
		{
			JLabel lbl = new JLabel(_inputFields[i] + ": ", JLabel.LEFT);
			lbl.setSize(LABEL_WIDTH, BTN_HEIGHT);
			JTextField textField = new JTextField();
			textField.setSize(TEXT_FIELD_WIDTH, BTN_HEIGHT);
			inputPanel.add(lbl);
			inputPanel.add(textField);

			_inputFieldMap.put(_inputFields[i], textField);
		}

		JPanel btnPanel = new JPanel(new FlowLayout());
		_btnOK = new JButton("OK");
		_btnOK.addActionListener(this);
		_btnCancel = new JButton("Cancel");
		_btnCancel.addActionListener(this);
		btnPanel.add(_btnOK);
		btnPanel.add(_btnCancel);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		mainPanel.add(new JPanel(), BorderLayout.NORTH);
		mainPanel.add(new JPanel(), BorderLayout.WEST);
		mainPanel.add(new JPanel(), BorderLayout.EAST);
		mainPanel.add(inputPanel, BorderLayout.CENTER);
		mainPanel.add(btnPanel, BorderLayout.SOUTH);

		setContentPane(mainPanel);
		setTitle("Connect to Server");
		setLocationRelativeTo(null); // Center window.

		pack(); // Layout components.
	}

	private JButton _btnOK;
	private JButton _btnCancel;
	private boolean _confirmed;
	private String[] _inputFields;	
	private HashMap<String, JTextField> _inputFieldMap;

}