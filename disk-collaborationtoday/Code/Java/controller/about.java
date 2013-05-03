package controller;

import java.util.*;
import frostillicus.controller.BasicXPageController;
import org.openntf.news.http.core.Category;
import org.openntf.news.http.core.Type;

import static org.openntf.news.http.core.MiscUtils.getConfigCache;

public class about extends BasicXPageController {
	private static final long serialVersionUID = 1L;

	public List<Type> getCategoryTypes() {
		Category category = (Category)resolveVariable("category");
		return getConfigCache().getTypesForCategory(category.getID());
	}
	public String getEntryModeratorsText() {
		Type type = (Type)resolveVariable("entry");
		// Geez, Java; would it kill you to have good string implosion?
		StringBuilder result = new StringBuilder();
		boolean appended = false;
		for(String mod : type.getModerators()) {
			if(appended) { result.append(", "); } else { appended = true; }
			result.append(mod);
		}
		return result.toString();
	}
}
