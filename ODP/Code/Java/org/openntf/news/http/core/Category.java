package org.openntf.news.http.core;

import java.io.Serializable;

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

public class Category implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9198710789794456083L;
	private final String _iD;
	private final String _description;
	private final String _displayName;

	public Category(final String iD, final String displayName, 
			final String description) {
		_iD = iD;
		_description = description;
		_displayName = displayName;
	}
	
	public static Category getEmptyCategory() {
		return new Category("", "", "");
	}
	
	public String getID() {
		return _iD;
	}

	public String getDescription() {
		return _description;
	}

	public String getDisplayName() {
		return _displayName;
	}
}
