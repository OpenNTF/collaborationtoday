package controller;

import frostillicus.controller.BasicXPageController;
import org.openntf.news.http.core.NewsEntry;
import org.openntf.news.http.core.Person;

import static org.openntf.news.http.core.MiscUtils.*;

public class modPopular extends BasicXPageController {
	private static final long serialVersionUID = 1L;

	public Person getEntryPerson() {
		NewsEntry entry = (NewsEntry)resolveVariable("entry");
		return getPersonsCache().getPerson(entry.getPID());
	}
}
