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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewNavigator;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class NewsCache {

	private boolean _isCached = false;
	private List<NewsEntry> _newsEntries;
	private List<NewsEntry> _spotlightNewsEntries;
	private List<NewsEntry> _topTopStories;
	private List<NewsEntry> _popularNewsEntries;
	private Map<String, List<NewsEntry>> _typedNewsEntries;
	private Map<String, List<NewsEntry>> _categorizedTopNewsEntries;
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

	public Map<String, List<NewsEntry>> getCategorizedTopNewsEntries() {
		initialize();
		return _categorizedTopNewsEntries;
	}

	private ArrayList<NewsEntry> getTypedEntriesList(String tID) {
		ArrayList<NewsEntry> newsEntries = null;
		newsEntries = (ArrayList<NewsEntry>) _typedNewsEntries.get(tID);
		if (newsEntries == null) {
			newsEntries = new ArrayList<NewsEntry>();
			_typedNewsEntries.put(tID, newsEntries);
		}
		return newsEntries;
	}

	private ArrayList<NewsEntry> getCategorizedTopEntriesList(String cID) {
		ArrayList<NewsEntry> newsEntries = null;
		newsEntries = (ArrayList<NewsEntry>) _categorizedTopNewsEntries
		.get(cID);
		if (newsEntries == null) {
			newsEntries = new ArrayList<NewsEntry>();
			_categorizedTopNewsEntries.put(cID, newsEntries);
		}
		return newsEntries;
	}

	@SuppressWarnings("unchecked")
	private synchronized void init() {
		_newsEntries = new ArrayList<NewsEntry>();
		_spotlightNewsEntries = new ArrayList<NewsEntry>();
		_topTopStories = new ArrayList<NewsEntry>();
		_typedNewsEntries = new HashMap<String, List<NewsEntry>>();
		_categorizedTopNewsEntries = new HashMap<String, List<NewsEntry>>();

		Database db = ExtLibUtil.getCurrentDatabase();
		View view = null;
		ViewNavigator navigator = null;
		FacesContext context = FacesContext.getCurrentInstance();
		ConfigCache config = (ConfigCache) context.getApplication()
		.getVariableResolver().resolveVariable(context, "configCache");

		try {
			view = db.getView("NewsModeratedCached");
			view.setAutoUpdate(false);
			navigator = view.createViewNav();
			ViewEntry tmpEntry;
			ViewEntry entry = navigator.getFirst();
			while (entry != null) {
				try {
					entry.setPreferJavaDates(true);
					List<Object> columnValues = entry.getColumnValues();

					Date d1 = MiscUtils.getColumnValueAsDate(columnValues.get(4));
					Date d2 = MiscUtils.getColumnValueAsDate(columnValues.get(0));
					Date d3 = MiscUtils.getColumnValueAsDate(columnValues.get(15));
					Date d4 = MiscUtils.getColumnValueAsDate(columnValues.get(22));

					Double clicksTotalDouble = MiscUtils.getColumnValueAsDouble(columnValues.get(13));
					Double clicksLastWeekDouble = MiscUtils.getColumnValueAsDouble(columnValues.get(14));

					String spotlightImageURL = (String)columnValues.get(18);
					if (spotlightImageURL != null) {
						if (!spotlightImageURL.equals("")) {
							spotlightImageURL = entry.getUniversalID()
							+ "/$file/" + spotlightImageURL;
						} else {
							spotlightImageURL = null;
						}
					}
					NewsEntry newsEntry = new NewsEntry(
							(String)columnValues.get(8),
							(String)columnValues.get(1),
							(String)columnValues.get(2),
							(String)columnValues.get(3),
							(String)columnValues.get(5),
							(String)columnValues.get(6),
							(String)columnValues.get(7),
							d1,
							d2,
							(String)columnValues.get(9),
							(String)columnValues.get(10),
							(String)columnValues.get(11),
							(String)columnValues.get(12),
							clicksTotalDouble,
							clicksLastWeekDouble,
							d3,
							(String)columnValues.get(16),
							(String)columnValues.get(17),
							spotlightImageURL,
							(String)columnValues.get(19),
							(String)columnValues.get(20),
							(String)columnValues.get(21),
							d4
					);
					_newsEntries.add(newsEntry);
					if (newsEntry.isSpotlight())
						_spotlightNewsEntries.add(newsEntry);
					getTypedEntriesList(newsEntry.getTID()).add(newsEntry);
					if (newsEntry.isTopStory()) {
						if(newsEntry.getTopStoryCategory().equalsIgnoreCase("top")) {
							_topTopStories.add(newsEntry);
						} else {
							getCategorizedTopEntriesList(
									newsEntry.getTopStoryCategory()).add(
											newsEntry);
						}
					}
				} catch (Exception e) {
				}

				tmpEntry = navigator.getNext();
				entry.recycle();
				entry = tmpEntry;
			}
			_spotlightNewsEntries = sortSpotlightStories(_spotlightNewsEntries);
			_topTopStories = sortTopStories(_topTopStories);
			if (_categorizedTopNewsEntries != null) {
				List<Category> categories = config.getCategories();
				if (categories != null) {
					Iterator it = categories.iterator();
					for (; it.hasNext();) {
						Category category = (Category) it.next();
						List<NewsEntry> catTopNews = _categorizedTopNewsEntries.get(category.getID());
						catTopNews = sortTopStories(catTopNews);
						_categorizedTopNewsEntries.put(category.getID(),
								catTopNews);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} catch (Throwable t) {
			t.printStackTrace();
			return;
		} finally {
			try {
				if (navigator != null) {
					navigator.recycle();
				}
				if (view != null) {
					view.recycle();
				}
			} catch (NotesException e) {
				e.printStackTrace();
				return;
			}
		}

		_isCached = true;
	}

	private void initialize() {
		if (_isCached)
			return;
		init();
	}

	public List<NewsEntry> getEntriesByPopularity() {
		initialize();
		if (_popularNewsEntries != null)
			return _popularNewsEntries;
		Map<String, NewsEntry> news = new HashMap<String, NewsEntry>();

		_popularNewsEntries = new ArrayList<NewsEntry>();
		for (int i = 0; i < _newsEntries.size(); i++) {
			NewsEntry n = _newsEntries.get(i);
			NewsEntry copy = new NewsEntry(n.getID(), n.getTID(), n.getTitle(),
					n.getPID(), n.getLink(), n.getImageURL(), n.getAbstract(),
					n.getModerationDate(), n.getPublicationDate(), n
					.isSpotlight(), n.isTopStory(), n
					.getTopStoryCategory(), n.getTopStoryPosition(), n
					.getClicksTotal(), n.getClicksLastWeek(), n
					.getCreationDate(), n.getState(), n
					.getSpotlightSentence(), n.getSpotlightImageURL(),
					n.getSpotlightPosition(), n.getModerator(), n
					.getLastEditor(), n.getLastModified());
			news.put(copy.getID(), copy);
		}

		PopularityComparator comparator = new PopularityComparator(news);
		TreeMap<String, NewsEntry> sortedNews = new TreeMap<String, NewsEntry>(comparator);
		sortedNews.putAll(news);

		for (String key : sortedNews.keySet()) {
			NewsEntry ne = (NewsEntry) news.get(key);
			_popularNewsEntries.add(ne);
		}

		return _popularNewsEntries;
	}

	public List<NewsEntry> getEntries() {
		initialize();
		return _newsEntries;
	}

	public List<NewsEntry> getEntriesByType(String tID) {
		initialize();
		return getTypedEntriesList(tID);
	}

	public List<NewsEntry> getSpotlightEntries() {
		initialize();
		return _spotlightNewsEntries;
	}

	public NewsEntry getSpotlightEntry(int position) {
		List<NewsEntry> entries = getSpotlightEntries();
		if (entries == null)
			return null;
		if (position < entries.size())
			return entries.get(position);
		return null;
	}

	public List<NewsEntry> getTopStories(String cID) {
		initialize();
		return getCategorizedTopEntriesList(cID);
	}

	public List<NewsEntry> getTopTopStories() {
		initialize();
		return _topTopStories;
	}

	private List<NewsEntry> sortTopStories(List<NewsEntry> newsEntries) {
		List<NewsEntry> output = new ArrayList<NewsEntry>();
		if (newsEntries == null)
			return null;
		for (int i = 0; i < newsEntries.size(); i++) {
			NewsEntry newsEntry = newsEntries.get(i);
			if (newsEntry.getTopStoryPosition() == 1)
				output.add(newsEntry);
		}
		for (int i = 0; i < newsEntries.size(); i++) {
			NewsEntry newsEntry = newsEntries.get(i);
			if (newsEntry.getTopStoryPosition() == 2)
				output.add(newsEntry);
		}
		for (int i = 0; i < newsEntries.size(); i++) {
			NewsEntry newsEntry = newsEntries.get(i);
			if (newsEntry.getTopStoryPosition() == 3)
				output.add(newsEntry);
		}
		for (int i = 0; i < newsEntries.size(); i++) {
			NewsEntry newsEntry = newsEntries.get(i);
			if (newsEntry.getTopStoryPosition() == 4)
				output.add(newsEntry);
		}
		return output;
	}

	private List<NewsEntry> sortSpotlightStories(
			List<NewsEntry> newsEntries) {
		List<NewsEntry> output = new ArrayList<NewsEntry>();
		for (int i = 0; i < newsEntries.size(); i++) {
			NewsEntry newsEntry = newsEntries.get(i);
			if (newsEntry.getSpotlightPosition() == 1)
				output.add(newsEntry);
		}
		for (int i = 0; i < newsEntries.size(); i++) {
			NewsEntry newsEntry = newsEntries.get(i);
			if (newsEntry.getSpotlightPosition() == 2)
				output.add(newsEntry);
		}
		for (int i = 0; i < newsEntries.size(); i++) {
			NewsEntry newsEntry = newsEntries.get(i);
			if (newsEntry.getSpotlightPosition() == 3)
				output.add(newsEntry);
		}
		for (int i = 0; i < newsEntries.size(); i++) {
			NewsEntry newsEntry = newsEntries.get(i);
			if (newsEntry.getSpotlightPosition() == 4)
				output.add(newsEntry);
		}
		return output;
	}
}