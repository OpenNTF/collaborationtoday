/*!COPYRIGHT HEADER! 
 *
 */

package org.openntf.ct.app;


import com.darwino.commons.services.HttpServerContext;
import com.darwino.commons.services.HttpServiceFactories;
import com.darwino.mobile.hybrid.platform.NanoHttpdDarwinoHttpServer;
import com.darwino.mobile.hybrid.services.MobileDelegateRestFactory;


public class AppServiceDispatcher extends NanoHttpdDarwinoHttpServer {
	
	public AppServiceDispatcher(HttpServerContext context) {
		super(context);
	}
	
	@Override
	public void addApplicationServiceFactories(HttpServiceFactories factories) {
		// The service should be executed locally or remotely (proxy), depending on the current mode
		factories.add(new MobileDelegateRestFactory(new AppServiceFactory()));
	}
}
