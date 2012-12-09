package org.openntf.news;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;

import org.openntf.news.shared.Constants;
import org.openntf.news.shared.StoryReaderException;
import org.openntf.news.shared.Utilities;

public class Story {

	//From Entry
	private String id;
	private String link;
	private Calendar date;
	private String title;
	private String abstractContent;
	private String fullContent;
	
	//Set on Save
	private Calendar creationDate;
	
	private HashMap<String,Object> additionalFields;
	private boolean redirectEnabled=false;

	public Story() {
		additionalFields=new HashMap<String,Object>();
		
		// setting all things GMT to prevent time zone inconsistencies...
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
	}

	public void addField(String name, Object value) {
		additionalFields.put(name, value);
	}

	public Object getField(String name) {
		return additionalFields.get(name);
	}

	public void addFields(Map<String,Object> values) {
		additionalFields.putAll(values);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLink() {
		return link;
	}


	public void setLink(String link) {
		this.link = link;
	}


	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getAbstractContent() {
		return abstractContent;
	}

	public void setAbstractContent(String abstractContent) {
		this.abstractContent = abstractContent;
	}

	public String getFullContent() {
		return fullContent;
	}

	public void setFullContent(String fullContent) {
		this.fullContent = fullContent;
	}

	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	public String toString() {
		DateFormat sdf = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		return getTitle() + " (" + getLink() + " - " + sdf.format(getDate().getTime()) + ")";
	}
	
	public boolean saveToDatabase(Database db) throws StoryReaderException {
		try {
			setCreationDate(Calendar.getInstance());
			
			Session session=db.getParent();
			
			Document storyDoc = db.createDocument();
			storyDoc.replaceItemValue("Form", "News");
			storyDoc.replaceItemValue("NID", Utilities.generateUniqueId());				
			
			if(isRedirectEnabled()) {
				try {
					setLink(Utilities.followRedirect(getLink()));
				} catch (IOException e) {
					throw new StoryReaderException(Constants.EXCEPTION_COMM_ERROR + ": "+getLink());
				}
			}

			storyDoc.replaceItemValue("NLink", getLink());

			storyDoc.replaceItemValue("NCreationDate", session.createDateTime(getCreationDate()));
			storyDoc.replaceItemValue("NPublicationDate", session.createDateTime(getDate()));
			storyDoc.replaceItemValue("NTitle", getTitle());
			storyDoc.replaceItemValue("NAbstract", getAbstractContent());
			storyDoc.replaceItemValue("NContent", getFullContent());

			for (Map.Entry<String, Object> entry : additionalFields.entrySet()) {
	        	storyDoc.replaceItemValue(entry.getKey(), entry.getValue());
	        }
			
			storyDoc.save();
			storyDoc.recycle();


			return true;
		} catch (NotesException e) {
			throw new StoryReaderException(Constants.EXCEPTION_SAVE_STORY + ": "+ e.getMessage());
		}

	}

	public boolean isRedirectEnabled() {
		return redirectEnabled;
	}

	public void setRedirectEnabled(boolean redirectEnabled) {
		this.redirectEnabled = redirectEnabled;
	}
	
	
}
