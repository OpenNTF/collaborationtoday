package org.openntf.news.http.core;

/*
 * © Copyright IBM, 2012
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

import java.util.Vector;

public class Type {

	private String _iD;
	private String _description;
	private Vector _moderators;
	private String _displayName;
	private String _categoryId;
	private String _hashTags;

	public Type(String iD, String displayName, Vector moderators,
			String description, String categoryId, String hashTags) {
		_iD = iD;
		_description = description;
		_moderators = moderators;
		_displayName = displayName;
		_categoryId = categoryId;
		_hashTags = hashTags;
		if (_hashTags == null) {
			_hashTags = "";
		}
	}
	
	public static Type getEmptyType() {
		return new Type("", "", new Vector(), "", "", "");
	}

	public String getCategoryId() {
		return _categoryId;
	}
	
	public String getHashTags() {
		return _hashTags;
	}
	
	public String getID() {
		return _iD;
	}

	public Vector getModerators() {
		return _moderators;
	}

	public String getDescription() {
		return _description;
	}

	public String getDisplayName() {
		return _displayName;
	}
}
