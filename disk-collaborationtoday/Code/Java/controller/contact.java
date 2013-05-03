package controller;

import java.util.Date;
import com.ibm.xsp.model.domino.wrapped.DominoDocument;
import lotus.domino.*;
import frostillicus.controller.BasicXPageController;

public class contact extends BasicXPageController {
	private static final long serialVersionUID = 1L;

	public void querySaveRequest() throws NotesException {
		Session session = (Session)resolveVariable("session");
		DominoDocument doc = (DominoDocument)resolveVariable("document1");
		DateTime now = session.createDateTime(new Date());
		doc.replaceItemValue("RCreationDate", now);
		doc.replaceItemValue("RState", "open");
		now.recycle();
	}
}
