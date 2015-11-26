package org.openntf.news.http.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewNavigator;

import com.ibm.xsp.extlib.util.ExtLibUtil;

public class Statistics implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String ITEM_DISPLAY_NAME = "PDisplayName";
	private static final String ITEM_TWITTER = "PTwitter";
	private static final String ITEM_EMAIL = "PEMail";
	private static final String ITEM_PICTURE_URL = "PPictureURL";
	private static final String ITEM_PICTURE_TYPE = "PPictureType";

	private List<Person> authors;
	private List<String> curators;

	public Statistics() {
		authors = new ArrayList<Person>();
		curators = new ArrayList<String>();

		try {
			// views
			View personsById = ExtLibUtil.getCurrentDatabase().getView("PersonsByPID");

			// get all posts and their amounts by poster
			View newsByAuthor = ExtLibUtil.getCurrentDatabase().getView("NewsByAuthor");
			ViewNavigator nav = newsByAuthor.createViewNav();
			ViewEntry ent = nav.getFirst();
			while (ent != null) {
				String pid = ent.getColumnValues().elementAt(0).toString();
				if (!pid.isEmpty()) {
					double posts = Double.valueOf(ent.getColumnValues().elementAt(1).toString());
					Document pDoc = personsById.getDocumentByKey(pid);
					if (pDoc != null) {
						// person found, create a person object
						Person person = new Person(posts, pid, pDoc.getItemValueString(ITEM_DISPLAY_NAME), pDoc.getItemValueString(ITEM_TWITTER), pDoc.getItemValueString(ITEM_EMAIL), pDoc
								.getItemValueString(ITEM_PICTURE_TYPE), pDoc.getItemValueString(ITEM_PICTURE_URL), pDoc.getUniversalID());
						authors.add(person);
					}
				}
				ent = nav.getNextCategory();
			}

		} catch (NotesException e) {
			e.printStackTrace();
		}
	}

	public List<Person> getAuthors() {
		Collections.sort(authors);
		return authors;
	}

	public List<String> getCurators() {
		return curators;
	}

}
