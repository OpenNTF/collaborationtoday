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
	private List<Page> footer;

	public Navigation() {
		initNav();
		initFooter();
	}

	private void initNav() {
		navigation = new ArrayList<Page>();
		navigation.add(new Page("Home", "", "home.xsp"));
		navigation.add(new Page("Follow", "", "follow.xsp"));
		navigation.add(new Page("Contact", "", "contact.xsp"));
		navigation.add(new Page("About", "", "about.xsp"));

		ConfigCache configBean = (ConfigCache) ExtLibUtil.resolveVariable(FacesContext.getCurrentInstance(), "configCache");
		try {
			if (configBean.isUserModerator(ExtLibUtil.getCurrentSession().getEffectiveUserName())) {
				navigation.add(new Page("Moderation", "", "add.xsp"));
			}
		} catch (NotesException e) {
			e.printStackTrace();
		}
	}

	private void initFooter() {
		footer = new ArrayList<Page>();
		footer.add(new Page("Hosted by OpenNTF", "", "http://www.openntf.org", true));
		footer.add(new Page("Terms of use", "", "http://openntf.org/legal/terms"));
		footer.add(new Page("Privacy Policy", "", "http://openntf.org/legal/privacypolicy"));
		footer.add(new Page("Contact OpenNTF", "", "http://www.openntf.org/main.nsf/page.xsp?name=Get_Involved"));
		footer.add(new Page("Follow OpenNTF", "", "http://www.openntf.org/main.nsf/blogsAll.xsp"));
		try {
			if (ExtLibUtil.getCurrentSession().getEffectiveUserName().equals("Anonymous")) {
				footer.add(new Page("Login", "", "login.xsp"));
			} else {
				// render if logged in
				footer.add(new Page("Logout", "", "/" + ExtLibUtil.getCurrentDatabase().getFilePath() + "?logout&redirectto=" + "/" + ExtLibUtil.getCurrentDatabase().getFilePath()));
			}
		} catch (NotesException e) {
			e.printStackTrace();
		}

	}

	public List<Page> getNavigation() {
		return navigation;
	}

	public List<Page> getFooter() {
		return footer;
	}

}
