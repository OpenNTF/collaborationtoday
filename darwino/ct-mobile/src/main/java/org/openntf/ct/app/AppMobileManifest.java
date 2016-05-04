/*!COPYRIGHT HEADER! 
 *
 */

package org.openntf.ct.app;

import com.darwino.commons.json.JsonException;
import com.darwino.jsonstore.sql.impl.sqlite.SqliteDatabaseCustomizer;
import com.darwino.mobile.platform.DarwinoMobileManifest;
import com.darwino.mobile.platform.DarwinoMobileSettings;

/**
 * Mobile Application Manifest.
 * 
 * @author Philippe Riand
 */
public class AppMobileManifest extends DarwinoMobileManifest {
	
	public AppMobileManifest(String pathInfo) {
		super(pathInfo);
	}

	@Override
	public void initDefaultSettings(DarwinoMobileSettings settings) {
		super.initDefaultSettings(settings);
		// Set the default settings here
	}

	@Override
	public SqliteDatabaseCustomizer findDatabaseCustomizer(String dbName) throws JsonException {
		// Return your database customizer here
		return new AppDatabaseCustomizer();
	}
}
