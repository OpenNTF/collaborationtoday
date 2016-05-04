/*!COPYRIGHT HEADER! 
 *
 */

package org.openntf.ct.app;
/*!COPYRIGHT HEADER! - CONFIDENTIAL 
 *
 * Darwino Inc Confidential.
 *
 * (c) Copyright Darwino Inc 2014-2015.
 *
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has been
 * deposited with the U.S. Copyright Office.     
 */

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIWindow;

import com.darwino.commons.Platform;
import com.darwino.commons.platform.PluginIOS;
import com.darwino.sqlite.IOSInstall;

public class IOSMainClass extends UIApplicationDelegateAdapter {

    private UIWindow window = null;

    @Override
    public boolean didFinishLaunching (UIApplication application, UIApplicationLaunchOptions launchOptions) {
        try {
        	IOSInstall.init();
        } catch(Throwable t) {
        	t.printStackTrace();
        }
    	
		try {
	        AppHybridApplication.create();
		} catch(Throwable t) {
			Platform.log(t);
			return false;
		}

        window = new UIWindow(UIScreen.getMainScreen().getBounds());
        window.setBackgroundColor(UIColor.lightGray());
        UINavigationController navigationController = new UINavigationController(
                application.addStrongRef(new MainViewController()));
        window.setRootViewController(navigationController);
        window.makeKeyAndVisible();

        return true;
    }

    @SuppressWarnings("unused")
	public static void main(String[] args) {
		// Make sure that these plugin are registered
    	Class<?> c1 = PluginIOS.class;
    	Class<?> c2 = AppPlugin.class;
		
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(args, null, IOSMainClass.class);
        pool.close();
    }
}