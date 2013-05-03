package org.openntf.news.http.core;

import java.util.Date;
import java.util.Map;
import lotus.domino.*;
import com.ibm.xsp.extlib.social.SocialServicesFactory;
import javax.faces.context.FacesContext;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class MiscUtils {
	private MiscUtils() { }

	public static String getCurrentCommonName() throws NotesException {
		String userName = SocialServicesFactory.getInstance().getAuthenticatedUserId(FacesContext.getCurrentInstance());
		Name name = ExtLibUtil.getCurrentSession().createName(userName);
		userName = name.getCommon();
		name.recycle();

		return userName;
	}

	public static void logException(Throwable e) {
		// Eventually, this should store in a document or elsewhere. For now, print to the console
		e.printStackTrace();
	}

	// Methods for coaxing column values to desired data types, with DateTime recycling
	public static Date getColumnValueAsDate(Object columnValue) {
		try {
			if(columnValue instanceof DateTime) {
				DateTime dt = (DateTime)columnValue;
				Date date = dt.toJavaDate();
				dt.recycle();
				return date;
			} else if(columnValue instanceof Date) {
				return (Date)columnValue;
			}
		} catch(NotesException ne) { }
		return new Date();
	}
	public static Double getColumnValueAsDouble(Object columnValue) {
		try {
			if(columnValue instanceof DateTime) {
				((DateTime)columnValue).recycle();
				return 0d;
			} else if(columnValue instanceof Double) {
				return (Double)columnValue;
			}
		} catch(NotesException ne) { }
		return 0d;
	}

	public static ConfigCache getConfigCache() {
		FacesContext context = FacesContext.getCurrentInstance();
		return (ConfigCache)context.getApplication().getVariableResolver().resolveVariable(context, "configCache");
	}
	public static NewsCache getNewsCache() {
		FacesContext context = FacesContext.getCurrentInstance();
		return (NewsCache)context.getApplication().getVariableResolver().resolveVariable(context, "newsCache");
	}
	public static PersonsCache getPersonsCache() {
		FacesContext context = FacesContext.getCurrentInstance();
		return (PersonsCache)context.getApplication().getVariableResolver().resolveVariable(context, "personsCache");
	}
	@SuppressWarnings("unchecked")
	public static Map<String, String> getParam() {
		FacesContext context = FacesContext.getCurrentInstance();
		return (Map<String, String>)context.getApplication().getVariableResolver().resolveVariable(context, "param");
	}


	public static void incinerate(Base... domObjects) {
		for(Base domObject : domObjects) {
			if(domObject != null) {
				try { domObject.recycle(); } catch(NotesException ne) { }
			}
		}
	}
}
