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

import java.text.SimpleDateFormat;
import java.util.Date;


public class NewsEntry {

	private final String _iD;
	private final String _tID;
	private final String _title;
	private final String _pID;
	private final String _link;
	private final String _imageURL;
	private final String _abstract;
	private final Date _moderationDate;
	private final Date _publicationDate;
	private final boolean _isSpotlight;
	private final boolean _isTopStory;
	private final String _topStoryCategory;
	private int _topStoryPosition;
	private int _spotlightPosition;
	private int _clicksTotal;
	private int _clicksLastWeek;
	private final Date _creationDate;
	private final String _state;
	private String _spotlightSentence;
	private String _spotlightImageURL;
	private final String _moderator;
	private final String _lastEditor;
	private final Date _lastModified;

	public NewsEntry(final String iD, final String tID, final String title, final String pID, final String link, final String imageURL, final String abstractt, final Date moderationDate, final Date publicationDate, final boolean isSpotlight,
			final boolean isTopStory, final String topStoryCategory, final int topStoryPosition, final int clicksTotal, final int clicksLastWeek, final Date creationDate, final String state, final String spotlightSentence,
			final String spotlightImageURL, final int spotlightPosition, final String moderator, final String lastEditor, final Date lastModified) {
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

	public NewsEntry(final String iD, final String tID, final String title, final String pID, final String link, final String imageURL, final String abstractt, final Date moderationDate, final Date publicationDate, final String isSpotlight, final String isTopStory,
			final String topStoryCategory, final String topStoryPosition, final Double clicksTotal, final Double clicksLastWeek, final Date creationDate, final String state, final String spotlightSentence, final String spotlightImageURL,
			final String spotlightPosition, final String moderator, final String lastEditor, final Date lastModified) {
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

	public void setSpotlightSentence(final String spotlightSentence) {
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

	public void setSpotlightImageURL(final String spotlightImageURL) {
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
		SimpleDateFormat formatter = new SimpleDateFormat();
		return formatter.format(this.getPublicationDate());
	}

}
