/*
 * © Copyright 2012 ZetaOne Solutions Group, LLC
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
package com.ZetaOne.mypic;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLSocketFactory;

public class FacebookProfileImage {
	public static final String TARGET_HTTPS_SERVER = "graph.facebook.com";
	public static final int TARGET_HTTPS_PORT = 443;

	public static BufferedImage getImage(String userName) throws Exception {

		Socket socket = SSLSocketFactory.getDefault().createSocket(TARGET_HTTPS_SERVER, TARGET_HTTPS_PORT);

		try {

			socket.setSoTimeout(1000);

			Writer out = new OutputStreamWriter(socket.getOutputStream(), "ISO-8859-1");

			out.write("GET /" + userName + "/picture?type=large HTTP/1.0\r\n");
			out.write("Host: " + TARGET_HTTPS_SERVER + ":" + TARGET_HTTPS_PORT + "\r\n");
			out.write("\r\n");
			out.flush();

			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "ISO-8859-1"));

			String RawResponse = "";
			String line = null;

			while ((line = in.readLine()) != null) {
				RawResponse += line + "&";
			}

			String redirectAddress = "";
			String[] response = RawResponse.split("&");
			for (int i = 0; i < response.length; ++i) {
				if (response[i].startsWith("Location:")) {
					redirectAddress = response[i].substring(9);
					break;
				}
			}

			out.close();
			in.close();

			URL url = new URL(redirectAddress);
			BufferedImage img = ImageIO.read(url);

			return img;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			socket.close();
		}

		return null;

	}
}
