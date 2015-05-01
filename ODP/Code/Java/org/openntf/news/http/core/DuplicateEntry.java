package org.openntf.news.http.core;

import java.io.Serializable;

public class DuplicateEntry implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6196240891950391124L;
	private String Nid;
	private String NTitle;
	private String NLink;
	
	public DuplicateEntry(){
		//
	}

	public String getNid() {
		return Nid;
	}

	public void setNid(String nid) {
		Nid = nid;
	}

	public String getNTitle() {
		return NTitle;
	}

	public void setNTitle(String title) {
		NTitle = title;
	}

	public String getNLink() {
		return NLink;
	}

	public void setNLink(String link) {
		NLink = link;
	}

}
