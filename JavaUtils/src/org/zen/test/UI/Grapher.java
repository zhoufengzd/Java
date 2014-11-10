package org.zen.test.UI;

import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Color;

//no deprecation here, 2 different methods of action listening

public class Grapher extends Applet implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6117904759088764177L;
	
	TextField textA = new TextField("            ");
	TextField textB = new TextField("            ");
	TextField textC = new TextField("            ");
	Label labelY = new Label(" y = ");
	Label labelA = new Label(" x^2 + ");
	Label labelB = new Label(" x + ");
	Label labelAns = new Label("     ");
	Button mybutton = new Button("Draw");
	// ****************************
	// global graphing variables
	double A, B, C;

	// ****************************

	public void init() {
		textA.setText("1");
		textB.setText("1");
		textC.setText("1");
		add(labelY);
		add(textA);
		add(labelA);
		add(textB);
		add(labelB);
		add(textC);
		add(mybutton);
		mybutton.addActionListener(this);
		// repaint();
	}

	public void paint(Graphics screen) {
		setSize(500, 500);
		// *****************************
		screen.setColor(Color.blue);
		screen.drawRect(100, 100, 300, 300); // bounds of rectangle
		// *****************************
		// draws the xy chart
		screen.setColor(Color.black);
		screen.drawLine(250, 100, 250, 400); // y axis
		screen.drawLine(100, 250, 400, 250); // x axis

		// *****************************
		// screen.drawLine(245,100,255, 100); //come up with a few lines and
		// look
		// screen.drawLine(245,115,255, 115); //at the pattern
		// screen.drawLine(245,130,255, 130);
		for (int i = 0; i <= 300; i = i + 15) // tick marks on y axis
		{
			screen.drawLine(245, 100 + i, 255, 100 + i);
		}
		// ****************************
		// labels x and y axis
		screen.drawString("Y", 248, 95);
		screen.drawString("X", 405, 254);
		// ******************************
		int j = 10; // labels y coordinates
		String jbacktoString;

		for (int i = 0; i <= 150; i = i + 15) // does upper half y
		{
			jbacktoString = Integer.toString(j);
			// screen.drawLine(245,100+i,255,100+i );
			if (j != 0 && j != 10)
				screen.drawString(jbacktoString, 262, 104 + i);
			j = j - 1;
		}
		j = 0;
		for (int i = 150; i <= 300; i = i + 15) // does lower half y
		{
			jbacktoString = Integer.toString(j);
			// screen.drawLine(245,100+i,255,100+i );
			if (j != 0 && j != 10)
				screen.drawString(jbacktoString, 262, 104 + i);
			j = j + 1;
		}

		// ******************************

		// screen.drawLine(100,245,100,255);
		// screen.drawLine(115,245,115,255);
		// screen.drawLine(130,245,130,255);
		for (int i = 0; i <= 300; i = i + 15) // tick marks on x axis
		{
			screen.drawLine(100 + i, 245, 100 + i, 255);
		}
		// *****************************
		j = 10; // labels x coordinates

		// String jbacktoString;
		for (int i = 0; i <= 150; i = i + 15) // does left axis
		{
			jbacktoString = Integer.toString(j);
			// screen.drawLine(100+i,245,100+i,255);
			if (j != 0 && j != 10 && j != -10)
				screen.drawString(jbacktoString, 97 + i, 269);
			j = j - 1;
		}
		j = 0;
		for (int i = 150; i <= 300; i = i + 15) // does right axis
		{
			jbacktoString = Integer.toString(j);
			// screen.drawLine(100+i,245,100+i,255);
			if (j != 0 && j != 10 && j != -10)
				screen.drawString(jbacktoString, 97 + i, 269);
			j = j + 1;
		}
		// *****************************
		// area to draw graph
		screen.setColor(Color.black); // graph is red
		// screen.drawLine(100,100,300,300);
		// double x,y, x1, y1, x2,y2, oldx1, oldy1;
		double x, y, transX, transY, oldTransX, oldTransY;
		// System.out.println(y);
		transX = 0;
		transY = 0;
		oldTransX = 0;
		oldTransY = 0;

		{
			// changed line
			for (x = -10; x <= 10; x = x + .01)// x=x+.01 controls incrementing
			{

				// if(oldTransX != 0) //needed to stop red on x axis

				// screen.setColor(new Color(255,0,0)); //sets this only if
				// button pushed
				y = A * x * x + B * x + C;
				System.out.println("x=" + x + " y=" + y);// proves correctly
															// calculates points
				// translation formula for x:
				transX = 15 * x + 250;
				// translation formula for y:
				transY = -15 * y + 250;
				if (transY != 250) // not sure why it is needed
					screen.setColor(Color.red);
				// new line
				if (transY > 100 && transY < 400)
					if (oldTransX != 0 && transX != 400) // try without to show
															// what happens
						screen.drawLine((int) oldTransX, (int) oldTransY,
								(int) transX, (int) transY);
				oldTransX = transX;
				oldTransY = transY;

			}
		}
		screen.setColor(new Color(0, 0, 0));
		screen.drawLine(100, 250, 400, 250);
		// System.out.println(A +" "+ B+" " + C);
		// *******************************

	}

	public void actionPerformed(ActionEvent event) {

		String mycaption = event.getActionCommand();
		String myA, myB, myC;
		// double x, y;
		double product;
		String backtoString;
		System.out.println(event);
		if (mycaption == "Draw") {
			myA = textA.getText();
			myB = textB.getText();
			myC = textC.getText();
			A = Double.parseDouble(myA);
			B = Double.parseDouble(myB);
			C = Double.parseDouble(myC);
			System.out.println(A + " " + B + " " + C);
			product = A * B;
			backtoString = Double.toString(product);
			System.out.println(A * B);
			labelAns.setText(backtoString);
			repaint();

		}

	}

}