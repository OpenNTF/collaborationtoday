/*!COPYRIGHT HEADER! 
 *
 */

package org.openntf.ct.app;

import com.darwino.jsonstore.sql.impl.sqlite.SqliteDatabaseCustomizer;



/**
 * Database customizer.
 */
public class AppDatabaseCustomizer extends SqliteDatabaseCustomizer {
	
	public static final int VERSION = 0;
	
	public AppDatabaseCustomizer() {
	}
	
	@Override
	public int getVersion(String databaseName) {
		return VERSION;
	}

//	@Override
//	public void getAlterStatements(List<String> statements, String schema, String databaseName, int existingVersion) throws JsonException {
//		if(existingVersion==VERSION) {
//			// Ok, we are good!
//			return;
//		}
//	}
}
