package org.openntf.news.http.core;

import java.util.ArrayList;

import lotus.domino.Database;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;
import lotus.domino.Document;

import com.ibm.xsp.extlib.util.ExtLibUtil;

public class DuplicateChecker {
	ArrayList<DuplicateEntry> duplicatesList = new ArrayList<DuplicateEntry>();
	
	public DuplicateChecker(){
		
	}
	
	public ArrayList<DuplicateEntry> checkDuplicates(String sNID, String sNTitle, String sNLink, Boolean bDoFuzzySearch){

		try{
			//clear HashMap
			duplicatesList.clear();
			//step 1: check if link already exist in database.
			if(!checkIfLinkAllReadyExist(sNID, sNTitle, sNLink)){
				//step 2: if step 1 did not get results, do fuzzy search title
				if(bDoFuzzySearch){
					doFuzzySearch(sNID, sNTitle, sNLink);
				}
			}

		}catch(Exception e){
			
		}finally{
			
		}
		return this.getDuplicatesList();
	}
	
	private boolean checkIfLinkAllReadyExist(String sNID, String sNTitle, String sNLink1){
		boolean bCheck = false;
		String sNLink;
		View vw = null;
		ViewEntryCollection vec = null;
		try{
			if(sNLink1.indexOf("#")>0){
				sNLink = sNLink1.substring(0, sNLink1.indexOf("#"));
			}else{
				sNLink = new String(sNLink1);
			}
					
			Database db = ExtLibUtil.getCurrentDatabase();
			vw = db.getView("NewsAllbyLink");
			vec = vw.getAllEntriesByKey(sNLink,true);
			if(vec.getCount()>0){
				ViewEntry entry = vec.getFirstEntry();
				while(entry!=null){
					ViewEntry tmpEntry = vec.getNextEntry(entry);
					Document doc = entry.getDocument();
					if(doc.getItemValueString("NID").compareTo(sNID)!=0){
						//the document is not the current document
						bCheck = true;
						DuplicateEntry dup = new DuplicateEntry();
						dup.setNid(doc.getItemValueString("NID"));
						dup.setNLink(doc.getItemValueString("NLink"));
						dup.setNTitle(doc.getItemValueString("NTitle"));
						
						duplicatesList.add(dup);	
					}
					entry.recycle();
					entry = tmpEntry;
				}
			}
			if(vw!=null){
				vw.recycle();
			}
			
		}catch(Exception e){
			
		}finally{		
				try {
					if(vec!=null){
						vec.recycle();
					}
					if(vw!=null){
					vw.recycle();
					}
				} catch (NotesException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		}
		return bCheck;
	}
	
	@SuppressWarnings("static-access")
	private boolean doFuzzySearch(String sNID, String sNTitle, String sNLink){
		boolean bCheck = false;
		DocumentCollection dc = null;
		try{
			Database db = ExtLibUtil.getCurrentDatabase();
			dc = db.FTSearch(sNTitle, 30, db.FT_SCORES, db.FT_FUZZY);
			if(dc.getCount()>0){
				Document doc = dc.getFirstDocument();
				while(doc!=null){
					Document docNext = dc.getNextDocument(doc);
					if(doc.getItemValueString("NID").compareTo(sNID)!=0){
						//the document is not the current document
						bCheck = true;
						DuplicateEntry dup = new DuplicateEntry();
						dup.setNid(doc.getItemValueString("NID"));
						dup.setNLink(doc.getItemValueString("NLink"));
						dup.setNTitle(doc.getItemValueString("NTitle"));
						
						duplicatesList.add(dup);	
					}
					doc.recycle();
					doc = docNext;
				}
			}
			
		}catch(Exception e){
			
		}finally{		
				try {
					if(dc!=null){
					dc.recycle();
					}
				} catch (NotesException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return bCheck;
	}

	public ArrayList<DuplicateEntry> getDuplicatesList() {
		return duplicatesList;
	}

	public void setDuplicatesList(ArrayList<DuplicateEntry> duplicatesList) {
		this.duplicatesList = duplicatesList;
	}
}
