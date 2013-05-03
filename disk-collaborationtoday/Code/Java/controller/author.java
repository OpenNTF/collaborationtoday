package controller;

import lotus.domino.*;
import java.util.*;
import com.ibm.xsp.model.domino.wrapped.DominoDocument;
import org.openntf.news.http.core.Person;
import frostillicus.controller.BasicXPageController;

import static org.openntf.news.http.core.MiscUtils.*;

public class author extends BasicXPageController {
	private static final long serialVersionUID = 1L;

	// TODO figure out why I can't call this directly as querySaveDocument="#{pageController.querySaveDocument}" - wrong params?
	public boolean querySaveDocument() throws Exception {
		DominoDocument doc = (DominoDocument)resolveVariable("currentDocument");

		if(!doc.isNewNote()) { return true; }

		String pid = sanitizeNameForID(doc.getItemValueString("PDisplayName"));
		Person person = getPersonsCache().getPerson(pid);

		return person.getDisplayName().isEmpty();
	}

	public void processNewDoc() throws Exception {
		DominoDocument doc = (DominoDocument)resolveVariable("currentDocument");

		doc.replaceItemValue("PCreationDate", new Date());

		String pid = sanitizeNameForID(doc.getItemValueString("PDisplayName"));
		doc.replaceItemValue("PID", pid);
	}

	public boolean isNewNote() throws NotesException {
		return ((DominoDocument)resolveVariable("currentDocument")).isNewNote();
	}
	public String getUniversalID() throws NotesException {
		return ((DominoDocument)resolveVariable("currentDocument")).getDocument().getUniversalID();
	}

	public void chooseMypic() throws NotesException {
		DominoDocument doc = (DominoDocument)resolveVariable("document1");
		doc.replaceItemValue("currentMypic", resolveVariable("name"));
		doc.save();
	}
	@SuppressWarnings("unchecked")
	public void deleteMypic() throws NotesException {
		/*
		 * var mypic = document11.getDocument(true)
			var rtItem:NotesRichTextItem = mypic.getFirstItem('mypics')
			var image:NotesEmbeddedObject = rtItem.getEmbeddedObject(name)
			image.remove()
			var mypicNames = mypic.getFirstItem('mypicNames')
			var rVals = mypicNames.getValues()
			rVals.remove(name)
			mypicNames.setValues(rVals)
			if (mypic.getItemValueString('currentMypic') == name) {
				if (mypicNames.getValues() == null) {
					mypic.replaceItemValue('currentMypic', '')
				} else {
					mypic.replaceItemValue('currentMypic', mypicNames.getValues().get(0))
				}
			}
			document11.save()
		 */

		DominoDocument doc = (DominoDocument)resolveVariable("document1");
		String name = (String)resolveVariable("name");

		Document mypic = doc.getDocument(true);
		RichTextItem rtItem = (RichTextItem)mypic.getFirstItem("mypics");
		EmbeddedObject image = rtItem.getEmbeddedObject(name);
		image.remove();

		List<String> names = mypic.getItemValue("mypicNames");
		names.remove(name);
		mypic.replaceItemValue("mypicNames", names);

		if(name.equals(mypic.getItemValueString("currentMypic"))) {
			if(names.size() == 0) {
				mypic.replaceItemValue("currentMypic", "");
			} else {
				mypic.replaceItemValue("currentMypic", names.get(0));
			}
		}
		doc.save();
	}

	private String sanitizeNameForID(final String name) {
		return name == null ? "" : name.replaceAll("\\s", "").replaceAll("\\W", "").toLowerCase();
	}
}
