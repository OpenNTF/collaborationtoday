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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.xsp.ajax.AjaxUtil;
import com.ibm.xsp.renderkit.html_extended.LinkRendererEx;

public class MypicCardRenderer extends LinkRendererEx {

	@Override
	public void encodeBegin(FacesContext paramFacesContext, UIComponent paramUIComponent) throws IOException {
		if ((paramFacesContext == null) || (paramUIComponent == null)) {
			throw new NullPointerException();
		}

		ResponseWriter localResponseWriter = paramFacesContext.getResponseWriter();
		if (AjaxUtil.isAjaxNullResponseWriter(localResponseWriter)) {
			return;
		}

		MypicCard localMypicCard = null;
		if (paramUIComponent instanceof MypicCard) {
			localMypicCard = (MypicCard) paramUIComponent;
		}

		String userName = localMypicCard.getUserName();
		String userId = localMypicCard.getUserId();
		if (userId == null) {
			if (userName == null) {
				return;
			} else {
				if (userName.isEmpty()) {
					return;
				} else {
					userId = UNIDEncoder.encode(userName);
				}
			}
		}
		String serviceUrl = localMypicCard.getServiceUrl();
			
		localResponseWriter.startElement("a", paramUIComponent);
		localResponseWriter.writeAttribute("id", paramUIComponent.getClientId(paramFacesContext), "id");
		localResponseWriter.writeAttribute("href", "#", null);
		localResponseWriter.writeAttribute("class", "myPicViewCard", null);
		localResponseWriter.writeAttribute("onclick", "mypic.showProfileCard('" + userId + "', '" + paramUIComponent.getClientId(paramFacesContext) + "', '" + serviceUrl + "');", null);
		
	}
	
	@Override
	public void encodeEnd(FacesContext paramFacesContext, UIComponent paramUIComponent) throws IOException {
		if ((paramFacesContext == null) || (paramUIComponent == null)) {
			throw new NullPointerException();
		}

		ResponseWriter localResponseWriter = paramFacesContext.getResponseWriter();
		if (AjaxUtil.isAjaxNullResponseWriter(localResponseWriter)) {
			return;
		}

		localResponseWriter.endElement("a");
	}
}
