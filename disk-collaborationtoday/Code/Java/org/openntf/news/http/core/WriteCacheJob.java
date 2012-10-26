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
 * .getColumnValues() once per entry, and to use .setPreferJavaDates.
 * Also fixed a stream-object leak in save/restoreState
 */

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import lotus.domino.*;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import com.ibm.domino.xsp.module.nsf.ThreadSessionExecutor;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import lotus.domino.*;

public class WriteCacheJob {

	public static void cancel() {
		synchronized (WriteCacheJob.class) {
			if (runningJob != null) {
				runningJob.cancel = true;
			}
		}
	}

	public static void start(HashSet<Click> clicks, String dbName) {
		synchronized (WriteCacheJob.class) {
			if (runningJob == null) {
				runningJob = new WriteClicksJob("News.nsf - Save Clicks",
						clicks, dbName);
				runningJob.addJobChangeListener(new JobChangeAdapter() {
					@Override
					public void done(IJobChangeEvent event) {
						runningJob = null;
					}
				});
				AccessController.doPrivileged(new PrivilegedAction<Object>() {
					public Object run() {
						runningJob.schedule();
						return null;
					}
				});
			}
		}
	}

	private static WriteClicksJob runningJob;

	private static final class WriteClicksJob extends Job {

		private boolean cancel;

		private String _title;
		private HashSet<Click> _clicks;
		private String _dbName;
		private ThreadSessionExecutor<IStatus> executor;

		public WriteClicksJob(String title, HashSet<Click> clicks, String dbName) {
			super(title);
			_title = title;
			_clicks = clicks;
			_dbName = dbName;

			this.executor = new ThreadSessionExecutor<IStatus>() {
				@SuppressWarnings("unchecked")
				@Override
				protected IStatus run(Session session) throws NotesException {					

					main: if (cancel) {
						break main;
					}					
				HashMap<String, HashSet<Click>> clicksByNID = new HashMap<String, HashSet<Click>>();

				Iterator it = _clicks.iterator();
				for (; it.hasNext();) {
					Click click = (Click) it.next();
					if (clicksByNID.containsKey(click.getNID())) {
						HashSet<Click> clicks = clicksByNID.get(click
								.getNID());
						clicks.add(click);
						clicks = removeDublicateIPAddresses(clicks);
						clicksByNID.put(click.getNID(), clicks);
					} else {
						HashSet<Click> newHashSet = new HashSet<Click>();
						newHashSet.add(click);
						clicksByNID.put(click.getNID(), newHashSet);
					}
				}
				Database db = null;
				Document doc = null;
				View newsByIDView = null;
				try {
					db = session.getDatabase(null, _dbName);

					if (db != null) {
						if (!db.isOpen())
							db.open();
						if (db.isOpen()) {
							newsByIDView = db.getView("NewsAllByID");
							newsByIDView.setAutoUpdate(false);

							Iterator iterator = clicksByNID.entrySet().iterator();
							while (iterator.hasNext()) {
								Map.Entry entry = (Map.Entry) iterator
								.next();
								String nID = (String) entry.getKey();
								HashSet<Click> clicksFromOneNID = (HashSet<Click>) entry.getValue();
								session.setConvertMime(false) ;
								doc = newsByIDView.getDocumentByKey(nID,
										true);
								if (doc != null) {
									HashSet<Click> storedClicks = (HashSet<Click>)restoreState(session, doc, "NClicks");
									if (storedClicks != null) {											
										storedClicks.addAll(clicksFromOneNID);
										clicksFromOneNID = storedClicks;
									}
									clicksFromOneNID = removeDublicateIPAddresses(clicksFromOneNID);

									doc.replaceItemValue("NClicksTotal", clicksFromOneNID.size());
									doc.replaceItemValue("NClicksLastWeek", getClicksLastWeek(clicksFromOneNID));
									saveState(session, clicksFromOneNID, doc, "NClicks");
									doc.save();						
									session.setConvertMime(true) ;

									doc.recycle();
								}
								iterator.remove();
							}
						}
					}
				} catch (NotesException e) {
					e.printStackTrace();
				} finally {
					if (newsByIDView != null)
						newsByIDView.recycle();
					if (db != null)
						db.recycle();
				}

				return Status.OK_STATUS;
				}

				int getClicksLastWeek(HashSet<Click> clicks) {
					int output = 0;					
					try {
						Iterator iterator = clicks.iterator();
						while (iterator.hasNext()) {
							Click click = (Click) iterator.next();											
							Calendar now = Calendar.getInstance();
							Calendar last = Calendar.getInstance();
							now.setTime(new Date());
							last.setTime(click.getDate());
							long millisecondsNow = now.getTimeInMillis();
							long millisecondsLast = last.getTimeInMillis();
							long diff = millisecondsNow - millisecondsLast;
							long diffDays = diff / (24 * 60 * 60 * 1000);

							if (diffDays <= 7) output = output + 1;														
						}												
					} catch (Exception e) {
						e.printStackTrace();
					}
					return output;
				}

				HashSet<Click> removeDublicateIPAddresses(HashSet<Click> clicks) {
					HashSet<Click> output = new HashSet<Click>();
					HashMap<String, Click> uniquesOnly = new HashMap<String, Click>();

					Iterator iterator = clicks.iterator();
					while (iterator.hasNext()) {
						Click click = (Click) iterator.next();
						if (!uniquesOnly.containsKey(click.getIP())) {
							uniquesOnly.put(click.getIP(), click);
						}
					}
					Iterator iterator2 = uniquesOnly.entrySet().iterator();
					while (iterator2.hasNext()) {
						Map.Entry entry = (Map.Entry) iterator2.next();
						Click click = (Click)entry.getValue();
						output.add(click);
					}
					return output;
				}

				public Serializable restoreState(Session session, Document doc, String itemName) {
					try {
						boolean convertMime = session.isConvertMime();
						session.setConvertMime(false);

						Serializable result = null;
						Stream mimeStream = session.createStream();
						MIMEEntity entity = doc.getMIMEEntity(itemName);
						entity.getContentAsBytes(mimeStream);

						ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
						mimeStream.getContents(streamOut);
						mimeStream.close();
						mimeStream.recycle();

						byte[] stateBytes = streamOut.toByteArray();
						ByteArrayInputStream byteStream = new ByteArrayInputStream(stateBytes);
						ObjectInputStream objectStream;
						if(entity.getHeaders().toLowerCase().contains("content-encoding: gzip")) {
							GZIPInputStream zipStream = new GZIPInputStream(byteStream);
							objectStream = new ObjectInputStream(zipStream);
						} else {
							objectStream = new ObjectInputStream(byteStream);
						}
						Serializable restored = (Serializable)objectStream.readObject();
						result = restored;

						entity.recycle();

						session.setConvertMime(convertMime);

						return result;
					} catch(Exception e) { }
					return null;
				}

				@SuppressWarnings("unused")
				public void saveStateGZIP(Session session, Serializable object, Document doc, String itemName) {
					try {
						boolean convertMime = session.isConvertMime();
						session.setConvertMime(false);

						ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
						ObjectOutputStream objectStream = new ObjectOutputStream(new GZIPOutputStream(byteStream));
						objectStream.writeObject(object);
						objectStream.flush();
						objectStream.close();

						MIMEEntity entity = null;
						MIMEEntity previousState = doc.getMIMEEntity(itemName);
						if (previousState == null) {
							entity = doc.createMIMEEntity(itemName);
						} else {
							entity = previousState;
						}

						Stream mimeStream = session.createStream();
						ByteArrayInputStream byteIn = new ByteArrayInputStream(byteStream.toByteArray());
						mimeStream.setContents(byteIn);
						entity.setContentFromBytes(mimeStream, "application/x-java-serialized-object", MIMEEntity.ENC_NONE);

						MIMEHeader header = entity.getNthHeader("Content-Encoding");
						if(header == null) { header = entity.createHeader("Content-Encoding"); }
						header.setHeaderVal("gzip");

						header.recycle();
						entity.recycle();
						mimeStream.close();
						mimeStream.recycle();

						session.setConvertMime(convertMime);
					} catch (Exception e) { }
				}
				public void saveState(Session session, Serializable object, Document doc, String itemName) {
					try {
						boolean convertMime = session.isConvertMime();
						session.setConvertMime(false);

						ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
						ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
						objectStream.writeObject(object);
						objectStream.flush();
						objectStream.close();

						MIMEEntity entity = null;
						MIMEEntity previousState = doc.getMIMEEntity(itemName);
						if (previousState == null) {
							entity = doc.createMIMEEntity(itemName);
						} else {
							entity = previousState;
						}

						Stream mimeStream = session.createStream();
						ByteArrayInputStream byteIn = new ByteArrayInputStream(byteStream.toByteArray());
						mimeStream.setContents(byteIn);
						entity.setContentFromBytes(mimeStream, "application/x-java-serialized-object", MIMEEntity.ENC_NONE);

						entity.recycle();
						mimeStream.close();
						mimeStream.recycle();

						session.setConvertMime(convertMime);
					} catch (Exception e) { e.printStackTrace(); }
				}
			};
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {
				return executor.run();
			} catch (Exception ex) {
				return Status.CANCEL_STATUS;
			}
		}
	};
}
