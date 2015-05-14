package org.openntf.news.http.core;

/*
 * � Copyright IBM, 2011, 2012
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
 * 
 * Author: Niklas Heidloff - niklas_heidloff@de.ibm.com
 */

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Session;
import net.htmlparser.jericho.CharacterReference;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftTagTypes;
import net.htmlparser.jericho.PHPTagTypes;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import com.ibm.xsp.extlib.util.ExtLibUtil;

public class Parser {

	// Constants
	static public int HTTP_TIMEOUT = 30000; // milliseconds
	static public String HTTP_USER_AGENT = "CollaborationToday";

	// No need to instantiate this class
	private Parser() {
	}

	static public String getOutput(final String urlToIndex) {

		Map<String, Object> viewScope=ExtLibUtil.getViewScope();
		
		String unid = "";
		Document newsEntry = null;
		try {
			MicrosoftTagTypes.register();
			PHPTagTypes.register();
			PHPTagTypes.PHP_SHORT.deregister();
			MasonTagTypes.register();

			URL targetURL = new URL(urlToIndex);
			URLConnection conn = targetURL.openConnection();

			if(viewScope.containsKey("SSLError")) {
				// It means we have experienced handshake error just before this.
				forceTrustSSL((HttpsURLConnection) conn);
				viewScope.remove("SSLError");
			}
			
			conn.setConnectTimeout(HTTP_TIMEOUT);
			conn.setReadTimeout(HTTP_TIMEOUT);
			conn.setRequestProperty("User-Agent", HTTP_USER_AGENT);

			Source source;
			
			try {
				source = new Source(conn);
			} catch (SSLHandshakeException e) {
				viewScope.put("SSLError", "1");
				return "";
			}
			
			source.fullSequentialParse();

			String title = getTitle(source);
			String description = getMetaValue(source, "description");
			if (description == null || description.isEmpty()) {
				description = title;
			}

			Session session = ExtLibUtil.getCurrentSession();
			Database db = ExtLibUtil.getCurrentDatabase();
			newsEntry = db.createDocument();

			newsEntry.appendItemValue("Form", "News");
			newsEntry.appendItemValue("NState", "queued");
			newsEntry.appendItemValue("NID", generateUniqueId());
			newsEntry.appendItemValue("NLink", urlToIndex);
			newsEntry.appendItemValue("PID", "unknown");
			newsEntry.appendItemValue("NTitle", title);
			
			String newsAbstract = description + "\n\n\n" + source.getTextExtractor().setIncludeAttributes(true).toString();
			
			if(newsAbstract.length()>1000) {
				newsAbstract = newsAbstract.substring(0, 1000);
			}
			
			newsEntry.appendItemValue("NAbstract", newsAbstract);
			newsEntry.appendItemValue("NAbstract", "");
			Date date = new Date();
			newsEntry.appendItemValue("NCreationDate", session.createDateTime(date));

			newsEntry.save();

			unid = newsEntry.getUniversalID();
		} catch (MalformedURLException exp1) {
			System.out.println("Invalid URL on Parser: " + urlToIndex);
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			MiscUtils.incinerate(newsEntry);
		}
		return unid;
	}

	private static String generateUniqueId() {
		String allowed = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder sb = new StringBuilder(6);
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			sb.append(allowed.charAt(random.nextInt(allowed.length())));
		}
		return sb.toString().toLowerCase();
	}

	private static String getTitle(final Source s) {
		Element e = s.getFirstElement(HTMLElementName.TITLE);
		if (e == null)
			return null;
		return CharacterReference.decodeCollapseWhiteSpace(e.getContent());
	}

	private static String getMetaValue(final Source s, final String k) {
		for (int p = 0; p < s.length();) {
			StartTag startTag = s.getNextStartTag(p, "name", k, false);
			if (startTag == null)
				return null;
			if (startTag.getName() == HTMLElementName.META)
				return startTag.getAttributeValue("content");
			p = startTag.getEnd();
		}
		return null;
	}

	/**
	 * This method forces any HTTPS connection to skip the CA validation.
	 * 
	 * In CT-like system, it's not a big security problem but THIS MUST NOT BE USED IN PRODUCTION!
	 * 
	 * ...never... ever... please :) 
	 * 
	 * @param con
	 */
	private static void forceTrustSSL(HttpsURLConnection con) {
		try {
			SSLContext sslContext;
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, polyannaTrustManager, new SecureRandom());
			
			con.setSSLSocketFactory(sslContext.getSocketFactory());
			con.setHostnameVerifier(polyannaVerifier);			
			
		} catch (NoSuchAlgorithmException e) {
			// No way!
		} catch (KeyManagementException e) {
			// Failed to trust... Nothing to do...
		}
		
	}
	
	private static final TrustManager[] polyannaTrustManager = new TrustManager[] {
		new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		}
	};
	
	private static final HostnameVerifier polyannaVerifier = new HostnameVerifier() {
		public boolean verify(String arg0, SSLSession arg1) {
			return true; // All people are decent and they won't try to deceive us!
		}
	};
	
}
