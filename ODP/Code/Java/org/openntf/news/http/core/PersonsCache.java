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

/*
 * 2012-09-29 - Jesse Gallagher
 * Modified view looping to setAutoUpdate(false), only run
 * .getColumnValues() once per entry, and to use .setPreferJavaDates
 */

import java.util.*;
import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewNavigator;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class PersonsCache {

	public PersonsCache() {
	}    

	private boolean _isCached = false;
	private Map<String, Person> _persons;

	private void initialize() {
		if (_isCached) return;
		init();
	}

	public void update() {
		_isCached = false;
		_persons = null;
		initialize();
	}

	@SuppressWarnings("unchecked")
	private synchronized void init() {
		_persons = new HashMap<String, Person>();

		Database db = ExtLibUtil.getCurrentDatabase();
		View viewPersonsAll = null;
		ViewNavigator viewNavigator = null;

		try { 
			viewPersonsAll = db.getView("PersonsAll");
			viewPersonsAll.setAutoUpdate(false);
			viewNavigator = viewPersonsAll.createViewNav();
			ViewEntry tmpEntry;
			ViewEntry entry = viewNavigator.getFirst();
			while (entry != null) {
				entry.setPreferJavaDates(true);
				List<Object> columnValues = entry.getColumnValues();

				Person person = new Person(
						(String)columnValues.get(5),
						(String)columnValues.get(0),
						(String)columnValues.get(1),
						(String)columnValues.get(2),
						(String)columnValues.get(3),
						(String)columnValues.get(4),
						entry.getUniversalID());
				_persons.put((String)columnValues.get(5), person);

				tmpEntry = viewNavigator.getNext();
				entry.recycle();
				entry = tmpEntry;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();		
		} finally {
			try {
				if (viewNavigator != null) {
					viewNavigator.recycle();
				}
				if (viewPersonsAll != null) {
					viewPersonsAll.recycle();
				}
			} catch (NotesException e) { 
				e.printStackTrace();
			}
		}
		_isCached = true;
	}

	public Person getPerson(String pID) {
		initialize();
		if (_persons == null)
			return Person.getEmptyPerson();
		Person person = _persons.get(pID);
		if (person == null)
			return Person.getEmptyPerson();
		return person;
	}

	public List<String> getPersonsForCombobox() {
		initialize(); 
		List<String> output = new Vector<String>();

		PersonComparator personComparator =  new PersonComparator(_persons);
		Map<String, Person> sortedPersons = new TreeMap<String, Person>(personComparator);
		sortedPersons.putAll(_persons);

		for(String key : sortedPersons.keySet()) {       
			Person person = (Person)sortedPersons.get(key);
			output.add(person.getDisplayName() + "|" + person.getPID());
		}
		return output;
	}
}