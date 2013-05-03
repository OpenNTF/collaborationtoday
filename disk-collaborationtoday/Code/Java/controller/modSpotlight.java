package controller;

import lotus.domino.*;
import frostillicus.controller.BasicXPageController;
import org.openntf.news.http.core.NewsEntry;
import org.openntf.news.http.core.Person;

import static org.openntf.news.http.core.MiscUtils.*;

public class modSpotlight extends BasicXPageController {
	private static final long serialVersionUID = 1L;

	public boolean isCurrentUserSpotlightMod() throws NotesException {
		Session session = (Session)resolveVariable("session");
		String userName = session.getEffectiveUserName();

		return getConfigCache().isUserSpotlightModerator(userName);
	}

	public Person getEntryPerson() {
		NewsEntry entry = (NewsEntry)resolveVariable("entry");
		return getPersonsCache().getPerson(entry.getPID());
	}
}
