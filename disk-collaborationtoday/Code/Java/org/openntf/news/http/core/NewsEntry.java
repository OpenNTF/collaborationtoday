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

import java.util.*;
import java.io.Serializable;
import org.ocpsoft.pretty.time.PrettyTime;

public class NewsEntry implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

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
	private final int _clicksTotal;
	private final int _clicksLastWeek;
	private final Date _creationDate;
	private final String _state;
	private final String _spotlightSentence;
	private final String _spotlightImageURL;
	private final String _moderator;
	private final String _lastEditor;
	private final Date _lastModified;

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
			if ((topStoryPosition != null) && (!topStoryPosition.isEmpty())) {
				_topStoryPosition = Integer.parseInt(topStoryPosition);
			} else {
				_topStoryPosition = -1;
			}
			if ((spotlightPosition != null) && (!spotlightPosition.isEmpty())) {
				_spotlightPosition = Integer.parseInt(spotlightPosition);
			} else {
				_spotlightPosition = -1;
			}
		} catch (Exception e) {
		}

		_clicksTotal = clicksTotal != null ? clicksTotal.intValue() : 0;
		_clicksLastWeek = clicksLastWeek != null ? clicksLastWeek.intValue() : 0;
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

	//	public void setSpotlightSentence(String spotlightSentence) {
	//		_spotlightSentence = spotlightSentence;
	//	}

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

	//	public void setSpotlightImageURL(String spotlightImageURL) {
	//		_spotlightImageURL = spotlightImageURL;
	//	}

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

	@Override
	public NewsEntry clone() {
		return new NewsEntry(
				this.getID(),
				this.getTID(),
				this.getTitle(),
				this.getPID(),
				this.getLink(),
				this.getImageURL(),
				this.getAbstract(),
				this.getModerationDate(),
				this.getPublicationDate(),
				this.isSpotlight(),
				this.isTopStory(),
				this.getTopStoryCategory(),
				this.getTopStoryPosition(),
				this.getClicksTotal(),
				this.getClicksLastWeek(),
				this.getCreationDate(),
				this.getState(),
				this.getSpotlightSentence(),
				this.getSpotlightImageURL(),
				this.getSpotlightPosition(),
				this.getModerator(),
				this.getLastEditor(),
				this.getLastModified()
		);
	}

	/* Added by Per Henrik Lausten */
	public String getPrettyTime() {
		PrettyTime p = new PrettyTime();
		return p.format(this.getPublicationDate());
	}

	public static class DescendingComparator implements Serializable, Comparator<NewsEntry> {
		private static final long serialVersionUID = 1L;

		public int compare(NewsEntry o1, NewsEntry o2) {
			return o2.getCreationDate().compareTo(o1.getCreationDate());
		}
	}

	// Sort first by spotlightPosition ascending then by creationDate descending
	public static class SpotlightComparator implements Serializable, Comparator<NewsEntry> {
		private static final long serialVersionUID = 1L;

		public int compare(NewsEntry o1, NewsEntry o2) {
			int result = o1.getSpotlightPosition() - o2.getSpotlightPosition();
			if(result == 0) {
				result = o2.getCreationDate().compareTo(o1.getCreationDate());
			}
			return result;
		}
	}
	// Sort first by topStoryPosition ascending then by creationDate descending
	public static class TopStoryComparator implements Serializable, Comparator<NewsEntry> {
		private static final long serialVersionUID = 1L;

		public int compare(NewsEntry o1, NewsEntry o2) {
			int result = o1.getTopStoryPosition() - o2.getTopStoryPosition();
			if(result == 0) {
				result = o2.getCreationDate().compareTo(o1.getCreationDate());
			}
			return result;
		}
	}
	public static class PopularityComparator implements Serializable, Comparator<NewsEntry> {
		private static final long serialVersionUID = 1L;

		public int compare(NewsEntry o1, NewsEntry o2) {
			if (o1.getClicksLastWeek() > o2.getClicksLastWeek()) {
				return -1;
			}
			if (o1.getClicksLastWeek() < o2.getClicksLastWeek()) {
				return 1;
			}
			return o1.getTitle().compareToIgnoreCase(o2.getTitle());
		}
	}
}
