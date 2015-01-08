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
package biz.webgate.domino.mywebgate.util.jsonparser;

import java.io.Reader;
import java.util.Iterator;

import biz.webgate.domino.mywebgate.util.URLFetcher;
import biz.webgate.domino.mywebgate.util.builder.ErrorJSONBuilder;

import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonParser;
import com.ibm.domino.services.rest.RestServiceEngine;
import com.ibm.domino.services.util.JsonWriter;

public class URLFetcherJSONParser {

	private static URLFetcherJSONParser m_Parser;

	private URLFetcherJSONParser() {

	}

	public static synchronized URLFetcherJSONParser getInstance() {
		if (m_Parser == null) {
			m_Parser = new URLFetcherJSONParser();
		}
		return m_Parser;
	}

	public void fetchURL(RestServiceEngine engine) {
		JsonJavaObject json = null;
		JsonJavaFactory factory = JsonJavaFactory.instanceEx;

		try {
			Reader r = engine.getHttpRequest().getReader();
			json = (JsonJavaObject) JsonParser.fromJson(factory, r);
			String strMethod = "url.fetchmetadata"; //json.getString("method");
			String strLink = json.getString("link");
			if ("url.fetchmetadata".equalsIgnoreCase(strMethod)) {
				URLFetcher urlFetcher = new URLFetcher(strLink);
				if (urlFetcher.fetchURL()) {
					JsonWriter jsWriter = new JsonWriter(engine
							.getHttpResponse().getWriter(), true);
					jsWriter.startObject();
					jsWriter.startProperty("result");
					jsWriter.outStringLiteral("ok");
					jsWriter.endProperty();

					// Title
					jsWriter.startProperty("title");
					jsWriter.outStringLiteral(urlFetcher.getTitle());
					jsWriter.endProperty();
					// Description
					jsWriter.startProperty("desc");
					jsWriter.outStringLiteral(urlFetcher.getDescription());
					jsWriter.endProperty();
					// url
					jsWriter.startProperty("url");
					jsWriter.outStringLiteral(urlFetcher.getURL());
					jsWriter.endProperty();

					jsWriter.startProperty("images");
					jsWriter.startArray();

					for (Iterator<String> itImg = urlFetcher.getThumbNails()
							.iterator(); itImg.hasNext();) {
						jsWriter.startArrayItem();
						jsWriter.outStringLiteral(itImg.next());
						jsWriter.endArrayItem();
					}
					jsWriter.endArray();

					jsWriter.endProperty();

					jsWriter.startProperty("opengraph");
					if (urlFetcher.getOpenGraph().size() > 0) {

						jsWriter.startObject();

						for (Iterator<String> itOG = urlFetcher.getOpenGraph()
								.keySet().iterator(); itOG.hasNext();) {
							String strOGTag = itOG.next();
							jsWriter.startProperty(strOGTag);
							jsWriter.outStringLiteral(urlFetcher.getOpenGraph()
									.get(strOGTag));
							jsWriter.endProperty();
						}
						jsWriter.endObject();
					} else {
						jsWriter.outStringLiteral("");
					}
					jsWriter.endProperty();
					jsWriter.endObject();

					jsWriter.close();
					return;
				} else {
					ErrorJSONBuilder.getInstance().processError2JSON(engine,
							5001, urlFetcher.getError(),
							urlFetcher.getException());
					return;
				}

			}
			ErrorJSONBuilder.getInstance().processError2JSON(engine, 5002,
					"Unknown Command " + strMethod, null);

		} catch (Exception e) {
			ErrorJSONBuilder.getInstance().processError2JSON(engine, 5999,
					"Error during URLFetcherJSONParser", e);
		}
	}
}
