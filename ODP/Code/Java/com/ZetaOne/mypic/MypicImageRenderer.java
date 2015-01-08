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

import java.io.IOException;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.ajax.AjaxUtil;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.renderkit.html_extended.ImageExRenderer;
import com.ibm.xsp.renderkit.html_extended.RenderUtil;

import lotus.domino.*;

public class MypicImageRenderer extends ImageExRenderer {

	@Override
	public void encodeEnd(FacesContext paramFacesContext, UIComponent paramUIComponent) throws IOException {
		if ((paramFacesContext == null) || (paramUIComponent == null)) {
			throw new NullPointerException();
		}

		ResponseWriter localResponseWriter = paramFacesContext.getResponseWriter();
		if (AjaxUtil.isAjaxNullResponseWriter(localResponseWriter)) {
			return;
		}

		MypicImage localXspGraphicImage = null;
		if (paramUIComponent instanceof MypicImage) {
			localXspGraphicImage = (MypicImage) paramUIComponent;
		}

		String size = localXspGraphicImage.getImageSize();
		String format = localXspGraphicImage.getFormat();
		String imageName = localXspGraphicImage.getImageName();
		String userName = localXspGraphicImage.getUserName();
		String userId = localXspGraphicImage.getUserId();
		if (userId == null) {
			if (userName == null) {
				return;
			} else {
				if (userName.isEmpty()) {
					return;
				} else {
					//userId = UNIDEncoder.encode(userName);
					try {
						View profiles = ExtLibUtil.getCurrentDatabase().getView("PersonsAll");
						profiles.setAutoUpdate(false);
						ViewEntry profileEntry = profiles.getEntryByKey(userName);
						userId = profileEntry.getUniversalID();
						profileEntry.recycle();
						profiles.recycle();
					} catch(NotesException ne) {
						ne.printStackTrace();
					}
				}
			}
		}

		size = (size == null) ? "" : size;
		format = (format == null) ? "" : format;
		imageName = (imageName == null) ? "" : imageName;
		String str1 = localXspGraphicImage.getServiceUrl() + "?method=getmypic&id=" + userId + (size.isEmpty() ? "" : "&size=" + size)
		+ (format.isEmpty() ? "" : "&format=" + format) + (imageName.isEmpty() ? "" : "&image=" + imageName)
		+ (imageName.isEmpty() ? "&cache=" + new Integer((new Date()).hashCode()).toString() : "");

		// String str1 = (localXspGraphicImage != null) ? (String)
		// localXspGraphicImage.getValue() : (String)
		// paramUIComponent.getAttributes().get("value");

		if (StringUtil.isEmpty(str1)) {
			return;
		}

		localResponseWriter.startElement("img", paramUIComponent);
		localResponseWriter.writeAttribute("id", paramUIComponent.getClientId(paramFacesContext), "id");
		localResponseWriter.writeAttribute("src", str1, null);
		String str2;
		if (localXspGraphicImage != null) {
			str2 = localXspGraphicImage.getAlt();
			if (str2 != null) {
				localResponseWriter.writeAttribute("alt", str2, null);
			} else {
				localResponseWriter.writeAttribute("alt", "", null);
			}

			str2 = localXspGraphicImage.getDir();
			if (str2 != null) {
				localResponseWriter.writeAttribute("dir", str2, null);
			}
			str2 = localXspGraphicImage.getRole();
			if (str2 != null) {
				localResponseWriter.writeAttribute("role", str2, null);
			}
			str2 = localXspGraphicImage.getHeight();
			if (str2 != null) {
				localResponseWriter.writeAttribute("height", str2, null);
			}
			if (localXspGraphicImage.isIsmap()) {
				localResponseWriter.writeAttribute("ismap", "ismap", null);
			}
			str2 = localXspGraphicImage.getLang();
			if (str2 != null) {
				localResponseWriter.writeAttribute("lang", str2, null);
			}
			str2 = localXspGraphicImage.getLongdesc();
			if (str2 != null) {
				localResponseWriter.writeAttribute("longdesc", str2, null);
			}
			str2 = localXspGraphicImage.getOnblur();
			if (str2 != null) {
				localResponseWriter.writeAttribute("onblur", str2, null);
			}
			str2 = localXspGraphicImage.getOnclick();
			if (str2 != null) {
				localResponseWriter.writeAttribute("onclick", str2, null);
			}
			str2 = localXspGraphicImage.getOndblclick();
			if (str2 != null) {
				localResponseWriter.writeAttribute("ondblclick", str2, null);
			}
			str2 = localXspGraphicImage.getOnkeydown();
			if (str2 != null) {
				localResponseWriter.writeAttribute("onkeydown", str2, null);
			}
			str2 = localXspGraphicImage.getOnkeypress();
			if (str2 != null) {
				localResponseWriter.writeAttribute("onkeypress", str2, null);
			}
			str2 = localXspGraphicImage.getOnkeyup();
			if (str2 != null) {
				localResponseWriter.writeAttribute("onkeyup", str2, null);
			}
			str2 = localXspGraphicImage.getOnmousedown();
			if (str2 != null) {
				localResponseWriter.writeAttribute("onmousedown", str2, null);
			}
			str2 = localXspGraphicImage.getOnmousemove();
			if (str2 != null) {
				localResponseWriter.writeAttribute("onmousemove", str2, null);
			}
			str2 = localXspGraphicImage.getOnmouseout();
			if (str2 != null) {
				localResponseWriter.writeAttribute("onmouseout", str2, null);
			}
			str2 = localXspGraphicImage.getOnmouseover();
			if (str2 != null) {
				localResponseWriter.writeAttribute("onmouseover", str2, null);
			}
			str2 = localXspGraphicImage.getOnmouseup();
			if (str2 != null) {
				localResponseWriter.writeAttribute("onmouseup", str2, null);
			}
			str2 = localXspGraphicImage.getStyle();
			if (str2 != null) {
				localResponseWriter.writeAttribute("style", str2, null);
			}
			str2 = localXspGraphicImage.getTitle();
			if (str2 != null) {
				localResponseWriter.writeAttribute("title", str2, null);
			}
			str2 = localXspGraphicImage.getUsemap();
			if (str2 != null) {
				localResponseWriter.writeAttribute("usemap", str2, null);
			}
			str2 = localXspGraphicImage.getWidth();
			if (str2 != null) {
				localResponseWriter.writeAttribute("width", str2, null);
			}

		} else {
			str2 = (String) paramUIComponent.getAttributes().get("alt");
			if (str2 != null) {
				localResponseWriter.writeAttribute("alt", str2, "alt");
			}
			String str3 = (String) paramUIComponent.getAttributes().get("dir");
			if (str3 != null) {
				localResponseWriter.writeAttribute("dir", str3, "dir");
			}
			String str4 = (String) paramUIComponent.getAttributes().get("role");
			if (str4 != null) {
				localResponseWriter.writeAttribute("role", str4, "role");
			}
			String str5 = (String) paramUIComponent.getAttributes().get("height");
			if (str5 != null) {
				localResponseWriter.writeAttribute("height", str5, "height");
			}
			String str6 = (String) paramUIComponent.getAttributes().get("width");
			if (str6 != null) {
				localResponseWriter.writeAttribute("width", str6, "width");
			}
			String str7 = (String) paramUIComponent.getAttributes().get("lang");
			if (str7 != null) {
				localResponseWriter.writeAttribute("lang", str7, "lang");
			}
			String str8 = (String) paramUIComponent.getAttributes().get("longdesc");
			if (str8 != null) {
				localResponseWriter.writeAttribute("longdesc", str8, "longdesc");
			}
			String str9 = (String) paramUIComponent.getAttributes().get("onblur");
			if (str9 != null) {
				localResponseWriter.writeAttribute("onblur", str9, "onblur");
			}
			String str10 = (String) paramUIComponent.getAttributes().get("onclick");
			if (str10 != null) {
				localResponseWriter.writeAttribute("onclick", str10, "onclick");
			}
			String str11 = (String) paramUIComponent.getAttributes().get("ondblclick");
			if (str11 != null) {
				localResponseWriter.writeAttribute("ondblclick", str11, "ondblclick");
			}
			String str12 = (String) paramUIComponent.getAttributes().get("onkeydown");
			if (str12 != null) {
				localResponseWriter.writeAttribute("onkeydown", str12, "onkeydown");
			}
			String str13 = (String) paramUIComponent.getAttributes().get("onkeypress");
			if (str13 != null) {
				localResponseWriter.writeAttribute("onkeypress", str13, "onkeypress");
			}
			String str14 = (String) paramUIComponent.getAttributes().get("onkeyup");
			if (str14 != null) {
				localResponseWriter.writeAttribute("onkeyup", str14, "onkeyup");
			}
			String str15 = (String) paramUIComponent.getAttributes().get("onmousedown");
			if (str15 != null) {
				localResponseWriter.writeAttribute("onmousedown", str15, "onmousedown");
			}
			String str16 = (String) paramUIComponent.getAttributes().get("onmousemove");
			if (str16 != null) {
				localResponseWriter.writeAttribute("onmousemove", str16, "onmousemove");
			}
			String str17 = (String) paramUIComponent.getAttributes().get("onmouseout");
			if (str17 != null) {
				localResponseWriter.writeAttribute("onmouseout", str17, "onmouseout");
			}
			String str18 = (String) paramUIComponent.getAttributes().get("onmouseover");
			if (str18 != null) {
				localResponseWriter.writeAttribute("onmouseover", str18, "onmouseover");
			}
			String str19 = (String) paramUIComponent.getAttributes().get("onmouseup");
			if (str19 != null) {
				localResponseWriter.writeAttribute("onmouseup", str19, "onmouseup");
			}
			String str20 = (String) paramUIComponent.getAttributes().get("style");
			if (str20 != null) {
				localResponseWriter.writeAttribute("style", str20, "style");
			}
			String str21 = (String) paramUIComponent.getAttributes().get("title");
			if (str21 != null) {
				localResponseWriter.writeAttribute("title", str21, "title");
			}
			String str22 = (String) paramUIComponent.getAttributes().get("usemap");
			if (str22 != null) {
				localResponseWriter.writeAttribute("usemap", str22, "usemap");
			}
			Object localObject = paramUIComponent.getAttributes().get("ismap");
			if ((localObject != null) && (RenderUtil.getBooleanValue(localObject))) {
				localResponseWriter.writeAttribute("ismap", "ismap", null);
			}

		}

		if (localXspGraphicImage != null) {
			str2 = localXspGraphicImage.getStyleClass();
		} else {
			str2 = (String) paramUIComponent.getAttributes().get("styleClass");
		}

		if (str2 != null) {
			localResponseWriter.writeAttribute("class", str2, "styleClass");
		}

		localResponseWriter.endElement("img");
	}
}
