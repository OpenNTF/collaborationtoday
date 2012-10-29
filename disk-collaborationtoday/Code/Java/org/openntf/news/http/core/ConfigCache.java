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
import java.util.Iterator;
import java.util.Vector;
import java.util.HashMap;
import java.util.List;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ACL;
import lotus.domino.ACLEntry;
import lotus.domino.ViewEntry;
import lotus.domino.ViewNavigator;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class ConfigCache {

	private boolean _isCached = false;
	private ArrayList<Type> _types;
	private ArrayList<Category> _categories;
	private String _captchaPublicKey;
	private String _captchaPrivateKey;
	private String _analyticsJS;

	public ConfigCache() {
	}  

	public void update() {
		_isCached = false;
		_types = null;
		_categories = null;
		_captchaPublicKey = "";
		_captchaPrivateKey = "";
		_analyticsJS = "";
		initialize();
	}

	public ArrayList<Type> getTypes() {
		initialize();
		return _types;
	}

	public ArrayList<Category> getCategories() {
		initialize();
		return _categories;
	}

	public void setCaptchaPublicKey(String captchaPublicKey) {
		_captchaPublicKey = captchaPublicKey;
	}

	public String getAnalyticsJS() {
		return _analyticsJS;
	}

	public void setCaptchaPrivateKey(String captchaPrivateKey) {
		_captchaPrivateKey = captchaPrivateKey;
	}

	public String getCaptchaPublicKey() {
		return _captchaPublicKey;
	}

	public String getCaptchaPrivateKey() {
		return _captchaPrivateKey;
	}

	@SuppressWarnings("unchecked")
	private synchronized void init() {
		_types = new ArrayList<Type>();
		_categories = new ArrayList<Category>();
		_captchaPublicKey = "";
		_captchaPrivateKey = "";
		_analyticsJS = "";

		Database db = ExtLibUtil.getCurrentDatabase();
		View viewTypesAll = null;
		View viewCategoriesAll = null;
		View viewConfigAll = null;
		ViewNavigator viewNavigatorTypes = null;
		ViewNavigator viewNavigatorConfig = null;
		ViewNavigator viewNavigatorCategories = null;

		try {
			viewTypesAll = db.getView("TypesAll");
			viewTypesAll.setAutoUpdate(false);
			viewNavigatorTypes = viewTypesAll.createViewNav();
			ViewEntry tmpEntry;
			ViewEntry entry = viewNavigatorTypes.getFirst();
			while (entry != null) {
				if (entry.isCategory() == false) {
					entry.setPreferJavaDates(true);
					List<Object> columnValues = entry.getColumnValues();

					Vector<Object> moderators = new Vector<Object>();
					Document doc = entry.getDocument();
					Item moderatorsItem = doc.getFirstItem("TModerators");
					if (moderatorsItem != null) moderators = moderatorsItem.getValues();
					Type type = new Type((String)columnValues.get(2),
							(String)columnValues.get(3),
							moderators,
							(String)columnValues.get(4),
							(String)columnValues.get(0),
							(String)columnValues.get(5));
					_types.add(type);
				}

				tmpEntry = viewNavigatorTypes.getNext();
				entry.recycle();
				entry = tmpEntry;
			}				

			viewCategoriesAll = db.getView("CategoriesAll");
			viewCategoriesAll.setAutoUpdate(false);
			viewNavigatorCategories = viewCategoriesAll.createViewNav();
			entry = viewNavigatorCategories.getFirst();
			while (entry != null) {
				if (entry.isCategory() == false) {
					entry.setPreferJavaDates(true);
					List<Object> columnValues = entry.getColumnValues();

					Category category = new Category((String)columnValues.get(1),
							(String)columnValues.get(2),
							(String)columnValues.get(3));
					_categories.add(category);
				}

				tmpEntry = viewNavigatorCategories.getNext();
				entry.recycle();
				entry = tmpEntry;
			}	

			viewConfigAll = db.getView("ConfigAll");
			viewConfigAll.setAutoUpdate(false);
			viewNavigatorConfig = viewConfigAll.createViewNav();
			entry = viewNavigatorConfig.getFirst();
			if (entry != null) {
				Document doc = entry.getDocument();
				if (doc != null) {
					setCaptchaPublicKey(doc.getItemValueString("COCaptchaPublicKey"));
					setCaptchaPrivateKey(doc.getItemValueString("COCaptchaPrivateKey"));
					_analyticsJS = doc.getItemValueString("COAnalytics");
					doc.recycle();
				}				
				entry.recycle();		        
			}	
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return;
		}
		finally {
			try {
				if (viewNavigatorTypes != null) {
					viewNavigatorTypes.recycle();
				}
				if (viewNavigatorCategories != null) {
					viewNavigatorCategories.recycle();
				}
				if (viewTypesAll != null) {
					viewTypesAll.recycle();
				}	
				if (viewConfigAll != null) {
					viewConfigAll.recycle();
				}	
				if (viewCategoriesAll != null) {
					viewCategoriesAll.recycle();
				}	
			}
			catch (NotesException e) {
				e.printStackTrace();
				return;
			}
		}
		_isCached = true;		
	}

	private void initialize() {
		if (_isCached) return;
		init();
	}


	public Type getType(String tID) {
		initialize();
		if (_types == null) return Type.getEmptyType();
		for(Type type : _types) {
			if (type.getID().equalsIgnoreCase(tID)) return type;
		}
		return Type.getEmptyType();
	}

	public Category getCategory(String cID) {
		initialize();
		if (_categories == null) return Category.getEmptyCategory();
		for(Category category : _categories) {
			if (category.getID().equalsIgnoreCase(cID)) return category;
		}
		return Category.getEmptyCategory();
	}

	public Vector<String> getTypesForCurrentUserCombobox() {
		Vector<String> output = null;
		try {
			String userName; 
			userName = com.ibm.xsp.extlib.social.SocialServicesFactory.getInstance().getAuthenticatedUserId(javax.faces.context.FacesContext.getCurrentInstance());
			Name name = com.ibm.xsp.extlib.util.ExtLibUtil.getCurrentSession().createName(userName);
			userName = name.getCommon();
			initialize();  

			output = new Vector<String>();
			for(Type type : _types) {
				Vector moderators = type.getModerators();
				if (moderators != null) {
					if (moderators.contains(userName)) {
						Category category = getCategory(type.getCategoryId());
						if (category != null)
							output.add(category.getDisplayName() + " - " + type.getDisplayName() + "|" + type.getID());
					}
				}
			}
		} catch (NotesException e) { }
		return output;
	}

	public Vector getCategoriesCombobox() {		
		Vector output = new Vector();
		output.add("Top|top");
		initialize();  
		if (_categories != null) {
			Iterator it = _categories.iterator();
			for (; it.hasNext();) {
				Category category = (Category)it.next();
				output.add(category.getDisplayName() + "|" + category.getID());
			}
		}
		return output;
	}

	public Vector getTypesForCurrentUser() {
		String user;
		Vector output = null;
		try {
			String userName; 
			userName = com.ibm.xsp.extlib.social.SocialServicesFactory.getInstance().getAuthenticatedUserId(javax.faces.context.FacesContext.getCurrentInstance());
			Name name = com.ibm.xsp.extlib.util.ExtLibUtil.getCurrentSession().createName(userName);
			userName = name.getCommon();
			initialize();  

			output = new Vector();
			Iterator it = _types.iterator();
			for (; it.hasNext();) {
				Type type = (Type)it.next();
				Vector moderators = type.getModerators();
				if (moderators != null) {
					if (moderators.contains(userName))
						output.add(type);
				}
			}
		}
		catch (NotesException e) {
		}
		return output;
	}

	public Vector getTypesForCategory(String categoryId) {		
		initialize();
		Vector output = new Vector();
		if (_types == null) return output;
		Iterator it = _types.iterator();
		for (; it.hasNext();) {
			Type type = (Type)it.next();        	
			if (categoryId.equalsIgnoreCase(type.getCategoryId())) {
				output.add(type);
			}
		}
		return output;		
	}

	private HashMap<String, Boolean> _moderators;
	private HashMap<String, Boolean> _topStoriesModerators;
	private HashMap<String, Boolean> _spotlightModerators;
	private HashMap<String, Boolean> _avatarModerators;

	public boolean isUserAvatarModerator(String user) {
		if (_avatarModerators == null) _avatarModerators = new HashMap<String, Boolean>();
		Boolean isModerator = _avatarModerators.get(user);
		if (isModerator == null) {
			isModerator = new Boolean(hasUserRole(user, "[AvatarModerator]"));
			_avatarModerators.put(user, isModerator);
		}
		return isModerator.booleanValue();
	}

	public boolean isUserModerator(String user) {
		if (_moderators == null) _moderators = new HashMap<String, Boolean>();
		Boolean isModerator = _moderators.get(user);
		if (isModerator == null) {
			isModerator = new Boolean(hasUserRole(user, "[Moderator]"));
			_moderators.put(user, isModerator);
		}
		return isModerator.booleanValue();
	}

	public boolean isUserTopStoriesModerator(String user) {
		if (_topStoriesModerators == null) _topStoriesModerators = new HashMap<String, Boolean>();
		Boolean isModerator = _topStoriesModerators.get(user);
		if (isModerator == null) {
			isModerator = new Boolean(hasUserRole(user, "[TopModerator]"));
			_topStoriesModerators.put(user, isModerator);
		}
		return isModerator.booleanValue();
	}

	public boolean isUserSpotlightModerator(String user) {
		if (_spotlightModerators == null) _spotlightModerators = new HashMap<String, Boolean>();
		Boolean isModerator = _spotlightModerators.get(user);
		if (isModerator == null) {
			isModerator = new Boolean(hasUserRole(user, "[SpotModerator]"));
			_spotlightModerators.put(user, isModerator);
		}
		return isModerator.booleanValue();
	}

	private boolean hasUserRole(String user, String roleName) {
		boolean output = false;
		Vector roles = getUserRoles(user);
		if (roles != null) {
			Iterator it = roles.iterator();
			for (; it.hasNext();) {
				String role = (String)it.next();        	
				if (role.equalsIgnoreCase(roleName)) {
					output = true;
				}
			}
		}
		return output;
	}

	private Vector getUserRoles(String user) {
		Vector roles = null;
		Database db = ExtLibUtil.getCurrentDatabase();
		try {
			ACL acl = db.getACL();
			if (acl != null) {
				ACLEntry entry = acl.getEntry(user);
				if (entry != null) {
					roles = entry.getRoles();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return roles;
	}
}
