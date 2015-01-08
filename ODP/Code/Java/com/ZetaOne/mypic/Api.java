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

import java.util.Map;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.net.URLDecoder;

import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lotus.domino.*;

import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.http.UploadedFile;

import com.ZetaOne.util.JSFUtil;

public class Api {

	private String username = "";

	public Api() throws NotesException {
		this.username = JSFUtil.getCurrentUser().getCanonical();
	}
	public Api(String username) {
		this.username = username;
	}

	public String getDocumentId() throws NotesException {
		Database database = ExtLibUtil.getCurrentDatabase();
		View profiles = database.getView("PersonsAll");
		profiles.setAutoUpdate(false);
		ViewEntry entry = profiles.getEntryByKey(this.username, true);
		String id = null;
		if(entry != null) {
			id = entry.getUniversalID();
		}
		profiles.recycle();
		return id;
	}
	public Document getDocument() throws NotesException {
		String id = this.getDocumentId();
		if(id != null) {
			return ExtLibUtil.getCurrentDatabase().getDocumentByUNID(id);
		}
		return null;
	}

	public void initializeApi() {
		try {
			if (JSFUtil.getContext().getUrl().hasParameter("method")) {
				execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public String checkForUpload(String clientId) {
		try {

			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			UploadedFile fileData =	(UploadedFile) request.getParameterMap().get(clientId);

			if (fileData != null) {
				return getUpload(fileData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	@SuppressWarnings("unchecked")
	public String checkForImport() {
		try {
			Map viewScope = JSFUtil.getViewScope();
			if (viewScope.containsKey("importURL")) {
				return retrieveImage();		
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "xsp-failure";
	}

	public String getUserUNIDWithCreate() {
		try {
			Document doc = this.getDocument();
			if(doc == null) {
				String unid = UNIDEncoder.encode(this.username);
				doc = ExtLibUtil.getCurrentDatabase().createDocument();
				doc.replaceItemValue("Form", "Profile");
				doc.replaceItemValue("Name", this.username);
				doc.replaceItemValue("UserID", unid);
				doc.setUniversalID(unid);
				doc.save();				
			}

			return doc.getUniversalID();
		} catch (NotesException ne) {
			ne.printStackTrace();
			return null;
		}
	}

	public void execute() {
		try {
			String rpc = JSFUtil.getContext().getUrl().getParameter("method").toLowerCase();
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse(); 
			ServletOutputStream responseStream = response.getOutputStream();

			if (rpc.equalsIgnoreCase("getmypic")) {
				getMyPic();
			}

			FacesContext.getCurrentInstance().responseComplete();	
			responseStream.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void getMyPic() {
		try {
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();				
			ServletOutputStream responseStream = response.getOutputStream();

			// 2012-08-29 JGH - Patched in changes from Declan Lynch to send noImage.png from WEB-INF folder if image not found instead of sending 404
			String mypicId = (JSFUtil.getContext().getUrl().hasParameter("id")) ? JSFUtil.getContext().getUrl().getParameter("id") : "";
			Document mypic = null;
			if (mypicId != "") {
				mypic = JSFUtil.getCurrentDatabase().getDocumentByUNID(mypicId);
			}

			String format = (JSFUtil.getContext().getUrl().hasParameter("format")) ? JSFUtil.getContext().getUrl().getParameter("format") : "png";

			BufferedImage img;
			if (null == mypic) {

				// Return the noImage graphic because no document was found for the user
				img = ImageIO.read(JSFUtil.getContext().getFacesContext()
						.getExternalContext().getResourceAsStream("WEB-INF/noImage.png"));

			} else {

				String imageName = (JSFUtil.getContext().getUrl().hasParameter("image"))
				? URLDecoder.decode(JSFUtil.getContext().getUrl().getParameter("image"), "UTF-8")
						: mypic.getItemValueString("currentMypic");

				if ("".equals(imageName)) {
					// return default image if no image specified and no currentMypic specified
					img = ImageIO.read(JSFUtil.getContext().getFacesContext().getExternalContext().getResourceAsStream("WEB-INF/noImage.png"));
				} else {
					// Return the selected image
					EmbeddedObject image = mypic.getAttachment(imageName);
					// return default image not found in document
					if (image == null) {
						img = ImageIO.read(JSFUtil.getContext().getFacesContext().getExternalContext().getResourceAsStream("WEB-INF/noImage.png"));
					} else {
						InputStream imageStream = image.getInputStream();
						img = ImageIO.read(imageStream);
						imageStream.close();
					}
				}
			}

			response.setContentType("image/" + format.toLowerCase());

			Integer w = 0,
			h = 0;

			if (JSFUtil.getContext().getUrl().hasParameter("size")) {
				if (JSFUtil.getContext().getUrl().getParameter("size").toLowerCase().equalsIgnoreCase("auto")) {
					w = img.getWidth();
					h = img.getHeight();
				} else {
					w = new Integer (JSFUtil.getContext().getUrl().getParameter("size"));
					h = new Integer(JSFUtil.getContext().getUrl().getParameter("size"));
				}
			} else {
				w = 64;
				h = 64;
			}

			BufferedImage dst = null;
			if (format.equalsIgnoreCase("JPG")) {
				dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			} else {
				ColorModel dstCM = img.getColorModel();
				dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(w, h), dstCM.isAlphaPremultiplied(), null);
			}

			Graphics2D g = dst.createGraphics();
			g.setComposite(java.awt.AlphaComposite.Src);
			g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING,java.awt.RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

			if (format.equalsIgnoreCase("JPG")) {
				g.drawImage(img, 0, 0, w, h, java.awt.Color.white, null);
			} else {
				g.drawImage(img, 0, 0, w, h, null);
			}
			g.dispose();

			ImageIO.write(dst, format.toUpperCase(), responseStream);
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch (NotesException ne) {
			ne.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public String cropMypic() {
		try {
			String imageName = (String) JSFUtil.getSessionScope().get("lastUpload"); 
			BufferedImage img = null;

			Document doc = this.getDocument();
			RichTextItem rtItem = (RichTextItem) doc.getFirstItem("mypics");

			EmbeddedObject image = rtItem.getEmbeddedObject(imageName);
			InputStream imageStream = image.getInputStream();

			img = ImageIO.read(imageStream);

			imageStream.close();

			Map viewScope = JSFUtil.getViewScope();

			Integer t = new Integer((String) viewScope.get("cropTop")), 
			l = new Integer((String) viewScope.get("cropLeft")),
			w = new Integer((String) viewScope.get("cropWidth")),
			h = new Integer((String) viewScope.get("cropHeight"));

			ColorModel dstCM = img.getColorModel();
			BufferedImage dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(w, h), dstCM.isAlphaPremultiplied(), null);

			Graphics2D g = dst.createGraphics();
			g.drawRenderedImage(img, AffineTransform.getTranslateInstance(-l,-t));
			g.dispose();

			Map sessionScope = JSFUtil.getSessionScope();

			File outputfile = new File(sessionScope.get("absolutePath") + File.separator + sessionScope.get("lastUpload"));

			ImageIO.write(dst, "png", outputfile);
			image.remove();
			doc.save();

			rtItem.embedObject(
					EmbeddedObject.EMBED_ATTACHMENT,
					"",
					sessionScope.get("absolutePath") + File.separator + sessionScope.get("lastUpload"),
					""
			);
			doc.replaceItemValue("currentMypic", sessionScope.get("lastUpload"));
			rtItem.compact();


			// Set the pic type to mypic
			// TODO: Don't do this here
			doc.replaceItemValue("PPictureType", "mypic");

			doc.save();

			DeleteFile.doDelete(sessionScope.get("absolutePath") + File.separator + sessionScope.get("lastUpload"));
			//JSFUtil.getContext().redirectToPage("home.xsp");

		} catch (NotesException ne) {
			ne.printStackTrace();
			return "xsp-failure";
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return "xsp-failure";
		}
		return "xsp-success";
	}

	@SuppressWarnings("unchecked")
	public String getUpload(UploadedFile fileData) {
		try {
			File tempFile = fileData.getServerFile();
			BufferedImage img = null;
			img = ImageIO.read(tempFile);

			// 2012-08-29 JGH - change from int to float to avoid divByZero error
			float w = img.getWidth();
			float h = img.getHeight();

			int nW, nH;
			if (w > h) {
				nW = 512;
				nH = Math.round(h * ((float) nW / w));
			} else {
				nH = 512;
				nW = Math.round(w * ((float) nH / h));					
			}

			ColorModel dstCM = img.getColorModel();
			BufferedImage dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(nW, nH), dstCM.isAlphaPremultiplied(), null);

			Graphics2D g = dst.createGraphics();
			g.setComposite(java.awt.AlphaComposite.Src);
			g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING,java.awt.RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
			g.drawImage(img, 0, 0, nW, nH, null);
			g.dispose();

			String imageName = UNIDEncoder.encode(java.lang.Integer.toString((new java.util.Date()).hashCode()));
			String fileName = imageName + ".png";

			File outputfile = new File(tempFile.getParentFile().getAbsolutePath() + File.separator + fileName);
			ImageIO.write(dst, "png", outputfile);

			//			String uname = this.username;
			//			String unid = UNIDEncoder.encode(uname);
			//			Database database = JSFUtil.getCurrentDatabase();
			//			Document doc = database.getDocumentByUNID(unid);
			//			if (null == doc) {
			//				doc = database.createDocument();
			//				doc.replaceItemValue("Form", "Profile");
			//				doc.replaceItemValue("Name", uname);
			//				doc.replaceItemValue("UserID", unid);
			//				doc.setUniversalID(unid);				
			//			}
			String unid = this.getUserUNIDWithCreate();
			Document doc = ExtLibUtil.getCurrentDatabase().getDocumentByUNID(unid);

			RichTextItem rtItem = (RichTextItem) doc.getFirstItem("mypics");
			if (null == rtItem) {
				rtItem = doc.createRichTextItem("mypics");
			}				

			rtItem.embedObject(
					EmbeddedObject.EMBED_ATTACHMENT,
					"",
					tempFile.getParentFile().getAbsolutePath() + File.separator + fileName,
					""
			);
			rtItem.compact();
			doc.save();

			Item mypicNames = doc.getFirstItem("mypicNames");
			if (null == mypicNames) {
				mypicNames = doc.appendItemValue("myPicNames", fileName);
			} else {
				mypicNames.appendToTextList(fileName);
			}

			doc.save();
			Map sessionScope = JSFUtil.getSessionScope();

			sessionScope.put("absolutePath", tempFile.getParentFile().getAbsolutePath());
			sessionScope.put("lastUpload", fileName);

			//FacesContext.getCurrentInstance().getExternalContext().redirect("cropMypic.xsp");
		} catch (NotesException ne) {
			ne.printStackTrace();
			return null;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return "xsp-success";
	}

	@SuppressWarnings("unchecked")
	public String retrieveImage() {
		try {

			BufferedImage img = null;

			Map requestScope = JSFUtil.getRequestScope();
			Map viewScope = JSFUtil.getViewScope();

			try {
				if (((String)requestScope.get("socialService")).equalsIgnoreCase("twitter")) {
					String atCheck = (String) viewScope.get("importURL");
					if (atCheck.startsWith("@")) {
						atCheck = atCheck.substring(1);
					}
					img = TwitterProfileImage.getImage(atCheck);
				} else if (((String)requestScope.get("socialService")).equalsIgnoreCase("facebook")) {
					img = FacebookProfileImage.getImage((String) viewScope.get("importURL"));
				} else if (((String)requestScope.get("socialService")).equalsIgnoreCase("weburl")) {
					URL url = new URL((String) viewScope.get("importURL"));
					img = ImageIO.read(url);
				}
			} catch (IOException ioe) {
				requestScope.put("imageError","Unable to retreive the image. Check the value below and try again. (" + ioe.toString() + ")");
				ioe.printStackTrace();
				return "xsp-failure";
			}

			if (null == img) {
				requestScope.put("imageError","Unable to retreive the image. Check the value below and try again.");
				return "xsp-failure";	
			}

			float w = img.getWidth();
			float h = img.getHeight();

			int nW = 0;
			int nH = 0;

			if (w > h) {
				nW = 512;
				nH = Math.round(h * ((float)nW / w));
			} else {
				nH = 512;
				nW = Math.round(w * ((float)nH / h));					
			}

			ColorModel dstCM = img.getColorModel();
			BufferedImage dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(nW, nH), dstCM.isAlphaPremultiplied(), null);

			Graphics2D g = dst.createGraphics();
			g.setComposite(java.awt.AlphaComposite.Src);
			g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING,java.awt.RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
			g.drawImage(img, 0, 0, nW, nH, null);
			g.dispose();

			String imageName = UNIDEncoder.encode(java.lang.Integer.toString((new java.util.Date()).hashCode()));
			String fileName = imageName + ".png";

			// Don't assume that xspupload is in the data directory
			String xspupload = ExtLibUtil.getStringProperty(ExtLibUtil.getXspContext(), "xsp.persistence.dir.xspupload");
			if(xspupload == null) {
				xspupload = ".";
			}

			File outputfile = new File(xspupload + File.separator + fileName);
			ImageIO.write(dst, "png", outputfile);

			//			String uname = this.username;
			//			String unid = UNIDEncoder.encode(uname);
			//			Database database = JSFUtil.getCurrentDatabase();
			//			Document doc = database.getDocumentByUNID(unid);
			//			if (null == doc) {
			//				doc = database.createDocument();
			//				doc.replaceItemValue("Form", "Profile");
			//				doc.replaceItemValue("Name", uname);
			//				doc.replaceItemValue("UserID", unid);
			//				doc.setUniversalID(unid);				
			//			}
			String unid = this.getUserUNIDWithCreate();
			Document doc = ExtLibUtil.getCurrentDatabase().getDocumentByUNID(unid);

			RichTextItem rtItem = (RichTextItem) doc.getFirstItem("mypics");
			if (null == rtItem) {
				rtItem = doc.createRichTextItem("mypics");
			}				

			rtItem.embedObject(
					EmbeddedObject.EMBED_ATTACHMENT,
					"",
					outputfile.getAbsolutePath(),
					""
			);
			rtItem.compact();
			doc.save();

			Item mypicNames = doc.getFirstItem("mypicNames");
			if (null == mypicNames) {
				mypicNames = doc.appendItemValue("myPicNames", fileName);
			} else {
				mypicNames.appendToTextList(fileName);
			}

			doc.save();

			Map sessionScope = JSFUtil.getSessionScope();
			sessionScope.put("absolutePath", xspupload);
			sessionScope.put("lastUpload", fileName);

			//FacesContext.getCurrentInstance().getExternalContext().redirect("cropMypic.xsp");

		} catch (NotesException ne) {
			ne.printStackTrace();
			return "xsp-failure";
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return "xsp-failure";
		} catch (Exception e) {
			e.printStackTrace();
			return "xsp-failure";
		}

		return "xsp-success";
	}

}
