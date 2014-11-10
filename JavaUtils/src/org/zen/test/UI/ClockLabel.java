package org.zen.test.UI;

import java.awt.*;
import java.util.*;

class TickerThread extends Thread {
	ClockLabel c1;

	public TickerThread(ClockLabel c) {
		c1 = c;
	}

	public void run() {
		while (true) {
			try {
				sleep(2000);
			} catch (InterruptedException e) {
				break;
			}
			c1.updateTime();
		}
	}
}

/**
 * A Label subclass that offers a time-of-day clock which updates every two
 * seconds. Use the startClock() method to start keeping time, and stopClock()
 * to stop keeping time.
 */
public class ClockLabel extends Label {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4105223655260227679L;
	private Thread ticker;

	public ClockLabel() {
		super("Clock", Label.CENTER);
		ticker = null;
	}

	public void startClock() {
		if (ticker == null) {
			ticker = new TickerThread(this);
			ticker.start();
		}
		updateTime();
	}

	public void stopClock() {
		ticker.interrupt();
		ticker = null;
	}

	public void updateTime() {
		Date now = new Date();
		String time = now.toString().substring(11, 23);
		setText(time);
	}

	public static void main(String[] args) {
		Frame fr = new Frame("Clock test");
		Label l1 = new Label("THIS IS A LABEL!");
		ClockLabel c1 = new ClockLabel();
		c1.setBackground(Color.gray);
		System.out.println("Starting the clock");
		c1.startClock();
		fr.add(l1, "Center");
		fr.add(c1, "North");
		fr.pack();
		fr.setVisible(true);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
		}
		System.out.println("Stopping the clock");
		c1.stopClock();
		try {
			Thread.sleep(12000);
		} catch (InterruptedException e) {
		}
		System.out.println("Starting the clock again");
		c1.startClock();

	}
}
