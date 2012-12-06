package org.openntf.news.http.core;

/*
 * Copyright IBM, 2012
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

/*
 * 2012-09-29 - Jesse Gallagher
 * Modified view looping to setAutoUpdate(false), only run
 * .getColumnValues() once per entry, and to use .setPreferJavaDates
 */

import java.util.*;
import lotus.domino.*;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class NewsCache {

	private boolean _isCached = false;
	private Collection<NewsEntry> _newsEntries;
	private Collection<NewsEntry> _spotlightNewsEntries;
	private Collection<NewsEntry> _topTopStories;
	private List<NewsEntry> _popularNewsEntries;
	private Map<String, Collection<NewsEntry>> _typedNewsEntries;
	private Map<String, Collection<NewsEntry>> _categorizedTopNewsEntries;
	private Date _lastUpdated;

	public NewsCache() {
		_lastUpdated = new Date();
	}

	public Date getLastUpdated() {
		return _lastUpdated;
	}

	public void update() {
		_lastUpdated = new Date();
		_isCached = false;
		_spotlightNewsEntries = null;
		_newsEntries = null;
		_popularNewsEntries = null;
		_typedNewsEntries = null;
		_topTopStories = null;
		_categorizedTopNewsEntries = null;
		initialize();
	}

	public Map<String, Collection<NewsEntry>> getCategorizedTopNewsEntries() {
		initialize();
		return _categorizedTopNewsEntries;
	}

	private Collection<NewsEntry> getTypedEntriesList(String tID) {
		Collection<NewsEntry> newsEntries = null;
		newsEntries = _typedNewsEntries.get(tID);
		if (newsEntries == null) {
			newsEntries = new TreeSet<NewsEntry>(new NewsEntry.DescendingComparator());
			_typedNewsEntries.put(tID, newsEntries);
		}
		return newsEntries;
	}

	private Collection<NewsEntry> getCategorizedTopEntriesList(String cID) {
		Collection<NewsEntry> newsEntries = null;
		newsEntries = _categorizedTopNewsEntries.get(cID);
		if (newsEntries == null) {
			newsEntries = new TreeSet<NewsEntry>(new NewsEntry.DescendingComparator());
			_categorizedTopNewsEntries.put(cID, newsEntries);
		}
		return newsEntries;
	}

	@SuppressWarnings("unchecked")
	private synchronized void init() {
		_newsEntries = new TreeSet<NewsEntry>(new NewsEntry.DescendingComparator());
		_spotlightNewsEntries = new TreeSet<NewsEntry>(new NewsEntry.SpotlightComparator());
		_topTopStories = new TreeSet<NewsEntry>(new NewsEntry.TopStoryComparator());
		_typedNewsEntries = new HashMap<String, Collection<NewsEntry>>();
		_categorizedTopNewsEntries = new HashMap<String, Collection<NewsEntry>>();

		Database db = ExtLibUtil.getCurrentDatabase();
		View view = null;
		ViewNavigator navigator = null;

		try {
			view = db.getView("NewsModeratedCached");
			view.setAutoUpdate(false);
			view.resortView("NModerationDate");

			// Look for entries in the last month only
			DateTime today = ExtLibUtil.getCurrentSession().createDateTime("Today");
			DateTime dt = ExtLibUtil.getCurrentSession().createDateTime("Today");
			dt.adjustMonth(-1);
			ViewEntry entry = null;
			int failsafe = 0;
			while(entry == null && dt.timeDifference(today) < 0) {
				entry = view.getEntryByKey(dt);
				dt.adjustDay(1);

				// You never know when it will go haywire
				if(failsafe++ > 100) break;
			}
			dt.recycle();
			today.recycle();

			navigator = view.createViewNav();
			if(entry != null) navigator.gotoEntry(entry);

			while (entry != null) {
				entry.setPreferJavaDates(true);
				List<Object> columnValues = entry.getColumnValues();

				Date d1 = MiscUtils.getColumnValueAsDate(columnValues.get(4));
				Date d2 = MiscUtils.getColumnValueAsDate(columnValues.get(0));
				Date d3 = MiscUtils.getColumnValueAsDate(columnValues.get(15));
				Date d4 = MiscUtils.getColumnValueAsDate(columnValues.get(22));

				Double clicksTotalDouble = MiscUtils.getColumnValueAsDouble(columnValues.get(13));
				Double clicksLastWeekDouble = MiscUtils.getColumnValueAsDouble(columnValues.get(14));

				String spotlightImageURL = (String)columnValues.get(18);
				if(!StringUtil.isEmpty(spotlightImageURL)) {
					spotlightImageURL = entry.getUniversalID() + "/$file/" + spotlightImageURL;
				} else {
					spotlightImageURL = null;
				}
				NewsEntry newsEntry = new NewsEntry(
						(String)columnValues.get(8),	// NID
						(String)columnValues.get(1),	// TID
						(String)columnValues.get(2),	// NTitle
						(String)columnValues.get(3),	// PID
						(String)columnValues.get(5),	// NLink
						(String)columnValues.get(6),	// NImageURL
						(String)columnValues.get(7),	// NAbstract
						d1,					// NModerationDate
						d2,					// NPublicationDate
						(String)columnValues.get(9),	// NSpotlight
						(String)columnValues.get(10),	// NTopStory
						(String)columnValues.get(11),	// NTopStoryCategory
						(String)columnValues.get(12),	// NTopStoryPosition
						clicksTotalDouble,		// NClicksTotal
						clicksLastWeekDouble,		// NClicksLastWeek
						d3,					// NCreationDate
						(String)columnValues.get(16),	// NState
						(String)columnValues.get(17),	// NSpotlightSentence
						spotlightImageURL,		// SpotlightPicture
						(String)columnValues.get(19),	// NSpotlightPosition
						(String)columnValues.get(20),	// NModerator
						(String)columnValues.get(21),	// NLastEditor
						d4					// NLastModified
				);
				_newsEntries.add(newsEntry);
				if (newsEntry.isSpotlight())
					_spotlightNewsEntries.add(newsEntry);
				getTypedEntriesList(newsEntry.getTID()).add(newsEntry);
				if (newsEntry.isTopStory()) {
					if(newsEntry.getTopStoryCategory().equalsIgnoreCase("top")) {
						_topTopStories.add(newsEntry);
					} else {
						getCategorizedTopEntriesList(newsEntry.getTopStoryCategory()).add(newsEntry);
					}
				}

				ViewEntry tmpEntry = entry;
				entry = navigator.getNext();
				tmpEntry.recycle();
			}
		} catch (Throwable t) {
			MiscUtils.logException(t);
			return;
		} finally {
			MiscUtils.incinerate(navigator, view);
		}

		_isCached = true;
	}

	private void initialize() {
		if (_isCached)
			return;
		init();
	}

	public Collection<NewsEntry> getEntriesByPopularity() {
		initialize();
		if (_popularNewsEntries == null) {
			// Limit popular stories to the last week only
			Date weekAgo = new Date();
			weekAgo.setTime(weekAgo.getTime() - (7 * 24 * 60 * 60 * 1000));

			_popularNewsEntries = new ArrayList<NewsEntry>();
			for(NewsEntry n : _newsEntries) {
				if(n.getCreationDate().after(weekAgo)) {
					// TODO: figure out if cloning is actually necessary here
					_popularNewsEntries.add(n.clone());
				}
			}

			Collections.sort(_popularNewsEntries, new NewsEntry.PopularityComparator());
		}

		return _popularNewsEntries;
	}

	public Collection<NewsEntry> getEntries() {
		initialize();
		return _newsEntries;
	}

	public Collection<NewsEntry> getEntriesByType(String tID) {
		initialize();
		return getTypedEntriesList(tID);
	}

	public Collection<NewsEntry> getSpotlightEntries() {
		initialize();
		return _spotlightNewsEntries;
	}

	public Collection<NewsEntry> getTopStories(String cID) {
		initialize();
		return getCategorizedTopEntriesList(cID);
	}

	public Collection<NewsEntry> getTopTopStories() {
		initialize();
		return _topTopStories;
	}
}