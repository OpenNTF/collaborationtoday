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
import java.util.Date;

public class Click implements Serializable {

	static final long serialVersionUID = 1;

	private String _nID;
	private String _ip;
	private Date _date;

	public Click() {
	}

	public void setNID(String nID) {
		_nID = nID;
	}

	public void setIP(String ip) {
		_ip = ip;
	}

	public void setDate(Date date) {
		_date = date;
	}

	public String getNID() {
		return _nID;
	}

	public String getIP() {
		return _ip;
	}

	public Date getDate() {
		return _date;
	}
}
