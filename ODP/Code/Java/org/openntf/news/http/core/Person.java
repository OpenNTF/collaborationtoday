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

import java.io.Serializable;
import java.security.MessageDigest;

import lotus.domino.Document;
import lotus.domino.NotesException;

import com.ibm.xsp.extlib.util.ExtLibUtil;

public class Person implements Serializable, Comparable<Person> {
	private static final long serialVersionUID = 1L;
	private String _pID;
	private String _eMailAddress;
	private String _twitter;
	private String _displayName;
	private String _pictureType;
	private String _pictureURL;
	private String documentId;
	private double _posts;

	public Person(String pID, String displayName, String twitter, String eMailAddress, String pictureType, String pictureURL, String documentId) {
		_pID = pID;
		_eMailAddress = eMailAddress;
		_twitter = twitter;
		_displayName = displayName;
		_pictureType = pictureType;
		_pictureURL = pictureURL;
		this.documentId = documentId;
	}

	public Person(double posts, String pID, String displayName, String twitter, String eMailAddress, String pictureType, String pictureURL, String documentId) {
		_posts = posts;
		_pID = pID;
		_eMailAddress = eMailAddress;
		_twitter = twitter;
		_displayName = displayName;
		_pictureType = pictureType;
		_pictureURL = pictureURL;
		this.documentId = documentId;
	}

	public static Person getEmptyPerson() {
		return new Person("", "", "", "", "", "", "");
	}

	public String getPID() {
		return _pID;
	}

	public String getEMailAddress() {
		return _eMailAddress;
	}

	public String getTwitter() {
		return _twitter;
	}

	public String getNameForTweet() {
		if (_twitter == null)
			return _displayName;
		if (_twitter.equalsIgnoreCase(""))
			return _displayName;
		return "@" + _twitter;
	}

	public String getDisplayName() {
		return _displayName;
	}

	public String getPictureURL() {
		if (_pictureType != null) {
			if (_pictureType.equalsIgnoreCase("multiple")) {
				return "heads.png";
			}
			if (_pictureType.equalsIgnoreCase("gravatar")) {
				return getGravatarURL();
			}
			if (_pictureType.equalsIgnoreCase("url")) {
				if (_pictureURL != null && !_pictureURL.isEmpty()) {
					return _pictureURL;
				}
			}
			if (_pictureType.equalsIgnoreCase("mypic")) {
				return "/mypicApi.xsp?method=getmypic&id=" + this.documentId;
			}
		}
		return "head.png";
	}

	public String getGravatarURL() {
		String output = "http://www.gravatar.com/avatar/";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			output = output + hex(md.digest(getEMailAddress().getBytes("CP1252")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}

	private static String hex(byte[] array) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; ++i) {
			sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
		}
		return sb.toString();
	}

	public double getPosts() {
		return _posts;
	}

	public int compareTo(Person o) {
		if (getPosts() > o.getPosts()) {
			return -1;
		} else if (getPosts() < o.getPosts()) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public boolean isChampion(){
		boolean result = false;
		try {
			Document doc = ExtLibUtil.getCurrentDatabase().getDocumentByUNID(documentId);
			result = doc.getItemValueString("PChampion").equals("1");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return result;
	}
}
