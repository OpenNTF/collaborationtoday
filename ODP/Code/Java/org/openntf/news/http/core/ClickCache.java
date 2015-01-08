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

import java.util.HashSet;
import java.util.Date;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import java.util.Calendar;

import javax.faces.context.FacesContext;

import lotus.domino.Database;
import lotus.domino.NotesException;

public class ClickCache {

	private HashSet<Click> _clicks;
	private Date _lastJob;

	public ClickCache() {
		if (_clicks == null) {
			_clicks = new HashSet<Click>();
		}
		if (_lastJob == null) {
			_lastJob = new Date();
		}
	} 

	public synchronized void addClick(Click click, boolean forceRun) {
		if (click != null) {
			click.setDate(new Date());
			_clicks.add(click);
		}
		boolean run = false;
		if (forceRun) {
			run = true;
		}
		else {
			if (runAgain()) {
				run = true;
			}
		}

		if (run) {
			HashSet<Click> clicksToBeSaved = _clicks;
			_clicks = new HashSet<Click>();
			_lastJob = new Date();
			try {
				Database db = ExtLibUtil.getCurrentDatabase();
				WriteCacheJob.start(clicksToBeSaved, db.getFilePath());
			} catch (NotesException e) {
				e.printStackTrace();
			}
		}
	}	

	private boolean runAgain() {
		boolean output = false;
		//int minutes = 1;
		int minutes = 5;

		try {
			String userName = MiscUtils.getCurrentCommonName();

			if (userName.equalsIgnoreCase("Anonymous")) {
				return false;
			}
			FacesContext context = FacesContext.getCurrentInstance();
			ConfigCache config = (ConfigCache) context.getApplication()
			.getVariableResolver().resolveVariable(context,
			"configCache");

			if (config.isUserModerator(userName)) {
				Calendar now = Calendar.getInstance();
				Calendar last = Calendar.getInstance();
				now.setTime(new Date());
				last.setTime(_lastJob);
				long millisecondsNow = now.getTimeInMillis();
				long millisecondsLast = last.getTimeInMillis();
				long diff = millisecondsNow - millisecondsLast;
				long diffMinutes = diff / (60 * 1000);

				if (diffMinutes >= minutes)
					output = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}
}
