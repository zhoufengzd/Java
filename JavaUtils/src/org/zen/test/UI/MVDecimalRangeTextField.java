package org.zen.test.UI;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * 
 * @author ichan
 *
 * This Class test the input value of a text field if it is between a range that provided.
 * Pass in min = 0 and max = 0 if there is no range.
 */

public class MVDecimalRangeTextField extends JTextField implements
		FocusListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected double m_Max = 0;
	protected double m_Min = 0;
	protected int width = 5;
	protected boolean allowNegative = true;
	protected boolean forceWhole = false;
	protected Dimension dim = new Dimension(40,20);
	
	protected static final String NON_DECIMAL_ERR = "Input value must be a decimal.";
	
	protected static final String NON_WHOLE_NUMBER_ERR = "Input value must be a whole number.";
	
	protected static final String OUT_OF_RANGE_ERR = "Input value out of range. Range: ";
	
	protected static final String NEGATIVE_ERR = "Input value must be a positive number";
	
	protected static final String PANEL_TITLE = "Decimal text field";
	
	public MVDecimalRangeTextField() 
	{
		this.setPreferredSize(dim);
		this.setText("0");
		this.addFocusListener(this);
	}
	public MVDecimalRangeTextField(String defaultVal) 
	{
		this();
		this.setText(defaultVal);
	}
	public MVDecimalRangeTextField( double min, double max) 
	{
		this();
		m_Min = min;
		m_Max = max;
	}
	public MVDecimalRangeTextField( String defaultVal, double min, double max) 
	{
		this(defaultVal);		
		m_Min = min;
		m_Max = max;
	}
	
	public void setMin(double min)
	{
		m_Min = min;	
	}
	public double getMin()
	{
		return m_Min ;	
	}
	public void setMax(double max)
	{
		m_Max = max;	
	}
	public double getMax()
	{
		return m_Max ;	
	}
	public void setAllowNegative(boolean isAllow)
	{
		allowNegative = isAllow;
	}
	public boolean getAllowNegative()
	{
		return allowNegative ;
	}
	public void setForceWhole(boolean isWhole)
	{
		forceWhole = isWhole;
	}
	public boolean getForceWhole()
	{
		return forceWhole ;
	}

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent e) {
		
		JTextField field = (JTextField) e.getComponent();
		String inputText = field.getText();
		double value = 0;
		try
		{
			value = Double.parseDouble(inputText);
			//if min and max are both 0, means it accept any decimal.
			
			if(m_Min != 0 || m_Max != 0)
			{
				
				if((value < m_Min || value > m_Max) && value != 0)
				{
					throw new Exception(OUT_OF_RANGE_ERR+m_Min+" to "+m_Max);
					
//					field.setText("0");
//					JOptionPane.showMessageDialog(field,
//							OUT_OF_RANGE_ERR+m_Min+" to "+m_Max,
//							PANEL_TITLE,
//		                     JOptionPane.ERROR_MESSAGE);
//					 
//					 
//					 field.requestFocus(); 
					 
				}
				
			}
			if(!allowNegative)
			{
				if(m_Min < 0 || m_Max < 0 || value < 0)
					throw new Exception(NEGATIVE_ERR);
			}
			if(forceWhole)
			{
				Double value1 = (Double)value;
				Double value2 = (Double)Math.floor(value + 0.5d);
				if (value1.doubleValue() != value2.doubleValue())
					throw new Exception(NON_WHOLE_NUMBER_ERR);
				else
					field.setText(String.valueOf(Math.round(value)));
			}
		}
		catch (Exception ex )
		{
			 field.setText("0");
			 JOptionPane.showMessageDialog(field,
//					 NON_DECIMAL_ERR,
					 ex.getMessage(),
					 PANEL_TITLE,
                     JOptionPane.ERROR_MESSAGE);
			 
			 field.requestFocus();
		}		
		
		
	}

}
