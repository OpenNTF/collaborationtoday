package org.openntf.news.monster.shared;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;

public final class Utilities {

	private static String UNIQUEID_LETTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static int UNIQUEID_DEFAULTSIZE = 6;
	
	public static String generateUniqueId() {
		StringBuilder sb = new StringBuilder(UNIQUEID_DEFAULTSIZE);
		Random random = new Random();
		for (int i=0; i < UNIQUEID_DEFAULTSIZE; i++) {
			sb.append(UNIQUEID_LETTERS.charAt(random.nextInt(UNIQUEID_LETTERS.length())));
		}
	    return sb.toString().toLowerCase(Locale.US);            
	}

	public static String followRedirect(String urlString) throws IOException {
		URL url=new URL(urlString);
		HttpURLConnection conn=(HttpURLConnection) url.openConnection();
		conn.setInstanceFollowRedirects(false);

		if(conn.getResponseCode()==301 && conn.getHeaderField("Location")!=null) {
			return conn.getHeaderField("Location");
		}
		
		return urlString;
	}
	
	public static String getMemoryStatus() {
		Runtime rt = Runtime.getRuntime();
		NumberFormat nf = NumberFormat.getInstance();
		nf.setGroupingUsed(true);
		nf.setMinimumFractionDigits(0);
		long total_mem = rt.totalMemory();
		long free_mem = rt.freeMemory();
		long used_mem = total_mem - free_mem;
		return "Amount of used memory/free memory: " + nf.format(used_mem/1000) + "KB / " + nf.format(free_mem/1000)+" KB";
		
		
	}

	public static Calendar getDateField(Document doc, String fieldName) throws NotesException {
		Item someItem=doc.getFirstItem(fieldName);
		DateTime someDate=null;
		Calendar result=null;
		
		if(null != someItem && someItem.getType()==Item.DATETIMES) {
			someDate=someItem.getDateTimeValue();
			result=Calendar.getInstance();
			result.setTime(someDate.toJavaDate());
		}

		if(someItem!=null) someItem.recycle();
		if(someDate!=null) someDate.recycle();
		
		return result;
	}
	
	/**
	 * recycles a domino document instance
	 * 
	 * @param lotus.domino.Base 
	 *           obj to recycle
	 * @category Domino
	 * @author Sven Hasselbach
	 * @category Tools
	 * @version 1.1
	 */
	public static void recycleObject(lotus.domino.Base obj) {
		if (obj != null) {
			try {
				obj.recycle();
			} catch (Exception e) {}
		}
	}

	/**
	 * 	 recycles multiple domino objects
	 *		
	 * @param objs
	 * @author Nathan T. Freeman
	 * 
	 */
	public static void recycleObjects(lotus.domino.Base... objs) {
		for ( lotus.domino.Base obj : objs ) 
			recycleObject(obj);
	}
}
