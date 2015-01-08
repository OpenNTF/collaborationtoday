/*
 * © Copyright 2012 ZetaOne Solutions Group, LLC
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
 */
package com.ZetaOne.mypic;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.context.FacesContext;

import lotus.domino.Session;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;

import com.ibm.xsp.component.UIViewRootEx2;
import com.ibm.xsp.designer.context.XSPContext;

import com.ZetaOne.mypic.UNIDEncoder;

/**
 * 
 * @author jeremy_hodge
 * 
 *         The ApplicationSettings class is responsible for loading, verifying,
 *         and caching application settings. If the settings are missing or
 *         deemed to be invalid, the user is redirected to the configuration
 *         page.
 * 
 */
public class ApplicationSettings implements Serializable {

	private static final long serialVersionUID = -1427610981266873594L;

	protected String SaveUrlInNab;
	protected String EnableProfiles;
	protected String StoreProfilesInNab;
	protected String NabServer;
	protected String NabFilePath;
	protected String ShowApiInNav;
	protected String DefaultFormat;
	protected String DefaultSize;
	protected boolean validConfig;

	protected ArrayList<String> Messages;

	public ApplicationSettings() {

		Messages = new ArrayList<String>();
		reloadSettings();

	}

	public boolean reloadSettings() {
		Messages.clear();

		try {

			Database database = (Database) resolveVariable("database");
			View settingsView = database.getView("applicationSettings");
			Document settings = settingsView.getFirstDocument();

			if (null == settings) {

				settingsView.recycle();
				XSPContext context = (XSPContext) resolveVariable("context");

				UIViewRootEx2 view = (UIViewRootEx2) resolveVariable("view");
				if (!view.getPageName().equals("/admin.xsp")) {
					System.out.println("mypic: invalid configuration - " + view.getPageName());
					validConfig = false;
					context.redirectToPage("/admin.xsp");
				}

			} else {

				SaveUrlInNab = settings.getItemValueString("saveMypicURLToNab");
				EnableProfiles = settings.getItemValueString("enableProfiles");
				StoreProfilesInNab = settings.getItemValueString("saveProfileToNab");
				NabServer = settings.getItemValueString("nabServer");
				NabFilePath = settings.getItemValueString("nabFilePath");
				ShowApiInNav = settings.getItemValueString("enableAPIPage");
				DefaultFormat = settings.getItemValueString("defaultFormat");
				DefaultSize = settings.getItemValueString("defaultSize");
				
				if (DefaultSize.trim().compareTo("") == 0) {
					DefaultSize = "50";
				}
				
				if (DefaultFormat.trim().compareTo("") == 0) {
					DefaultFormat = "png";
				}

				if ((SaveUrlInNab.equals("Yes") || StoreProfilesInNab.equals("Yes")) && (NabServer.isEmpty() || NabFilePath.isEmpty())) {
					Messages.clear();
					Messages.add("To save mypic URLs or Profile information to a Names and address book, "
							+ "the NAB Server and File Path must be specified.");

					UIViewRootEx2 view = (UIViewRootEx2) resolveVariable("view");
					if (!view.getPageName().equals("/admin.xsp")) {
						XSPContext context = (XSPContext) resolveVariable("context");
						validConfig = false;
						context.redirectToPage("/admin.xsp");
					}

				}

				settings.recycle();
				settingsView.recycle();
			}
			validConfig = true;
		} catch (NotesException e) {
			e.printStackTrace();
		}

		return validConfig;
	}

	public boolean saveSettings() {

		try {
			Database database = (Database) resolveVariable("database");
			Document settings = null;
			View settingsView = database.getView("applicationSettings");
			settings = settingsView.getFirstDocument();
			if (settings == null) {
				settings = database.createDocument();
				settings.replaceItemValue("form", "applicationSettings");
			}

			settings.replaceItemValue("saveMypicURLToNab", SaveUrlInNab);
			settings.replaceItemValue("enableProfiles", EnableProfiles);
			settings.replaceItemValue("saveProfileToNab", StoreProfilesInNab);
			settings.replaceItemValue("nabServer", NabServer);
			settings.replaceItemValue("nabFilePath", NabFilePath);
			settings.replaceItemValue("enableAPIPage", ShowApiInNav);
			settings.replaceItemValue("defaultFormat", DefaultFormat);
			settings.replaceItemValue("defaultSize", DefaultSize);
			
			settings.save();

			if ((SaveUrlInNab.equals("Yes") || StoreProfilesInNab.equals("Yes")) && (NabServer.isEmpty() || NabFilePath.isEmpty())) {
				Messages.clear();
				Messages.add("To save mypic URLs or Profile information to a Names and address book, "
						+ "the NAB Server and File Path must be specified.");
				validConfig = false;
			} else {
				Messages.clear();
				validConfig = true;
			}

			settings.recycle();
		} catch (NotesException e) {
			e.printStackTrace();
			return false;
		}

		return validConfig;
	}
	
	public boolean storeInNab(String mypicProfileUNID) {
		try {
			Database database = (Database) resolveVariable("database");
			Document profile = database.getDocumentByUNID(mypicProfileUNID);
			
			if (null != profile) {
				Session session = (Session) resolveVariable("sessionAsSigner");
				Database nab = session.getDatabase(this.NabServer, this.NabFilePath);
				
				if (null != nab) {
					String userAbbrev = session.createName(profile.getItemValueString("User")).getAbbreviated();
					View VIMPeople = nab.getView("($VIMPeople)");
					
					Document nabEntry = VIMPeople.getDocumentByKey(userAbbrev, true);
					if (null != nabEntry) {
						XSPContext context = (XSPContext) resolveVariable("context");

						profile.replaceItemValue("PhotoURL",
								context.getUrl().getScheme() + "://" + context.getUrl().getHost() + '/' + database.getFilePath() +
								"/api.xsp?method=getmypic&id=" +
								UNIDEncoder.encode(session.createName(profile.getItemValueString("User")).getCanonical()) + "&size=" +
								this.DefaultSize + "&format=" + this.DefaultFormat);
						
						if (this.StoreProfilesInNab == "Yes") {
							// If we're synching w/ the NAB, Email Address should not be pushed to nab -- it should only get pulled
							profile.replaceItemValue("InternetAddress", nabEntry.getItemValueString("InternetAddress"));

							nabEntry.replaceItemValue("WebSite", profile.getItemValueString("WebSite"));
							nabEntry.replaceItemValue("PhotoURL", profile.getItemValueString("PhotoURL"));
							nabEntry.replaceItemValue("AboutMe", profile.getItemValueString("AboutMe"));
							nabEntry.replaceItemValue("StreetAddress", profile.getItemValueString("StreetAddress"));
							nabEntry.replaceItemValue("City", profile.getItemValueString("City"));
							nabEntry.replaceItemValue("State", profile.getItemValueString("State"));
							nabEntry.replaceItemValue("Zip", profile.getItemValueString("Zip"));
							nabEntry.replaceItemValue("Country", profile.getItemValueString("Country"));
							nabEntry.replaceItemValue("PhoneNumber", profile.getItemValueString("PhoneNumber"));
							nabEntry.replaceItemValue("HomeFAXPhoneNumber", profile.getItemValueString("HomeFAXPhoneNumber"));
							nabEntry.replaceItemValue("Spouse", profile.getItemValueString("Spouse"));
							nabEntry.replaceItemValue("Children", profile.getItemValueString("Children"));
							nabEntry.replaceItemValue("JobTitle", profile.getItemValueString("JobTitle"));
							nabEntry.replaceItemValue("CompanyName", profile.getItemValueString("CompanyName"));
							nabEntry.replaceItemValue("Dpartment", profile.getItemValueString("Dpartment"));
							nabEntry.replaceItemValue("Employee", profile.getItemValueString("Employee"));
							nabEntry.replaceItemValue("Location", profile.getItemValueString("Location"));
							nabEntry.replaceItemValue("Manager", profile.getItemValueString("Manager"));
							nabEntry.replaceItemValue("OfficePhoneNumber", profile.getItemValueString("OfficePhoneNumber"));
							nabEntry.replaceItemValue("OfficeFAXPhoneNumber", profile.getItemValueString("OfficeFAXPhoneNumber"));
							nabEntry.replaceItemValue("CellPhoneNumber", profile.getItemValueString("CellPhoneNumber"));
							nabEntry.replaceItemValue("PhoneNumber_6", profile.getItemValueString("PhoneNumber_6"));
							nabEntry.replaceItemValue("Assistant", profile.getItemValueString("Assistant"));
							nabEntry.replaceItemValue("OfficeStreetAddress", profile.getItemValueString("OfficeStreetAddress"));
							nabEntry.replaceItemValue("OfficeCity", profile.getItemValueString("OfficeCity"));
							nabEntry.replaceItemValue("OfficeState", profile.getItemValueString("OfficeState"));
							nabEntry.replaceItemValue("OfficeZip", profile.getItemValueString("OfficeZip"));
							nabEntry.replaceItemValue("OfficeCountry", profile.getItemValueString("OfficeCountry"));
							nabEntry.replaceItemValue("OfficeNumber", profile.getItemValueString("OfficeNumber"));
						}
						if (this.SaveUrlInNab == "Yes") {
							nabEntry.replaceItemValue("PhotoURL", profile.getItemValueString("PhotoURL"));
						}
						if (nabEntry.save()) {
							if (profile.save()) {
								return true;
							} else {
								return false;
							}
						} else {
							return false;
						}

					} else {
						return false;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (NotesException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private static Object resolveVariable(String variable) {
		return FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), variable);
	}

	public String getSaveUrlInNab() {
		return SaveUrlInNab;
	}

	public void setSaveUrlInNab(String saveUrlInNab) {
		SaveUrlInNab = saveUrlInNab;
	}

	public String getEnableProfiles() {
		return EnableProfiles;
	}

	public void setEnableProfiles(String enableProfiles) {
		EnableProfiles = enableProfiles;
	}

	public String getStoreProfilesInNab() {
		return StoreProfilesInNab;
	}

	public void setStoreProfilesInNab(String storeProfilesInNab) {
		StoreProfilesInNab = storeProfilesInNab;
	}

	public String getNabServer() {
		return NabServer;
	}

	public void setNabServer(String server) {
		NabServer = server;
	}

	public String getNabFilePath() {
		return NabFilePath;
	}

	public void setNabFilePath(String filePath) {
		NabFilePath = filePath;
	}

	public ArrayList<String> getMessages() {
		return Messages;
	}

	public String getShowApiInNav() {
		return ShowApiInNav;
	}

	public void setShowApiInNav(String showApiInNav) {
		ShowApiInNav = showApiInNav;
	}

	public String getDefaultFormat() {
		return DefaultFormat;
	}

	public void setDefaultFormat(String defaultFormat) {
		DefaultFormat = defaultFormat;
	}

	public String getDefaultSize() {
		return DefaultSize;
	}

	public void setDefaultSize(String defaultSize) {
		DefaultSize = defaultSize;
	}
}
