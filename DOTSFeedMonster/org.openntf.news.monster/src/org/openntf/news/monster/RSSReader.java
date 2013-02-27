package org.openntf.news.monster;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.htmlparser.jericho.Renderer;
import net.htmlparser.jericho.Source;

import org.openntf.news.monster.meta.Story;
import org.openntf.news.monster.shared.Constants;
import org.openntf.news.monster.shared.StoryReaderException;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class RSSReader {

	private String feedUrl;

	private HashMap<String,Object> storyFields;
	
	private int size=0;
	private boolean loaded=false;
	private SyndFeed feed;
	private boolean redirectEnabled=false;
	
	public RSSReader(String _feedUrl) throws StoryReaderException {
		
		this.feedUrl=_feedUrl;
		this.storyFields=new HashMap<String,Object>();

	}

	public void addStoryField(String name, Object value) {
		storyFields.put(name, value);
	}

	public Object getStoryField(String name) {
		return storyFields.get(name);
	}

	public void addStoryFields(Map<String,Object> values) {
		storyFields.putAll(values);
	}

	public void loadFeed() throws StoryReaderException {
		try {
			URL url = new URL(feedUrl);
			
			HttpURLConnection httpSource = (HttpURLConnection)url.openConnection();
			
			httpSource.setConnectTimeout(Constants.HTTP_TIMEOUT);
			httpSource.setReadTimeout(Constants.HTTP_TIMEOUT);
			httpSource.addRequestProperty("User-Agent", Constants.HTTP_USERAGENT);
			
			SyndFeedInput input = new SyndFeedInput();
			XmlReader reader = new XmlReader(httpSource);

			this.feed = input.build(reader);
			this.size = feed.getEntries().size();
			setLoaded(true);

		} catch (MalformedURLException e) {
			StoryReaderException sre=new StoryReaderException(Constants.EXCEPTION_INVALID_URL + ": "+ e.getMessage());
			sre.setStackTrace(e.getStackTrace());
			throw sre;
		} catch (IOException e) {
			StoryReaderException sre=new StoryReaderException(Constants.EXCEPTION_COMM_ERROR + ": "+ e.getMessage());
			sre.setStackTrace(e.getStackTrace());
			throw sre;
		} catch (IllegalArgumentException e) {
			StoryReaderException sre=new StoryReaderException(Constants.EXCEPTION_FEED_ERROR + ": "+ e.getMessage());
			sre.setStackTrace(e.getStackTrace());
			throw sre;
		} catch (FeedException e) {
			StoryReaderException sre=new StoryReaderException(Constants.EXCEPTION_FEED_ERROR + ": "+ e.getMessage());
			sre.setStackTrace(e.getStackTrace());
			throw sre;
		}			
	}

	public Story getNthStory(int index) {
		
		if(! isLoaded()) {
			throw new IllegalStateException("RSSReader has not been loaded yet");
		}
		
		if(index < 0 || index >=size) {
			throw new IndexOutOfBoundsException();
		}
		
		SyndEntry entry=(SyndEntry)feed.getEntries().get(index);
		return entryToStory(entry);
		
	}

	private Story entryToStory(SyndEntry entry) {
		Story story=new Story();
		
		story.addFields(storyFields);
		
		story.setLink(entry.getLink());
		story.setRedirectEnabled(isRedirectEnabled());
		
		Calendar date = Calendar.getInstance();
		
		if (entry.getPublishedDate()!=null) {
			date.setTime(entry.getPublishedDate());
		}
		else if (entry.getUpdatedDate()!=null) {
			date.setTime(entry.getUpdatedDate());
		}
		
		story.setDate(date);
		story.setTitle(entry.getTitle());
		
		String content=getFeedContent(entry);
		String abstractContent=cleanHTML(content);
		
		if(abstractContent.length() >= Constants.STORY_ABSTRACT_MAXLENGTH) {
			abstractContent = abstractContent.substring(0, Constants.STORY_ABSTRACT_MAXLENGTH - 1) + "[...]";
		}
	
		story.setAbstractContent(abstractContent);
//		story.setFullContent(content); // Currently we won't have full content due to possible legal implications.
		
		return story;		
	}
	
	@SuppressWarnings("unchecked")
	private String getFeedContent(SyndEntry entry) {
		StringBuffer output = new StringBuffer();
		if(entry.getDescription() == null) {
			Iterator<SyndContent> contentIterator = entry.getContents().iterator();

			while (contentIterator.hasNext()) {
				SyndContent content = contentIterator.next();
				output.append(content.getValue());
			}
		}
		else {
			SyndContent content = entry.getDescription();
			output.append(content.getValue());
		}
		return output.toString();
	}

	private String cleanHTML(String input) {
		Renderer renderer=new Source(input).getRenderer();

		// We don't want it to be wrapped
		renderer.setMaxLineLength(Integer.MAX_VALUE);
		renderer.setIncludeHyperlinkURLs(false);
		
		return renderer.toString();
	}
	
	public int getSize() {
		return size;
	}

	public boolean isLoaded() {
		return loaded;
	}

	private void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	private boolean isRedirectEnabled() {
		return redirectEnabled;
	}

	public void setRedirectEnabled(boolean redirectEnabled) {
		this.redirectEnabled = redirectEnabled;
	}

}
