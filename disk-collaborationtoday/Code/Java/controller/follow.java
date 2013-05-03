package controller;

import frostillicus.controller.BasicXPageController;
import org.openntf.news.http.core.Type;

import static org.openntf.news.http.core.MiscUtils.getConfigCache;

public class follow extends BasicXPageController {
	private static final long serialVersionUID = 1L;

	//configCache.getCategory(type.getCategoryId()).getDisplayName()
	public String getTypeCategoryName() {
		Type type = (Type)resolveVariable("type");
		return getConfigCache().getCategory(type.getCategoryId()).getDisplayName();
	}
}
