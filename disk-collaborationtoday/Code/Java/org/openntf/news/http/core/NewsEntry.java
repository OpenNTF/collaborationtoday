package org.openntf.news.http.core;

/*
 * � Copyright IBM, 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 * Author: Niklas Heidloff - niklas_heidloff@de.ibm.com
 */

import java.util.Date;

import org.ocpsoft.pretty.time.PrettyTime;

public class NewsEntry {

	private String _iD;
	private String _tID;
	private String _title;
	private String _pID;
	private String _link;
	private String _imageURL;
	private String _abstract;
	private Date _moderationDate;	
	private Date _publicationDate;
	private boolean _isSpotlight;
	private boolean _isTopStory;
	private String _topStoryCategory;
	private int _topStoryPosition;
	private int _spotlightPosition;
	private int _clicksTotal;
	private int _clicksLastWeek;
	private Date _creationDate;
	private String _state;
	private String _spotlightSentence;
	private String _spotlightImageURL;
	private String _moderator;
	private String _lastEditor;
	private Date _lastModified;

	public NewsEntry(String iD, String tID, String title, String pID, String link, String imageURL, String abstractt, Date moderationDate,
			Date publicationDate, boolean isSpotlight, boolean isTopStory, String topStoryCategory, int topStoryPosition, int clicksTotal,
			int clicksLastWeek, Date creationDate, String state, String spotlightSentence, String spotlightImageURL, int spotlightPosition,
			String moderator, String lastEditor, Date lastModified) {
		_iD = iD;
		_tID = tID;
		_title = title;
		_pID = pID;
		_link = link;
		_imageURL = imageURL;
		_abstract = abstractt;
		_moderationDate = moderationDate;
		_publicationDate = publicationDate;
		_isSpotlight = isSpotlight;
		_isTopStory = isTopStory;
		_topStoryCategory = topStoryCategory;
		_clicksTotal = clicksTotal;
		_clicksLastWeek = clicksLastWeek;
		_topStoryPosition = topStoryPosition;
		_spotlightPosition = spotlightPosition;
		_creationDate = creationDate;
		_state = state;
		_spotlightSentence = spotlightSentence;
		_spotlightImageURL = spotlightImageURL;
		_moderator = moderator;
		_lastEditor = lastEditor;
		_lastModified = lastModified;
	}

	public NewsEntry(String iD, String tID, String title, String pID, String link, String imageURL, String abstractt, Date moderationDate,
			Date publicationDate, String isSpotlight, String isTopStory, String topStoryCategory, String topStoryPosition,
			Double clicksTotal, Double clicksLastWeek, Date creationDate, String state, String spotlightSentence, String spotlightImageURL,
			String spotlightPosition, String moderator, String lastEditor, Date lastModified) {
		_iD = iD;
		_tID = tID;
		_title = title;
		_pID = pID;
		_link = link;
		_moderator = moderator;
		_lastEditor = lastEditor;
		_lastModified = lastModified;
		_imageURL = imageURL;
		_abstract = abstractt;
		_moderationDate = moderationDate;
		_publicationDate = publicationDate;
		_creationDate = creationDate;
		_state = state;
		_spotlightSentence = spotlightSentence;
		_spotlightImageURL = spotlightImageURL;

		_isSpotlight = "yes".equalsIgnoreCase(isSpotlight);
		_isTopStory = "yes".equalsIgnoreCase(isTopStory);
		_topStoryCategory = topStoryCategory;

		try {
			if ((topStoryPosition != null) && (!topStoryPosition.isEmpty()))
				_topStoryPosition = Integer.parseInt(topStoryPosition);
			if ((spotlightPosition != null) && (!spotlightPosition.isEmpty()))
				_spotlightPosition = Integer.parseInt(spotlightPosition);
			if (clicksTotal != null) {
				_clicksTotal = clicksTotal.intValue();
			}
			if (clicksLastWeek != null) {
				_clicksLastWeek = clicksLastWeek.intValue();
			}
		} catch (Exception e) {
		}
	}

	public String getModerator() {
		return _moderator;
	}

	public String getLastEditor() {
		return _lastEditor;
	}

	public Date getLastModified() {
		return _lastModified;
	}

	public void setSpotlightSentence(String spotlightSentence) {
		_spotlightSentence = spotlightSentence;
	}

	public String getSpotlightSentence() {
		return _spotlightSentence;
	}

	public int getClicksTotal() {
		return _clicksTotal;
	}

	public int getClicksLastWeek() {
		return _clicksLastWeek;
	}

	public int getTopStoryPosition() {
		return _topStoryPosition;
	}

	public int getSpotlightPosition() {
		return _spotlightPosition;
	}

	public String getTopStoryCategory() {
		return _topStoryCategory;
	}

	public boolean isTopStory() {
		return _isTopStory;
	}

	public boolean isSpotlight() {
		return _isSpotlight;
	}

	public Date getPublicationDate() {
		return _publicationDate;
	}

	public Date getModerationDate() {
		return _moderationDate;
	}

	public String getAbstract() {
		return _abstract;
	}

	public String getImageURL() {
		return _imageURL;
	}

	public String getSpotlightImageURL() {
		return _spotlightImageURL;
	}

	public void setSpotlightImageURL(String spotlightImageURL) {
		_spotlightImageURL = spotlightImageURL;
	}

	public String getLink() {
		return _link;
	}

	public String getPID() {
		return _pID;
	}

	public String getTitle() {
		return _title;
	}

	public String getTID() {
		return _tID;
	}

	public String getID() {
		return _iD;
	}

	public Date getCreationDate() {
		return _creationDate;
	}

	public String getState() {
		return _state;
	}

	/* Added by Per Henrik Lausten */
	public String getPrettyTime() {
		PrettyTime p = new PrettyTime();
		return p.format(this.getPublicationDate());
	}


	// TODO Add getPerson()? That'd cross the models a bit, but it sure would be convenient
}
