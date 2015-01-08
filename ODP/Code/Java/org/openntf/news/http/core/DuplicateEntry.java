package org.openntf.news.http.core;

public class DuplicateEntry {

	private String Nid;
	private String NTitle;
	private String NLink;
	
	public DuplicateEntry(){
		
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
