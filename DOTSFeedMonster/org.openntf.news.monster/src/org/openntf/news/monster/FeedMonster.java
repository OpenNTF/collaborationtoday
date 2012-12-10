package org.openntf.news.monster;

import java.util.Calendar;
import java.util.Date;

import lotus.domino.Database;
import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewNavigator;
import lotus.domino.Item;

import org.openntf.news.Story;
import org.openntf.news.shared.Constants;
import org.openntf.news.shared.MonsterException;
import org.openntf.news.shared.StoryReaderException;

public final class FeedMonster {

	private Session session;
	private Database newsDB;
	private View viewCheck;
	private View viewBlogsAll;
	private ViewNavigator viewNavigator;
	private DateTime now;
	
	public FeedMonster(Database _newsDB) throws NotesException {
		newsDB = _newsDB;
	}
	
	public final void ReadFeeds() throws MonsterException, NotesException {
		
		try {
			try {	
				if (!newsDB.isOpen()) {
					newsDB.open();
				}
				session=newsDB.getParent();
			} catch(NotesException e) {
				if(Constants.debug) e.printStackTrace();
				throw new MonsterException(Constants.EXCEPTION_NEWSDB_ERROR + ": "+ e.getMessage());
			}
			now=session.createDateTime(Calendar.getInstance());

			viewCheck = newsDB.getView(Constants.VIEW_NEWS_CHECK);
			viewBlogsAll = newsDB.getView(Constants.VIEW_BLOGS_ALL);
			viewNavigator = viewBlogsAll.createViewNav();
			ViewEntry tmpEntry;
			ViewEntry entry = viewNavigator.getFirst();
			while (entry != null) {
				Date startTime=new Date();
				int storyCount=0;
				
				Document blogDocument = entry.getDocument();
				String blogId=blogDocument.getItemValueString("BID");
				
				try {
					blogDocument.replaceItemValue("BLastTry", now);

					// FIXME: DateTime object to be recycled
					DateTime lastStoryDate=null;

					Item lastSuccess=blogDocument.getFirstItem("BLastSuccess");
					
					if(null != lastSuccess && lastSuccess.getType()==Item.DATETIMES) {
						lastStoryDate=lastSuccess.getDateTimeValue();
					}
					
					RSSReader rss=new RSSReader(blogDocument.getItemValueString("BFeed"));

					// Setting default values
					rss.addStoryField("BID", blogId); //blogId
					rss.addStoryField("PID", blogDocument.getItemValueString("PID")); //authorId
					rss.addStoryField("NState", "queued"); //State

					if(blogDocument.getItemValueString("BFeedHasRedirection").equals("1")) {
						rss.setRedirectEnabled(true);
					}
					
					rss.loadFeed();
					
					blogDocument.replaceItemValue("BLastError", "");
					blogDocument.replaceItemValue("BLastErrorMessage", "");
					
					for(int i=0; i<rss.getSize(); i++) {
						Story s=rss.getNthStory(i);
						DateTime storyDate=session.createDateTime(s.getDate());
						boolean saveToDB=lastStoryDate == null || storyDate.timeDifference(lastStoryDate) > 0;
						storyDate.recycle();
						
						if(saveToDB) {
							storyCount++;
							s.saveToDatabase(newsDB);
						}
					}

					if(storyCount>0) {
						blogDocument.replaceItemValue("BLastSuccess", now);
					}
					blogDocument.save();

				} catch (NotesException e) {
					if(Constants.debug) e.printStackTrace();
					System.out.println(Constants.EXCEPTION_BLOG_SAVE + " : " + e.getMessage());
				} catch(StoryReaderException sre) {
					System.out.println("Error reading RSSEntry: ["+blogDocument.getItemValueString("BID")+"]: "+sre.getMessage());
					try {
						blogDocument.replaceItemValue("BLastError", now);
						blogDocument.replaceItemValue("BLastErrorMessage", sre.getMessage());
						blogDocument.save();
					} catch (NotesException e) {
						if(Constants.debug) e.printStackTrace();
						System.out.println(Constants.EXCEPTION_BLOG_SAVE + " : " + e.getMessage());
					}
				}
					
				tmpEntry = viewNavigator.getNext();
		        entry.recycle();
		        blogDocument.recycle();
		        entry = tmpEntry;
		        
		        long duration=((new Date().getTime()) - startTime.getTime())/1000;
		        System.out.println("Fetched " + String.valueOf(storyCount) + " stories from '" + blogId + "' in " + String.valueOf(duration) + " secs...");
			}	
		}
		catch (Exception e) {
			if(Constants.debug) e.printStackTrace();
			throw new MonsterException(Constants.EXCEPTION_NEWSDB_FEEDS + ": "+ e.getMessage());
		}
		finally {
			if (viewNavigator != null) {
				viewNavigator.recycle();
			}
			if (viewBlogsAll != null) {
				viewBlogsAll.recycle();
			}
			if (viewCheck != null) {
				viewCheck.recycle();
			}
			if(now!=null) {
				now.recycle();
			}
		}

	}
	
}
