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

import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import java.net.URLEncoder;

public class NewsEntriesJson {
	public NewsEntriesJson() {
		_count = DEFAULT_COUNT;
		_format = FORMAT_JSONP;
		_filter = FILTER_ALL;
	}

	private String _count;
	private String _filter;
	private String _format;

	public String FORMAT_JSON = "json";
	public String FORMAT_JSONP = "jsonp";

	public String FILTER_TOP = "top";
	public String FILTER_ALL = "all";
	public String FILTER_POPULAR = "popular";
	public String FILTER_SPOTLIGHT = "spotlight";

	public String NEWS_ENTRY_ID = "id";
	public String NEWS_ENTRY_TYPE_DISPLAY_NAME = "type_display_name";
	public String NEWS_ENTRY_TYPE_ID = "type_id";
	public String NEWS_ENTRY_TITLE = "title";
	public String NEWS_ENTRY_PERSON_ID = "person_id";
	public String NEWS_ENTRY_PERSON_DISPLAY_NAME = "person_display_name";
	public String NEWS_ENTRY_LINK = "link";
	public String NEWS_ENTRY_IMAGE_URL = "image_url";
	public String NEWS_ENTRY_ABSTRACT_ENCODED = "abstract_encoded";
	public String NEWS_ENTRY_MODERATION_DATE = "moderation_date";
	public String NEWS_ENTRY_PUBLICATION_DATE = "publication_date";
	public String NEWS_ENTRY_IS_SPOTLIGHT = "is_spotlight";
	public String NEWS_ENTRY_SPOTLIGHT_SENTENCE = "spotlight_sentence";
	public String NEWS_ENTRY_IS_TOP_STORY = "is_top_story";
	public String NEWS_ENTRY_TOP_STORY_CATEGORY = "top_story_category";
	public String NEWS_ENTRY_TOP_STORY_POSITION = "top_story_position";
	public String NEWS_ENTRY_CLICKS_TOTAL = "clicks_total";
	public String NEWS_ENTRY_CLICKS_LAST_WEEK = "clicks_last_week";

	private final String DEFAULT_COUNT = "10";

	public void setCount(String count) {
		_count = count;
		if (_count == null) {
			_count = DEFAULT_COUNT;
		} else {
			if (count.equalsIgnoreCase(""))
				_count = DEFAULT_COUNT;
		}
	}

	public String getCount() {
		return _count;
	}

	public int getCountAsInt() {
		try {
			Integer i = new Integer(_count);
			return i.intValue();
		} catch (Exception e) {
			Integer i = new Integer(DEFAULT_COUNT);
			return i.intValue();
		}
	}

	public void setFormat(String format) {
		_format = FORMAT_JSONP;
		if (format == null)
			return;
		if (format.equalsIgnoreCase(FORMAT_JSON)) {
			_format = FORMAT_JSON;
		}
	}

	public String getFormat() {
		return _format;
	}

	public void setFilter(String filter) {
		if (filter == null) {
			_filter = FILTER_TOP;
		} else {
			if (filter.equalsIgnoreCase("")) {
				_filter = FILTER_TOP;
			} else {
				_filter = filter;
			}
		}
	}

	public String getFilter() {
		return _filter;
	}

	public String getJson() {
		String output;

		if (_format.equalsIgnoreCase(FORMAT_JSON)) {
			output = "[";
		} else {
			output = "dojo.io.script.jsonp_dojoIoScript1._jsonpCallback({'responseData': {'results': [";	
		}		

		FacesContext context = FacesContext.getCurrentInstance();
		NewsCache newsCache = (NewsCache) context.getApplication()
		.getVariableResolver().resolveVariable(context, "newsCache");
		ConfigCache configCache = (ConfigCache) context.getApplication().getVariableResolver().resolveVariable(context, "configCache");
		PersonsCache personsCache = (PersonsCache) context.getApplication().getVariableResolver().resolveVariable(context, "personsCache");
		try {
			List<NewsEntry> newsEntries;

			if (_filter.equalsIgnoreCase(FILTER_ALL)) {
				newsEntries = newsCache.getEntries();
			} else {
				if (_filter.equalsIgnoreCase(FILTER_TOP)) {
					newsEntries = newsCache.getEntries();

					newsEntries = newsCache.getTopTopStories();
					if (newsEntries == null) {
						newsEntries = new java.util.ArrayList<NewsEntry>();
					}
					List<NewsEntry> moreEntries;					
					Map<String, List<NewsEntry>> categorizedTopNewsEntries = newsCache.getCategorizedTopNewsEntries();
					if (categorizedTopNewsEntries != null) {
						List<Category> categories = configCache.getCategories();
						if (categories != null) {
							for(Category category : categories) {
								moreEntries = categorizedTopNewsEntries.get(category.getID());
								if (moreEntries != null) {
									for (int a = 0; a < moreEntries.size(); a++) {
										newsEntries.add(moreEntries.get(a));
									}
								}					        	
							}
						}
					}
				} else {
					if (_filter.equalsIgnoreCase(FILTER_POPULAR)) {
						newsEntries = newsCache.getEntriesByPopularity();
					} else {
						if (_filter.equalsIgnoreCase(FILTER_SPOTLIGHT)) {
							newsEntries = newsCache.getSpotlightEntries();
						} else {
							newsEntries = newsCache.getEntriesByType(_filter);
						}
					}
				}
			}

			if (newsEntries != null) {
				int amount = newsEntries.size();
				if (amount > getCountAsInt()) amount = getCountAsInt();

				for (int i = 0; i < amount; i++) {
					NewsEntry entry = newsEntries.get(i);					

					output = output + "{" + 
					"'" + NEWS_ENTRY_ID + "': '" + entry.getID() + "', " + 
					"'" + NEWS_ENTRY_TYPE_DISPLAY_NAME+ "': '" + configCache.getType(entry.getTID()).getDisplayName() + "', " +
					"'" + NEWS_ENTRY_TITLE + "': '" + encode(entry.getTitle()) + "', " + 
					"'" + NEWS_ENTRY_PERSON_ID + "': '" + entry.getPID() + "', " +
					"'" + NEWS_ENTRY_PERSON_DISPLAY_NAME+ "': '" + personsCache.getPerson(entry.getPID()).getDisplayName() + "', " +
					"'" + NEWS_ENTRY_LINK + "': '" + entry.getLink() + "', " +
					"'" + NEWS_ENTRY_IMAGE_URL + "': '" + entry.getImageURL() + "', " +
					"'" + NEWS_ENTRY_ABSTRACT_ENCODED + "': '" + encode(entry.getAbstract()) + "', " + 
					"'" + NEWS_ENTRY_MODERATION_DATE + "': '" + entry.getModerationDate() + "', " +
					"'" + NEWS_ENTRY_PUBLICATION_DATE + "': '" + entry.getPublicationDate() + "', " +
					"'" + NEWS_ENTRY_IS_SPOTLIGHT + "': " + entry.isSpotlight() + ", " +
					"'" + NEWS_ENTRY_SPOTLIGHT_SENTENCE + "': '" + encode(entry.getSpotlightSentence()) + "', " +
					"'" + NEWS_ENTRY_IS_TOP_STORY + "': " + entry.isTopStory() + ", " +
					"'" + NEWS_ENTRY_TOP_STORY_CATEGORY + "': '" + entry.getTopStoryCategory() + "', " +
					"'" + NEWS_ENTRY_TOP_STORY_POSITION + "': " + entry.getTopStoryPosition() + ", " +
					"'" + NEWS_ENTRY_CLICKS_TOTAL + "': " + entry.getClicksTotal() + ", " +
					"'" + NEWS_ENTRY_CLICKS_LAST_WEEK + "': " + entry.getClicksLastWeek() + ", " +
					"'" + NEWS_ENTRY_TYPE_ID+ "': '" + entry.getTID() + "'},";
				}
			}
			if (_format.equalsIgnoreCase(FORMAT_JSON)) {
				output = output + "]";
			}
			else {
				output = output
				+ "], }, 'responseDetails': null,'responseStatus': 200})";	
			}			
		} catch (Exception ne) {
			ne.printStackTrace();
			if (_format.equalsIgnoreCase(FORMAT_JSON)) {
				return "[]";
			}
			else {
				return "dojo.io.script.jsonp_dojoIoScript1._jsonpCallback({'responseData': {'results': [], }, 'responseDetails': null,'responseStatus': 500})";
			}
		}

		return output;
	}

	private String encode(String toBeEncoded) {	
		if (toBeEncoded == null) return "";
		String output = null;
		try {
			output = URLEncoder.encode(toBeEncoded, "UTF-8").replaceAll("\\+", "%20");	      
			output = output.trim();
		}
		catch (Exception e) {
			output = toBeEncoded.trim();
		}
		return output;
	}
}
