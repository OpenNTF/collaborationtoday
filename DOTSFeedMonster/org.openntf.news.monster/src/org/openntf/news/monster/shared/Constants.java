package org.openntf.news.monster.shared;

public final class Constants {
	// TODO: Strings should be arranged again...
	// TODO: Constants should be organized...
	
	public static final String DATABASE_PATH_AND_NAME = "openntf/news1.nsf";
	public static final String APPLICATION_NAME = "FeedMonster";
	public static final String TASK_ID = "feedmonster";

	public static final int STORY_ABSTRACT_MAXLENGTH=400;
	public static final int HTTP_TIMEOUT=15000;
	public static final String HTTP_USERAGENT="CollaborationToday";	
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
	
	public static String VIEW_BLOGS_ALL = "(FeedDefinitions)";
	
	public static boolean debug=false;
	public static boolean memory_dump=true;
}
