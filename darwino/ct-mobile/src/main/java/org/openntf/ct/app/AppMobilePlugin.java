/*!COPYRIGHT HEADER! 
 *
 * (c) Copyright Darwino Inc. 2014-2016.
 *
 * Licensed under The MIT License (https://opensource.org/licenses/MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
 * and associated documentation files (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial 
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE 
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.openntf.ct.app;

import java.util.List;



/**
 * Application Plugin.
 */
public abstract class AppMobilePlugin extends AppBasePlugin {
	
	public AppMobilePlugin(String name) {
		super(name);
	}

	@Override
	public void findExtensions(Class<?> serviceClass, List<Object> extensions) {
		// Example showing how to bypass SSL certificates for development purposes
		// This might be useful when using self signed certificates with local development servers
/*		
		if(serviceClass==SSLCertificateExtension.class) {
			extensions.add(new SSLCertificateExtension() {
				@Override
				public boolean shouldTrust(String url) {
					// Trust local servers
					if(url.startsWith("https://192.168.")) {
						return true;
					}
					return false;
				}
			});
		}
*/		
		super.findExtensions(serviceClass, extensions);
	}
}
