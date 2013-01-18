package org.openntf.news.monster.shared;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {
	public static int ERROR_MESSAGES=1;
	public static int LOG_MESSAGES=2;
	public static int DEBUG_MESSAGES=4;
	public static int STACK_TRACES=8;
	public static int ALL_MESSAGES=15;
		
	private String handler;
	private int mode;
	
	public Logger() {
		this("");
	}
	
	public Logger(String handler) {
		this(handler, ERROR_MESSAGES+LOG_MESSAGES+DEBUG_MESSAGES);
	}

	public Logger(String handler, int mode) {
		setHandler(handler);
		setMode(mode);
	}

	public String formatMessage(String message) {
		Calendar cal=Calendar.getInstance();
		DateFormat sdf=SimpleDateFormat.getDateTimeInstance();
		
		return sdf.format(cal.getTime())+"> "+this.handler+":"+message;
	}
	
	public void log(Object obj) {
		if(checkMode(LOG_MESSAGES)) {
			System.out.println("L: "+formatMessage(obj.toString()));
		}
	}
	
	public boolean checkMode(int targetMode) {
		return (mode & targetMode) > 0;
	}

	public void error(Object obj) {
		if(checkMode(ERROR_MESSAGES)) {
			System.err.println("E: "+formatMessage(obj.toString()));
		}
	}
	
	public void debug(Object obj) {
		if(checkMode(DEBUG_MESSAGES)) {
			System.out.println("D: "+formatMessage(obj.toString()));
		}
	}

	public void printStack(Throwable t) {
		if(checkMode(STACK_TRACES)) {
			t.printStackTrace();
		}
	}
	
	public String getHandler() {
		return handler;
	}
	public void setHandler(String handler) {
		this.handler = handler;
	}

	/**
	 * @return the mode
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

}
