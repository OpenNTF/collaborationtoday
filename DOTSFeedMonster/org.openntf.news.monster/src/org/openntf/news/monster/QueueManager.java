package org.openntf.news.monster;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewNavigator;

import org.openntf.news.monster.meta.FeedDefinition;
import org.openntf.news.monster.meta.Story;
import org.openntf.news.monster.shared.Constants;
import org.openntf.news.monster.shared.Logger;
import org.openntf.news.monster.shared.MonsterException;
import org.openntf.news.monster.shared.StoryReaderException;
import org.openntf.news.monster.shared.TaskletLogger;
import org.openntf.news.monster.shared.Utilities;

public enum QueueManager {
	INSTANCE;

	private Logger logger=new TaskletLogger(Logger.ALL_MESSAGES);

	private HashMap<String, FeedDefinition> feeds=new HashMap<String, FeedDefinition>();
	private LinkedList<String> feedQueue=new LinkedList<String>();

	private String dbName="";

	private boolean ready=false;
	
	private Database getNewsDB(Session session) {
		try {
			if(dbName.equals("")) {
				dbName = session.getEnvironmentString("Monster_TargetDB", true);
				if(null == dbName || dbName.equals("")) {
					dbName=Constants.DATABASE_PATH_AND_NAME;
				}
				logger.log("Using database '" + dbName + "'");
			}
			
			Database newsDB = session.getDatabase("", dbName );
			
			if(! newsDB.isOpen()) {
				newsDB.open();
			}
			
			return newsDB;
			
		} catch (NotesException e) {
			logger.printStack(e);
			throw new RuntimeException("Unable to open News database!");
		}
		
	}
	
	public void init(Session session, Logger logger) {
		setLogger(logger);
		init(session);
	}
	
	public void init(Session session) {
		try {
			feeds.clear();
			feedQueue.clear();
			prepareQueue(session);
			this.ready=true;
		} catch (MonsterException e) {
			logger.printStack(e);
			throw new RuntimeException("Unable to prepare feed queue");
		}
	}

	public void prepareQueue(Session session) throws MonsterException {
		Database newsDB=getNewsDB(session);
		HashMap<String, FeedDefinition> tempFeeds=getFeeds(newsDB);

		int count=0;

		String[] feedIds=feeds.keySet().toArray(new String[0]);
	
		for(String feedId : feedIds) {
			if(tempFeeds.containsKey(feedId)) {
				// We already know he feed. In case it has changed, we have to replace.
				FeedDefinition feed = tempFeeds.get(feedId);
				feeds.put(feedId, feed);
			} else {
				// It has been deleted. Remove it from feeds and queue.
				feeds.remove(feedId);
				feedQueue.remove(feedId);
				count++;
			}
			tempFeeds.remove(feedId);
		}

		if(count>0) {
			logger.log(String.format("%1d feeds removed from the queue", count));
		}
		
		// Now we have a list of new feeds in tempFeeds.
		feeds.putAll(tempFeeds);
		feedQueue.addAll(0, tempFeeds.keySet());
		
		if(tempFeeds.size()>0) {
			logger.log(String.format("%1d feed(s) added to queue", tempFeeds.size()));
		}
	}

	private HashMap<String, FeedDefinition> getFeeds(Database newsDB) throws MonsterException {

		View viewBlogsAll=null;
		ViewNavigator viewNavigator=null;
		
		HashMap<String, FeedDefinition> tempFeeds=new HashMap<String, FeedDefinition>();
		
		try {
			viewBlogsAll = newsDB.getView(Constants.VIEW_BLOGS_ALL);
			viewNavigator = viewBlogsAll.createViewNav();
			
			ViewEntry entry = viewNavigator.getFirst();

			while (entry != null) {
				Document blogDocument = entry.getDocument();
				String blogId=blogDocument.getItemValueString("BID");
				
				FeedDefinition feed=new FeedDefinition(blogId);

				tempFeeds.put(blogId, feed);

				feed.setLastSuccess(Utilities.getDateField(blogDocument, "BLastSuccess"));
				feed.setLastTry(Utilities.getDateField(blogDocument, "BLastTry"));
				feed.setLastError(Utilities.getDateField(blogDocument, "BLastError"));
				feed.setLastErrorMessage(blogDocument.getItemValueString("BLastErrorMessage"));

				feed.setUrl(blogDocument.getItemValueString("BFeed"));
				feed.setAuthorId(blogDocument.getItemValueString("PID"));
				feed.setRedirection(blogDocument.getItemValueString("BFeedHasRedirection").equals("1"));
				feed.setDisplayName(blogDocument.getItemValueString("BDisplayName"));
				feed.setNoteId(blogDocument.getNoteID());
				
				ViewEntry tmpEntry = viewNavigator.getNext();

				Utilities.recycleObjects(entry, blogDocument);
		        entry = tmpEntry;
		        
			}
		} catch (NotesException ne) {
			MonsterException mexc=new MonsterException(Constants.EXCEPTION_NEWSDB_FEEDS + ": "+ ne.text);
			mexc.setStackTrace(ne.getStackTrace());
			throw mexc;
		} catch (Exception e) {
			MonsterException mexc=new MonsterException(Constants.EXCEPTION_NEWSDB_FEEDS + ": "+ e.getLocalizedMessage());
			mexc.setStackTrace(e.getStackTrace());
			throw mexc;
		} finally {
			Utilities.recycleObjects(viewNavigator, viewBlogsAll);
		}

		return tempFeeds;
	}

	public void readFeedById(String id, Session session) {
		Database newsDB=getNewsDB(session);
		FeedDefinition feed=feeds.get(id);
		
		if(feed==null) {
			logger.error("Invalid blog id ("+id+")");
			return;
		}
				
		if(feed.isDeleted(newsDB)) {
			logger.error("Feed ('"+id+"') has been deleted. Removing from queue...");
			feedQueue.remove(id);
			feeds.remove(id);
		} else {
			readFeed(feed, session);
			feedQueue.remove(id);
			feedQueue.addLast(id);
		}
	}
	
	public void readNextFeed(Session session) {
		String id=feedQueue.getFirst();
		logger.log("Fetching '"+id+"'...");
		readFeedById(id, session);
	}

	// TODO: Although we make sure deletion check has been made before, secondary check is needed.
	private boolean readFeed(FeedDefinition feed, Session session) {
		Database newsDB=getNewsDB(session);

		boolean result=false;
		Date startTime=new Date();
		int storyCount=0;
		
		String blogId=feed.getId();
		Calendar lastStoryDate=null;
		Calendar storyDate=null;
		
		try {
			feed.setLastTry(Calendar.getInstance());

			lastStoryDate=feed.getLastSuccess();
			
			RSSReader rss=new RSSReader(feed.getUrl());

			// Setting default values
			rss.addStoryField("BID", blogId); //blogId
			rss.addStoryField("PID", feed.getAuthorId()); //authorId
			rss.addStoryField("NState", "queued"); //State

			rss.setRedirectEnabled(feed.isRedirection());
			
			rss.loadFeed();
			
			feed.setLastError(null);
			feed.setLastErrorMessage("");
			
			for(int i=0; i<rss.getSize(); i++) {
				Story s=rss.getNthStory(i);
				
				storyDate=s.getDate();

				boolean saveToDB=lastStoryDate == null || storyDate.after(lastStoryDate);
				
				if(saveToDB) {
					storyCount++;
					s.saveToDatabase(newsDB);
				}
			}

			if(storyCount>0) {
				feed.setLastSuccess(Calendar.getInstance());
			}

			feed.saveState(newsDB);

			result=true;

		} catch (NotesException e) {
			logger.printStack(e);
			logger.error(Constants.EXCEPTION_BLOG_SAVE + " : " + e.text);
		} catch(StoryReaderException sre) {
			logger.printStack(sre);
			logger.error("Error reading RSSEntry: ["+blogId+"]: "+sre.getMessage());
			try {
				feed.setLastError(Calendar.getInstance());
				feed.setLastErrorMessage(sre.getMessage());
				feed.saveState(newsDB);
			} catch (NotesException e) {
				logger.printStack(e);
				logger.error(Constants.EXCEPTION_BLOG_SAVE + " : " + e.text);
			}
		}
		
        long duration=((new Date().getTime()) - startTime.getTime())/1000;
        logger.debug("Fetched " + String.valueOf(storyCount) + " stories from '" + blogId + "' in " + String.valueOf(duration) + " secs...");

		return result;
	}
	
	public void listQueue() {
		for(String id:this.feedQueue) {
			FeedDefinition feed=feeds.get(id);
			
			logger.log(feed.getId()+":"+feed.getDisplayName());
		}		
	}

	public void setLogger(Logger _logger) {
		logger=_logger;
	}

	/**
	 * @return the ready
	 */
	public boolean isReady() {
		return ready;
	}

}
