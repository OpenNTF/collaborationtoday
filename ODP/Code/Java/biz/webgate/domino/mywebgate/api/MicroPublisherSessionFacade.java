 /*
 * © Copyright WebGate Consulting AG, 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */
package biz.webgate.domino.mywebgate.api;

import biz.webgate.domino.mywebgate.util.jsonparser.URLFetcherJSONParser;

import com.ibm.domino.services.HttpServiceConstants;
import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.rest.RestServiceEngine;
import com.ibm.xsp.extlib.component.rest.CustomService;
import com.ibm.xsp.extlib.component.rest.CustomServiceBean;

public class MicroPublisherSessionFacade extends CustomServiceBean {

	@Override
	public void renderService(CustomService service, RestServiceEngine engine)
			throws ServiceException {
		service
				.setContentType(HttpServiceConstants.CONTENTTYPE_APPLICATION_JSON_UTF8);
		if ("/micropublisher/fetchLink".equals(engine.getHttpRequest()
						.getPathInfo())) {
			URLFetcherJSONParser.getInstance().fetchURL(engine);
		}
		// TODO: Handle wrong URL
	}
}
