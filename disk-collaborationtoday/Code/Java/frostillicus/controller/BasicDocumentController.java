package frostillicus.controller;

import lotus.domino.*;

import com.ibm.xsp.model.domino.wrapped.DominoDocument;
import java.util.Map;

public class BasicDocumentController extends BasicXPageController implements DocumentController {
	private static final long serialVersionUID = 1L;

	public void queryNewDocument() throws Exception { }
	public void postNewDocument() throws Exception { }
	public void queryOpenDocument() throws Exception { }
	public void postOpenDocument() throws Exception {
		DominoDocument doc = this.getDoc();
		Map<String, Object> viewScope = getViewScope();
		viewScope.put("$REF", doc.getValue("$REF"));
	}
	public void querySaveDocument() throws Exception { }
	public void postSaveDocument() throws Exception { }

	public String save() throws Exception {
		DominoDocument doc = this.getDoc();

		Map<String, Object> viewScope = getViewScope();
		viewScope.put("$REF", doc.getValue("$REF"));

		if(doc.save()) {
			Database database = doc.getParentDatabase();
			if(database.isFTIndexed()) {
				database.updateFTIndex(false);
			}
			return "xsp-success";
		} else {
			return "xsp-failure";
		}
	}
	public String cancel() throws Exception {
		return "xsp-cancel";
	}
	public String delete() throws Exception {
		DominoDocument doc = this.getDoc();

		doc.getDocument(true).remove(true);
		return "xsp-success";
	}

	public String getDocumentId() {
		try {
			return this.getDoc().getDocument().getUniversalID();
		} catch(Exception e) { return ""; }
	}

	public boolean isEditable() { return this.getDoc().isEditable(); }
	public boolean isNewNote() throws NotesException { return this.getDoc().isNewNote(); }

	protected DominoDocument getDoc() {
		return (DominoDocument)resolveVariable("doc");
	}
}
