package controller;

import com.ibm.xsp.model.domino.wrapped.DominoViewEntry;
import org.openntf.news.http.core.Person;
import frostillicus.controller.BasicXPageController;

import static org.openntf.news.http.core.MiscUtils.*;

public class mod extends BasicXPageController {
	private static final long serialVersionUID = 1L;

	public Person getEntryPerson() {
		DominoViewEntry entry = (DominoViewEntry)resolveVariable("entry");
		return getPersonsCache().getPerson((String)entry.getColumnValue("PID"));
	}
}
