package org.openntf.news.monster.tasklets;

import lotus.domino.NotesException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.openntf.news.monster.QueueManager;
import org.openntf.news.monster.shared.MonsterException;

import com.ibm.dots.annotation.HungPossibleAfter;
import com.ibm.dots.annotation.Run;
import com.ibm.dots.annotation.RunEvery;
import com.ibm.dots.annotation.RunOnStart;
import com.ibm.dots.task.AbstractServerTaskExt;
import com.ibm.dots.task.RunWhen;
import com.ibm.dots.task.RunWhen.RunUnit;


public class FeedMonsterTasklet extends AbstractServerTaskExt {

	private QueueManager qm=QueueManager.INSTANCE;
	
	public FeedMonsterTasklet() {
	}

	public void dispose() throws NotesException {
	}
	
	@RunOnStart
	public void initFeedMonster(IProgressMonitor monitor) {
		logMessage("Initialization started");
		qm.init(getSession());
		logMessage("Initialization finished");
	}

	@Run(id="list")
	public void listQueue(IProgressMonitor monitor) {
		if(! qm.isReady()) {
			logMessage("LIST: Queue Manager is not initialized!");
			return;
		}
		qm.listQueue();
	}
	
	@Run(id="fetch")
	public void fetchFeed(String[] args, IProgressMonitor monitor) {
		if(! qm.isReady()) {
			logMessage("FETCH: Queue Manager is not initialized!");
			return;
		}
		String blogId=args[0];
		if(blogId=="") {
			logMessage("Please provide a blog Id...");
		}

		logMessage("Fetching '"+blogId+"' started");
		qm.readFeedById(blogId, getSession());
		logMessage("Fetching '"+blogId+"' finished");
	}

	@Run(id="reload")
	@RunEvery( every=60, unit=RunUnit.minute )
	public void reloadQueue(IProgressMonitor monitor) throws MonsterException {
		if(! qm.isReady()) {
			logMessage("RELOAD: Queue Manager is not initialized!");
			return;
		}

		logMessage("Reload started");
		qm.prepareQueue(getSession());
		qm.listQueue();
		logMessage("Reload finished");
	}
	
	@RunEvery( every=5, unit=RunUnit.minute )
	@HungPossibleAfter( timeInMinutes=2 )
	public void fetchNext(IProgressMonitor monitor) {
		if(! qm.isReady()) {
			logMessage("FETCHNEXT: Queue Manager is not initialized!");
			return;
		}
		qm.readNextFeed(getSession());
	}
	
	@Override
	protected void doRun(RunWhen runWhen, IProgressMonitor monitor) throws NotesException {}
}
