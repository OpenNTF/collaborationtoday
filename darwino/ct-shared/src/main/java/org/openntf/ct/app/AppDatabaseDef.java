/*!COPYRIGHT HEADER! 
 *
 */

package org.openntf.ct.app;

import com.darwino.commons.json.JsonException;
import com.darwino.commons.util.StringUtil;
import com.darwino.jsonstore.impl.DatabaseFactoryImpl;
import com.darwino.jsonstore.meta._Database;
import com.darwino.jsonstore.meta._Store;

/**
 * Database Definition.
 * 
 * @author Philippe Riand
 */
public class AppDatabaseDef extends DatabaseFactoryImpl {

	public static final int DATABASE_VERSION	= 1;
	public static final String DATABASE_NAME	= "ct";
	
	public static final String STORE_BLOG		= "Blog";
	public static final String STORE_CATEGORY	= "Category";
	public static final String STORE_CONFIG		= "Config";
	public static final String STORE_DELETED	= "Deleted";
	public static final String STORE_NEWS		= "News";
	public static final String STORE_PERSON		= "Person";
	public static final String STORE_REQUEST	= "Request";
	public static final String STORE_TYPE		= "Type";

	
	
	@Override
	public int getDatabaseVersion(String databaseName) throws JsonException {
		if(!StringUtil.equalsIgnoreCase(databaseName, DATABASE_NAME)) {
			return -1;
		}
		return DATABASE_VERSION;
	}
	
	@Override
	public _Database loadDatabase(String databaseName) throws JsonException {
		if(!StringUtil.equalsIgnoreCase(databaseName, DATABASE_NAME)) {
			return null;
		}
		_Database db = new _Database(DATABASE_NAME, "Collaboration Today", DATABASE_VERSION);

		db.setReplicationEnabled(true);
		db.setInstanceEnabled(false);
		
		// Stores.
		{
			_Store store = db.addStore(STORE_BLOG);
			store.setLabel("Blogs");
		}
		{
			_Store store = db.addStore(STORE_CATEGORY);
			store.setLabel("Categories");
		}
		{
			_Store store = db.addStore(STORE_CONFIG);
			store.setLabel("Configurations");
		}
		{
			_Store store = db.addStore(STORE_DELETED);
			store.setLabel("Deleted documents");
		}
		{
			_Store store = db.addStore(STORE_NEWS);
			store.setLabel("News");
		}
		{
			_Store store = db.addStore(STORE_PERSON);
			store.setLabel("Persons");
		}
		{
			_Store store = db.addStore(STORE_REQUEST);
			store.setLabel("Requests");
		}
		{
			_Store store = db.addStore(STORE_TYPE);
			store.setLabel("Types");
		}

		return db;
	}
}
