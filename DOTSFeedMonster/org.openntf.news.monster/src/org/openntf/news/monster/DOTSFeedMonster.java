package org.openntf.news.monster;

import lotus.domino.Database;
import lotus.domino.NotesException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.openntf.news.shared.Constants;
import org.openntf.news.shared.MonsterException;
import org.openntf.news.shared.Utilities;

import com.ibm.dots.task.AbstractServerTask;
import com.ibm.dots.task.RunWhen;


public class DOTSFeedMonster extends AbstractServerTask {

	Database db;
	
	public DOTSFeedMonster() {
	}

	public void dispose() throws NotesException {
		if(db!=null) db.recycle();
	}
	
	public void run(RunWhen runWhen, String[] args, IProgressMonitor monitor) throws NotesException {
		
		monitor.beginTask(Constants.APPLICATION_NAME + ": " + Constants.TASK_ID, 0 );
		logMessage(Constants.APPLICATION_NAME + ": " + Constants.TASK_ID + " started");
		if(Constants.memory_dump) logMessage("Launching: " + Utilities.getMemoryStatus());
		
		String dbname=getSession().getEnvironmentString("Monster_TargetDB", true);
		
		if(null == dbname || dbname.equals("")) {
			dbname=Constants.DATABASE_PATH_AND_NAME;
		}
		
		logMessage(Constants.APPLICATION_NAME + ": Using database '" + dbname + "'");
		
		db = getSession().getDatabase("", dbname );

		try {
			if(!db.isOpen()) {
				throw new MonsterException(Constants.EXCEPTION_NEWSDB_ERROR + ": "+dbname);
			}
		
			// TODO: Add console logging
			new FeedMonster(db).ReadFeeds();
		} catch(MonsterException e) {
			if(Constants.debug) e.printStackTrace();
			logMessage(e.getMessage());
		}
		
		logMessage(Constants.APPLICATION_NAME + ": " + Constants.TASK_ID + " finished");
		if(Constants.memory_dump) logMessage("Run Success: " + Utilities.getMemoryStatus());
		monitor.worked( 1 );

	}
}
