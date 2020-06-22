package org.openntf.news.http.core;

import lotus.domino.Document;
import lotus.domino.NotesException;

import com.ibm.xsp.extlib.util.ExtLibUtil;

public class Helper {

	public static boolean isChampion(String pid) {
		boolean result = false;
		try {
			Document pDoc = ExtLibUtil.getCurrentDatabase().getView("PersonsByPID").getDocumentByKey(pid);
			if (pDoc != null) {
				result = pDoc.getItemValueString("PChampion").equals("1");
			}
		} catch (NotesException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	public static boolean isAmbassador(String pid) {
		boolean result = false;
		try {
			Document pDoc = ExtLibUtil.getCurrentDatabase().getView("PersonsByPID").getDocumentByKey(pid);
			if (pDoc != null) {
				result = pDoc.getItemValueString("PHCLMaster").equals("1");
			}
		} catch (NotesException e) {
			e.printStackTrace();
		}

		return result;
	}
}
