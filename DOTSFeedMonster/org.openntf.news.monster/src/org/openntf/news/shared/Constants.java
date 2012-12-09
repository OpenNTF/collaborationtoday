package org.openntf.news.shared;

public final class Constants {
	public static final String DATABASE_PATH_AND_NAME = "openntf/news1.nsf";
	public static final String APPLICATION_NAME = "News";
	public static final String TASK_ID = "org.openntf.news.readFeeds";

	public static final int STORY_ABSTRACT_MAXLENGTH=400;
	
	public static final int SO_CLIENT_ID = 636; 
	public static final String SO_CLIENT_SECRET = "UJJvYYZ)Fdf1o7IMwOLa4Q((";
	public static final String SO_APP_KEY = "WyYv8IMhY28eyZdIDcbZfw((";
	
	
	public static String EXCEPTION_NO_BLOG_DOCUMENT = "No blog document";
	public static String EXCEPTION_NO_SESSION = "Unable to obtain Notes Session";
	public static String EXCEPTION_BLOG_SAVE = "Blog document cannot be updated";
	public static String EXCEPTION_INVALID_URL = "Invalid URL specified";
	public static String EXCEPTION_INVALID_CONFIG =  "Feed document cannot be read";
	public static String EXCEPTION_COMM_ERROR = "Communication Error";
	public static String EXCEPTION_FEED_ERROR = "Feed cannot be read";
	public static String EXCEPTION_SAVE_STORY = "Story cannot be saved";
	public static String EXCEPTION_NEWSDB_ERROR = "News Database cannot be opened";
	public static String EXCEPTION_NEWSDB_FEEDS = "Error in Feeds";
	
	public static String VIEW_BLOGS_ALL = "BlogsAll";
	public static String VIEW_NEWS_CHECK = "NewsByIdDate";
	
	public static boolean debug=false;
	public static boolean memory_dump=true;
}
