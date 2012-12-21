package frostillicus;

import java.util.*;
import java.io.Serializable;

import com.ibm.xsp.extlib.util.ExtLibUtil;

import lotus.domino.*;

public class AliasManager extends AbstractKeyMap<String, String> implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<String, String> cache = new HashMap<String, String>();

	public AliasManager() throws NotesException {
		this.regenerateCache();
	}

	public String get(Object key) {
		if(!(key instanceof String)) { throw new IllegalArgumentException(); }

		if(this.cache.containsKey(key)) {
			return this.cache.get(key);
		}
		return (String)key;
	}

	@Override
	public void clear() {
		this.cache.clear();
		try { this.regenerateCache(); } catch(NotesException ne) { }
	}

	@SuppressWarnings("unchecked")
	private void regenerateCache() throws NotesException {
		Database database = ExtLibUtil.getCurrentDatabase();
		View aliases = database.getView("Aliases");
		aliases.setAutoUpdate(false);
		ViewNavigator viewNav = aliases.createViewNav();
		ViewEntry entry = viewNav.getFirst();
		while(entry != null) {
			entry.setPreferJavaDates(true);
			List<Object> columnValues = entry.getColumnValues();

			this.cache.put((String)columnValues.get(0), (String)columnValues.get(1));

			ViewEntry tempEntry = entry;
			entry = viewNav.getNext(entry);
			tempEntry.recycle();
		}
		viewNav.recycle();
		aliases.recycle();
	}
}
