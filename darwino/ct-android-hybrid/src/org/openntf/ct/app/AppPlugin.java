/*!COPYRIGHT HEADER! 
 *
 */

package org.openntf.ct.app;

import java.util.List;

import com.darwino.mobile.platform.commands.CommandsExtension;

/**
 * Android Plugin for registering the services.
 * 
 */
public class AppPlugin extends AppMobilePlugin {
	
	public AppPlugin() {
		super("Android Application");
	}

	@Override
	public void findExtensions(Class<?> serviceClass, List<Object> extensions) {
		if(serviceClass==CommandsExtension.class) {
			extensions.add(new AppHybridActions());
		}
	}
}
