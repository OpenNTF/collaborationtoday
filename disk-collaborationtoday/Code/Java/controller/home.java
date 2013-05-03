package controller;

import frostillicus.controller.BasicXPageController;
import java.util.*;
import org.openntf.news.http.core.ConfigCache;
import org.openntf.news.http.core.NewsEntry;
import org.openntf.news.http.core.Person;
import org.openntf.news.http.core.Type;
import com.ibm.commons.util.StringUtil;

import static org.openntf.news.http.core.MiscUtils.*;

public class home extends BasicXPageController {
	private static final long serialVersionUID = 1L;

	public String getPageTitle() {
		ConfigCache configCache = getConfigCache();

		String filter = getFilter();
		if(StringUtil.isEmpty(filter)) {
			return "Top Stories | Collaboration Today";
		} else if("all".equals(filter)) {
			return "Recent | Collaboration Today";
		} else if("popular".equals(filter)) {
			return "Popular | Collaboration Today";
		} else if(!"all".equals(filter) && !"popular".equals(filter)) {
			return configCache.getCategory(configCache.getType(filter).getCategoryId()).getDisplayName() + " - " + configCache.getType(filter).getDisplayName() + " | Collaboration Today";
		}
		return "";
	}
	public Type getCurrentType() {
		return getConfigCache().getType(getFilter());
	}
	public List<NewsEntry> getEntries() {
		return "all".equals(getFilter()) ? getNewsCache().getEntries() : getNewsCache().getEntriesByType(getFilter());
	}

	/*
	 * Repeat-specific methods
	 */
	public Person getEntryPerson() {
		return getPersonsCache().getPerson(((NewsEntry)resolveVariable("entryA")).getPID());
	}
	public String getEntryCategoryName() {
		try {
			NewsEntry entry = (NewsEntry)resolveVariable("entryA");
			ConfigCache configCache = getConfigCache();
			return configCache.getCategory(configCache.getType(entry.getTID()).getCategoryId()).getDisplayName();
		} catch(Exception e) {
			return "";
		}
	}
	public String getEntryTypeName() {
		try {
			NewsEntry entry = (NewsEntry)resolveVariable("entryA");
			return getConfigCache().getType(entry.getTID()).getDisplayName();
		} catch(Exception e) {
			return "";
		}
	}

	private String getFilter() {
		return getParam().get("filter");
	}
}
