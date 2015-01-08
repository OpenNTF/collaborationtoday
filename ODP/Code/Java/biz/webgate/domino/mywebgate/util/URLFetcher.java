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
package biz.webgate.domino.mywebgate.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class URLFetcher { 

	private String m_URL;
	private String m_Title = "";
	private String m_Description = "";
	private String m_URLContent = "";
	private ArrayList<String> m_ThumbNails = new ArrayList<String>();

	private Exception m_Exception;
	private String m_Error;

	private HashMap<String, String> m_OpenGraph = new HashMap<String, String>();

	public URLFetcher(String url) {
		super();
		m_URL = url;
	}

	public String getURL() {
		return m_URL;
	}

	public String getTitle() {
		return m_Title;
	}

	public String getDescription() {
		return m_Description == null ? "" : m_Description;
	}

	public ArrayList<String> getThumbNails() {
		return m_ThumbNails;
	}

	public boolean fetchURL() {
		try {
			HttpClient httpClient = new DefaultHttpClient();

			HttpGet httpGet = new HttpGet(m_URL);
			httpGet.addHeader("Content-Type", "text/html; charset=utf-8");
			HttpResponse response = httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();

			String strBaseURL = m_URL;
			if (strBaseURL.lastIndexOf("/") > 8) {
				strBaseURL = strBaseURL.substring(0, strBaseURL
						.lastIndexOf("/"));
			}

			Document doc = null;
			if (statusCode == 200) {
				m_ThumbNails.add("no image");
				HttpEntity entity = response.getEntity();
				// String content = EntityUtils.toString(entity);

				try {
					DOMParser dpHTML = new DOMParser();
					dpHTML
							.setProperty(
									"http://cyberneko.org/html/properties/default-encoding",
									"utf-8");
					// dpHTML.parse(new
					// InputSource(EntityUtils.toString(entity)));
					dpHTML.parse(new InputSource(entity.getContent()));
					doc = dpHTML.getDocument();
					NodeList ndlMet = doc.getElementsByTagName("meta");
					NodeList ndlTitle = doc.getElementsByTagName("title");
					NodeList ndlImage = doc.getElementsByTagName("img");
					check4OpenGraphTags(ndlMet, strBaseURL);
					if (m_Description.equals("")) {
						for (int nCounter = 0; nCounter < ndlMet.getLength(); nCounter++) {
							Element elCurrent = (Element) ndlMet.item(nCounter);
							if ("description".equalsIgnoreCase(elCurrent
									.getAttribute("name"))) {
								if (elCurrent.hasAttribute("content")) {
									m_Description = elCurrent
											.getAttribute("content");
									nCounter = ndlMet.getLength();
								}
							}
						}
					}
					if (ndlTitle.getLength() > 0 && m_Title.equals("")) {
						m_Title = ((Element) ndlTitle.item(0)).getFirstChild()
								.getNodeValue();
					}
					for (int nCounter = 0; nCounter < ndlImage.getLength(); nCounter++) {
						Element elCurrent = (Element) ndlImage.item(nCounter);
						if (elCurrent.hasAttribute("src")) {
							String strImage = elCurrent.getAttribute("src");
							if (ndlImage.getLength() > 20
									&& elCurrent.hasAttribute("height")) {
								String strHeight = elCurrent
										.getAttribute("height");
								strHeight.replace("px", "");
								try {
									int nHeight = Integer.parseInt(strHeight);
									if (nHeight > 200) {
										strImage = null;
									}
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
							if (strImage != null) {
								strImage = checkIMAGEURL(strImage, strBaseURL);

								if (!m_ThumbNails.contains(strImage)) {
									m_ThumbNails.add(strImage);
								}
							}
						}
					}
				} catch (IllegalStateException e) {
					m_Error = e.getLocalizedMessage();
					m_Exception = e;
					e.printStackTrace();
				} catch (SAXException e) {
					m_Error = e.getLocalizedMessage();
					m_Exception = e;
					e.printStackTrace();
				} finally {
					httpClient.getConnectionManager().shutdown();
				}

			}

		} catch (Exception e) {
			m_Error = e.getLocalizedMessage();
			m_Exception = e;
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void check4OpenGraphTags(NodeList ndlMeta, String strBaseURL) {
		for (int nCounter = 0; nCounter < ndlMeta.getLength(); nCounter++) {
			Element elMeta = (Element) ndlMeta.item(nCounter);
			// Test if property is available
			if (elMeta.hasAttribute("property")) {
				String strProperty = elMeta.getAttribute("property");
				// CHECK if we have a OpenGraphProperty
				if (strProperty.toLowerCase().startsWith("og:")) {
					m_OpenGraph
							.put(strProperty, elMeta.getAttribute("content"));
				}
				if ("og:image".equalsIgnoreCase(strProperty)) {
					String strImage = checkIMAGEURL(elMeta.getAttribute("content"), strBaseURL);
					m_ThumbNails.add(strImage);
				}
				if ("og:title".equalsIgnoreCase(strProperty)) {
					m_Title = elMeta.getAttribute("content");
				}
				if ("og:url".equalsIgnoreCase(strProperty)) {
					m_URLContent = elMeta.getAttribute("content");
				}
				if ("og:description".equalsIgnoreCase(strProperty)) {
					m_Description = elMeta.getAttribute("content");
				}
			}
		}
	}

	public Exception getException() {
		return m_Exception;
	}

	public String getError() {
		return m_Error;
	}

	public String getURLContent() {
		return m_URLContent;
	}

	public HashMap<String, String> getOpenGraph() {
		return m_OpenGraph;
	}

	private String checkIMAGEURL(String strImage, String strBaseURL) {
		String strRC = strImage;
		if (strRC.startsWith("//")) {
			strRC = "http:" + strRC;
		}
		if (!strRC.toLowerCase().startsWith("http://")) {
			strRC = strBaseURL + "/" + strRC;
		}

		return strRC;

	}
}
