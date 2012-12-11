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
import javax.faces.context.FacesContext;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URLEncoder;

import com.ibm.domino.services.util.JsonWriter;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class NewsEntriesJson {

	public NewsEntriesJson() { }

	private String _count = DEFAULT_COUNT;
	private String _filter = FILTER_ALL;
	private String _format = FORMAT_JSONP;

	public static final String FORMAT_JSON = "json";
	public static final String FORMAT_JSONP = "jsonp";

	public static final String FILTER_TOP = "top";
	public static final String FILTER_ALL = "all";
	public static final String FILTER_POPULAR = "popular";
	public static final String FILTER_SPOTLIGHT = "spotlight";

	public static final String NEWS_ENTRY_ID = "id";
	public static final String NEWS_ENTRY_TYPE_DISPLAY_NAME = "type_display_name";
	public static final String NEWS_ENTRY_TYPE_ID = "type_id";
	public static final String NEWS_ENTRY_TITLE = "title";
	public static final String NEWS_ENTRY_PERSON_ID = "person_id";
	public static final String NEWS_ENTRY_PERSON_DISPLAY_NAME = "person_display_name";
	public static final String NEWS_ENTRY_LINK = "link";
	public static final String NEWS_ENTRY_IMAGE_URL = "image_url";
	public static final String NEWS_ENTRY_ABSTRACT_ENCODED = "abstract_encoded";
	public static final String NEWS_ENTRY_MODERATION_DATE = "moderation_date";
	public static final String NEWS_ENTRY_PUBLICATION_DATE = "publication_date";
	public static final String NEWS_ENTRY_IS_SPOTLIGHT = "is_spotlight";
	public static final String NEWS_ENTRY_SPOTLIGHT_SENTENCE = "spotlight_sentence";
	public static final String NEWS_ENTRY_IS_TOP_STORY = "is_top_story";
	public static final String NEWS_ENTRY_TOP_STORY_CATEGORY = "top_story_category";
	public static final String NEWS_ENTRY_TOP_STORY_POSITION = "top_story_position";
	public static final String NEWS_ENTRY_CLICKS_TOTAL = "clicks_total";
	public static final String NEWS_ENTRY_CLICKS_LAST_WEEK = "clicks_last_week";

	private static final String DEFAULT_COUNT = "10";

	public void setCount(String count) {
		_count = count == null || count.isEmpty() ? DEFAULT_COUNT : count;
	}
	public String getCount() {
		return _count;
	}
	public int getCountAsInt() {
		try {
			return Integer.parseInt(_count, 10);
		} catch (Exception e) {
			return Integer.parseInt(DEFAULT_COUNT, 10);
		}
	}

	public void setFormat(String format) {
		_format = FORMAT_JSON.equalsIgnoreCase(format) ? FORMAT_JSON : FORMAT_JSONP;
	}
	public String getFormat() {
		return _format;
	}

	public void setFilter(String filter) {
		_filter = filter == null || filter.isEmpty() ? FILTER_TOP : filter;
	}
	public String getFilter() {
		return _filter;
	}

	public String getJson() {
		StringWriter writer = new StringWriter();
		boolean compact = true;
		JsonWriter json = new JsonWriter(writer, compact);

		try {
			if (FORMAT_JSON.equalsIgnoreCase(_format)) {
				json.startArray();
			} else {
				writer.append("dojo.io.script.jsonp_dojoIoScript1._jsonpCallback(");
				json.startObject();
				json.startProperty("responseData");
				json.startObject();
				json.startProperty("results");
				json.startArray();
			}		

			FacesContext context = FacesContext.getCurrentInstance();
			NewsCache newsCache = (NewsCache)ExtLibUtil.resolveVariable(context, "newsCache");
			ConfigCache configCache = (ConfigCache)ExtLibUtil.resolveVariable(context, "configCache");
			PersonsCache personsCache = (PersonsCache)ExtLibUtil.resolveVariable(context, "personsCache");

			Collection<NewsEntry> newsEntries;

			if(FILTER_ALL.equalsIgnoreCase(_filter)) {
				newsEntries = (List<NewsEntry>)newsCache.getEntries();
			} else if(FILTER_TOP.equalsIgnoreCase(_filter)) {
				newsEntries = newsCache.getTopTopStories();
				if (newsEntries == null) {
					newsEntries = new ArrayList<NewsEntry>();
				}
				Map<String, Collection<NewsEntry>> categorizedTopNewsEntries = newsCache.getCategorizedTopNewsEntries();
				if (categorizedTopNewsEntries != null) {
					List<Category> categories = configCache.getCategories();
					if (categories != null) {
						for(Category category : categories) {
							Collection<NewsEntry> moreEntries = categorizedTopNewsEntries.get(category.getID());
							if (moreEntries != null) {
								newsEntries.addAll(moreEntries);
							}
						}
					}
				}
			} else if(FILTER_POPULAR.equalsIgnoreCase(_filter)) {
				newsEntries = newsCache.getEntriesByPopularity();
			} else if(FILTER_SPOTLIGHT.equalsIgnoreCase(_filter)) {
				newsEntries = newsCache.getSpotlightEntries();
			} else {
				newsEntries = newsCache.getEntriesByType(_filter);
			}

			if (newsEntries != null) {
				int amount = newsEntries.size();
				if (amount > getCountAsInt()) amount = getCountAsInt();

				for(NewsEntry entry : newsEntries) {

					json.startObject();
					writeProperty(json, NEWS_ENTRY_ID, entry.getID());
					writeProperty(json, NEWS_ENTRY_TYPE_DISPLAY_NAME, configCache.getType(entry.getTID()).getDisplayName());
					writeProperty(json, NEWS_ENTRY_TITLE, encode(entry.getTitle()));
					writeProperty(json, NEWS_ENTRY_PERSON_ID, entry.getPID());
					writeProperty(json, NEWS_ENTRY_PERSON_DISPLAY_NAME, personsCache.getPerson(entry.getPID()).getDisplayName());
					writeProperty(json, NEWS_ENTRY_LINK, entry.getLink());
					writeProperty(json, NEWS_ENTRY_IMAGE_URL, entry.getImageURL());
					writeProperty(json, NEWS_ENTRY_ABSTRACT_ENCODED, encode(entry.getAbstract()));
					writeProperty(json, NEWS_ENTRY_MODERATION_DATE, entry.getModerationDate());
					writeProperty(json, NEWS_ENTRY_PUBLICATION_DATE, entry.getPublicationDate());
					writeProperty(json, NEWS_ENTRY_IS_SPOTLIGHT, entry.isSpotlight());
					writeProperty(json, NEWS_ENTRY_SPOTLIGHT_SENTENCE, encode(entry.getSpotlightSentence()));
					writeProperty(json, NEWS_ENTRY_IS_TOP_STORY, entry.isTopStory());
					writeProperty(json, NEWS_ENTRY_TOP_STORY_CATEGORY, entry.getTopStoryCategory());
					writeProperty(json, NEWS_ENTRY_TOP_STORY_POSITION, entry.getTopStoryPosition());
					writeProperty(json, NEWS_ENTRY_CLICKS_TOTAL, entry.getClicksTotal());
					writeProperty(json, NEWS_ENTRY_CLICKS_LAST_WEEK, entry.getClicksLastWeek());
					writeProperty(json, NEWS_ENTRY_TYPE_ID, entry.getTID());
					json.endObject();

				}
			}
			if (FORMAT_JSON.equalsIgnoreCase(_format)) {
				json.endArray();
			} else {

				json.endArray();
				json.endObject();
				json.endProperty();
				json.startProperty("responseDetails");
				json.outNull();
				json.endProperty();
				json.startProperty("responseStatus");
				json.outIntLiteral(200);
				json.endProperty();
				json.endObject();
				writer.append(")");
			}
		} catch (Exception ne) {
			MiscUtils.logException(ne);
			if (FORMAT_JSON.equalsIgnoreCase(_format)) {
				return "[]";
			}
			else {
				return "dojo.io.script.jsonp_dojoIoScript1._jsonpCallback({'responseData': {'results': [], }, 'responseDetails': null,'responseStatus': 500})";
			}
		}

		return writer.toString();
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

	private void writeProperty(JsonWriter json, String propertyName, String value) throws IOException {
		json.startProperty(propertyName);
		json.outStringLiteral(value == null ? "" : value);
		json.endProperty();
	}
	private void writeProperty(JsonWriter json, String propertyName, int value) throws IOException {
		json.startProperty(propertyName);
		json.outIntLiteral(value);
		json.endProperty();
	}
	private void writeProperty(JsonWriter json, String propertyName, boolean value) throws IOException {
		json.startProperty(propertyName);
		json.outBooleanLiteral(value);
		json.endProperty();
	}
	private void writeProperty(JsonWriter json, String propertyName, Date value) throws Exception {
		json.startProperty(propertyName);
		if(value == null) { json.outNull(); } else { json.outStringLiteral(value.toString()); }
		json.endProperty();
	}
}
