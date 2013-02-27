package org.openntf.news.monster.meta;

import java.util.Calendar;

import org.openntf.news.monster.shared.Utilities;

import lotus.domino.Database;
import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;

public class FeedDefinition {
	private String id="";
	private String noteId="";
	
	private String url="";
	private String authorId="";
	private boolean redirection=false;
	
	private Calendar lastSuccess=null;
	private Calendar lastTry=null;
	private Calendar lastError=null;

	private String displayName="";
	private String lastErrorMessage="";
	
	public FeedDefinition(String id) {
		this.id=id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	public boolean isRedirection() {
		return redirection;
	}

	public void setRedirection(boolean redirection) {
		this.redirection = redirection;
	}

	public Calendar getLastSuccess() {
		return lastSuccess;
	}

	public void setLastSuccess(Calendar lastSuccess) {
		this.lastSuccess = lastSuccess;
	}

	public Calendar getLastTry() {
		return lastTry;
	}

	public void setLastTry(Calendar lastTry) {
		this.lastTry = lastTry;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the lastError
	 */
	public Calendar getLastError() {
		return lastError;
	}

	/**
	 * @param lastError the lastError to set
	 */
	public void setLastError(Calendar lastError) {
		this.lastError = lastError;
	}

	/**
	 * @return the lastErrorMessage
	 */
	public String getLastErrorMessage() {
		return lastErrorMessage;
	}

	/**
	 * @param lastErrorMessage the lastErrorMessage to set
	 */
	public void setLastErrorMessage(String lastErrorMessage) {
		this.lastErrorMessage = lastErrorMessage;
	}

	/**
	 * @param db: The database object where feed definitions exists
	 * @return true if we can't get the feed definition document. Probably deleted.  
	 */
	public boolean isDeleted(Database db) {
		Document feedDoc=null;
		try {
			feedDoc = db.getDocumentByID(noteId);
			return (feedDoc==null);
		} catch (NotesException e) {
			throw new RuntimeException("Unable to check if Feed definition is deleted!");
		} finally {
			Utilities.recycleObject(feedDoc);
		}
	}
	
	public boolean saveState(Database db) throws NotesException {
		boolean result;
		
		Session session=db.getParent();
		DateTime someDate;
		
		Document feedDoc = db.getDocumentByID(noteId);
		
		if(getLastTry()!=null) {
			someDate=session.createDateTime(getLastTry());
			feedDoc.replaceItemValue("BLastTry", someDate);
			someDate.recycle();
		} else {
			feedDoc.replaceItemValue("BLastTry", "");
		}
		
		if(getLastSuccess()!=null) {
			someDate=session.createDateTime(getLastSuccess());
			feedDoc.replaceItemValue("BLastSuccess", someDate);
			someDate.recycle();
		} else {
			feedDoc.replaceItemValue("BLastSuccess", "");
		}
		
		if(getLastError()!=null) {
			someDate=session.createDateTime(getLastError());
			feedDoc.replaceItemValue("BLastError", someDate);
			someDate.recycle();
		} else {
			feedDoc.replaceItemValue("BLastError", "");
		}
		
		feedDoc.replaceItemValue("BLastErrorMessage", getLastErrorMessage());
		
		result=feedDoc.save();
		feedDoc.recycle();
		return result;
	}

	/**
	 * @return the noteId
	 */
	public String getNoteId() {
		return noteId;
	}

	/**
	 * @param noteId the noteId to set
	 */
	public void setNoteId(String noteId) {
		this.noteId = noteId;
	}
	
}
