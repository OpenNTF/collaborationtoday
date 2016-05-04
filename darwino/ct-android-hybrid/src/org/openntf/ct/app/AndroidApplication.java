/*!COPYRIGHT HEADER! 
 *
 */

package org.openntf.ct.app;

import android.app.Application;

import com.darwino.commons.json.JsonException;

/**
 * Android Application
 * 
 */
public class AndroidApplication extends Application {
	
	@Override
	public final void onCreate() {
		super.onCreate();

		// Create the Darwino Application
		try {
			AppHybridApplication.create(this);
		} catch(JsonException ex) {
			throw new RuntimeException("Unable to create Darwino application", ex);
		}
	}
}
