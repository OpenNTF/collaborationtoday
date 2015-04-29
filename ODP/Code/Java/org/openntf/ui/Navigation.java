package org.openntf.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Navigation implements Serializable {

	private static final long serialVersionUID = -4983110583704441736L;
	private List<Page> navigation;
	
	public Navigation(){
		navigation = new ArrayList<Page>();
		navigation.add(new Page("Home", "", "home.xsp"));
		navigation.add(new Page("Follow", "", "follow.xsp"));
		navigation.add(new Page("Contact", "", "contact.xsp"));
		navigation.add(new Page("About", "", "about.xsp"));
	}

	public List<Page> getNavigation() {
		return navigation;
	}

}
