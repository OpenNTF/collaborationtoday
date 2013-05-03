package controller;

import frostillicus.controller.BasicXPageController;
import org.openntf.news.http.core.Person;

import com.ibm.xsp.model.domino.wrapped.DominoViewEntry;

import static org.openntf.news.http.core.MiscUtils.*;

public class modApproved extends BasicXPageController {
	private static final long serialVersionUID = 1L;

	public Person getEntryPerson() {
		DominoViewEntry entry = (DominoViewEntry)resolveVariable("entry");
		return getPersonsCache().getPerson((String)entry.getColumnValue("PID"));
	}
}
