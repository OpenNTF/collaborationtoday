package org.openntf.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import lotus.domino.NotesException;

import org.openntf.news.http.core.ConfigCache;

import com.ibm.xsp.extlib.util.ExtLibUtil;

public class Navigation implements Serializable {

	private static final long serialVersionUID = -4983110583704441736L;
	private List<Page> navigation;
	
	public Navigation(){
		navigation = new ArrayList<Page>();
		navigation.add(new Page("Home", "", "home.xsp"));
		navigation.add(new Page("Follow", "", "follow.xsp"));
		navigation.add(new Page("Contact", "", "contact.xsp"));
		navigation.add(new Page("About", "", "about.xsp"));
		
		ConfigCache configBean = (ConfigCache)ExtLibUtil.resolveVariable(FacesContext.getCurrentInstance(), "configCache");
		try {
			if(configBean.isUserModerator(ExtLibUtil.getCurrentSession().getEffectiveUserName())){
				navigation.add(new Page("Moderation", "", "add.xsp"));
			}
		} catch (NotesException e) {
			e.printStackTrace();
		}
	}

	public List<Page> getNavigation() {
		return navigation;
	}

}
