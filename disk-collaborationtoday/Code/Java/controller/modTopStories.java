package controller;

import java.util.*;
import lotus.domino.*;
import frostillicus.controller.BasicXPageController;
import org.openntf.news.http.core.Category;
import org.openntf.news.http.core.NewsEntry;
import org.openntf.news.http.core.Person;

import static org.openntf.news.http.core.MiscUtils.*;

public class modTopStories extends BasicXPageController {
	private static final long serialVersionUID = 1L;

	public boolean isCurrentUserTopStoriesMod() throws NotesException {
		Session session = (Session)resolveVariable("session");
		String userName = session.getEffectiveUserName();

		return getConfigCache().isUserTopStoriesModerator(userName);
	}

	public Person getTopStoryPerson() {
		NewsEntry entry = (NewsEntry)resolveVariable("entryMTT");
		return getPersonsCache().getPerson(entry.getPID());
	}

	public List<NewsEntry> getCategoryTopStories() {
		Category category = (Category)resolveVariable("category");
		return getNewsCache().getTopStories(category.getID());
	}
	public Person getCategoryStoryPerson() {
		NewsEntry entry = (NewsEntry)resolveVariable("entryMTC");
		return getPersonsCache().getPerson(entry.getPID());
	}
}
