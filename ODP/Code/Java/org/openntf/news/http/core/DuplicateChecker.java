package org.openntf.news.http.core;

import java.util.ArrayList;
import java.util.List;
import lotus.domino.Database;
import lotus.domino.DocumentCollection;
import lotus.domino.View;
import lotus.domino.Document;

import com.ibm.xsp.extlib.util.ExtLibUtil;

public class DuplicateChecker {
	List<DuplicateEntry> duplicatesList = new ArrayList<DuplicateEntry>();

	public DuplicateChecker() {

	}

	public List<DuplicateEntry> checkDuplicates(String sNID, String sNTitle, String sNLink, Boolean bDoFuzzySearch) {

		try{
			//clear HashMap
			duplicatesList.clear();
			//step 1: check if link already exist in database.
			if(!linkAlreadyExists(sNID, sNTitle, sNLink)) {
				//step 2: if step 1 did not get results, do fuzzy search title
				if(bDoFuzzySearch){
					doFuzzySearch(sNID, sNTitle, sNLink);
				}
			}

		} catch(Exception e) {
			MiscUtils.logException(e);
		}
		return this.getDuplicatesList();
	}

	private boolean linkAlreadyExists(String sNID, String sNTitle, String sNLink1) {
		boolean bCheck = false;
		String sNLink;
		View newsByLink = null;
		DocumentCollection docs = null;
		try{
			int hashIndex = sNLink1.indexOf("#");
			if(hashIndex > 0) {
				sNLink = sNLink1.substring(0, hashIndex);
			} else {
				sNLink = sNLink1;
			}

			Database db = ExtLibUtil.getCurrentDatabase();
			newsByLink = db.getView("NewsAllbyLink");
			newsByLink.setAutoUpdate(false);
			docs = newsByLink.getAllDocumentsByKey(sNLink, true);

			Document doc = docs.getFirstDocument();
			while(doc != null) {
				if(!doc.getItemValueString("NID").equals(sNID)){
					//the document is not the current document
					bCheck = true;
					DuplicateEntry dup = new DuplicateEntry();
					dup.setNid(doc.getItemValueString("NID"));
					dup.setNLink(doc.getItemValueString("NLink"));
					dup.setNTitle(doc.getItemValueString("NTitle"));

					duplicatesList.add(dup);
				}

				Document tempDoc = doc;
				doc = docs.getNextDocument(doc);
				tempDoc.recycle();
			}

		} catch(Exception e) {
			MiscUtils.logException(e);
		} finally {
			MiscUtils.incinerate(docs, newsByLink);
		}
		return bCheck;
	}

	private boolean doFuzzySearch(String sNID, String sNTitle, String sNLink) {
		boolean bCheck = false;
		DocumentCollection docs = null;
		try {
			Database db = ExtLibUtil.getCurrentDatabase();
			docs = db.FTSearch(sNTitle, 30, Database.FT_SCORES, Database.FT_FUZZY);
			Document doc = docs.getFirstDocument();
			while(doc != null) {
				if(!doc.getItemValueString("NID").equals(sNID)){
					// the document is not the current document
					bCheck = true;
					DuplicateEntry dup = new DuplicateEntry();
					dup.setNid(doc.getItemValueString("NID"));
					dup.setNLink(doc.getItemValueString("NLink"));
					dup.setNTitle(doc.getItemValueString("NTitle"));

					duplicatesList.add(dup);
				}

				Document tempDoc = doc;
				doc = docs.getNextDocument(doc);
				tempDoc.recycle();
			}

		} catch(Exception e) {
			MiscUtils.logException(e);
		} finally {
			MiscUtils.incinerate(docs);
		}
		return bCheck;
	}

	public List<DuplicateEntry> getDuplicatesList() {
		return duplicatesList;
	}

	public void setDuplicatesList(List<DuplicateEntry> duplicatesList) {
		this.duplicatesList = duplicatesList;
	}
}
