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

import javax.faces.render.Renderer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.xsp.ajax.AjaxUtil;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.designer.context.XSPContext;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.resource.ScriptResource;

import com.ZetaOne.mypic.MypicLoginControl;

public class MypicLoginControlRenderer extends Renderer {

	public void encodeBegin(FacesContext paramFacesContext,
			UIComponent paramUIComponent) throws IOException {
		if ((paramFacesContext == null) || (paramUIComponent == null)) {
			throw new NullPointerException();
		}
	}

	public void encodeChildren(FacesContext paramFacesContext,
			UIComponent paramUIComponent) {
		if ((paramFacesContext == null) || (paramUIComponent == null))
			throw new NullPointerException();
	}

	public void encodeEnd(FacesContext paramFacesContext, UIComponent paramUIComponent) throws IOException
	{
		if ((paramFacesContext == null) || (paramUIComponent == null)) {
			throw new NullPointerException();
		}
		 
		ResponseWriter writer = paramFacesContext.getResponseWriter();
		if (AjaxUtil.isAjaxNullResponseWriter(writer)) {
			return;
		}
		 
		MypicLoginControl loginControl = null;
		if (paramUIComponent instanceof MypicLoginControl) {
			loginControl = (MypicLoginControl) paramUIComponent;
		}
		 
		if (null == loginControl) {
			return;
		}
		 
		writer.startElement("div", paramUIComponent);
		writer.writeAttribute("id", paramUIComponent.getClientId(paramFacesContext), "id");
		if (loginControl.getContainerStyleClass() != null)
			writer.writeAttribute("class", loginControl.getContainerStyleClass(), null);
		 
		if (loginControl.getContainerStyle() != null)
			writer.writeAttribute("style", loginControl.getContainerStyle(), null);
		 
		/* mypic image */
		boolean isAnonymous = true;
		String commonName = "";
		String canonicalName = "";
		XSPContext ctx = XSPContext.getXSPContext(FacesContext.getCurrentInstance());
		if(ctx!=null) {
			com.ibm.designer.runtime.directory.DirectoryUser user = ctx.getUser();
			isAnonymous = user.isAnonymous();
			commonName = user.getCommonName();
			canonicalName = user.getFullName();
		} else {
			isAnonymous = false;
		}
		 		
		if (!isAnonymous) {
			String size = loginControl.getImageSize();
			String format = loginControl.getFormat();
			String imageName = loginControl.getImageName();
			String userId = "";
			if (!canonicalName.isEmpty()) {
				userId = UNIDEncoder.encode(canonicalName);
			}

			if (!userId.isEmpty()) {
				size = (size == null) ? "" : size;
				format = (format == null) ? "" : format;
				imageName = (imageName == null) ? "" : imageName;
				String str1 = loginControl.getServiceUrl() + "?method=getmypic&id=" + userId + (size.isEmpty() ? "" : "&size=" + size)
						+ (format.isEmpty() ? "" : "&format=" + format) + (imageName.isEmpty() ? "" : "&image=" + imageName)
						+ (imageName.isEmpty() ? "&cache=" + new Integer((new Date()).hashCode()).toString() : "");
		
				writer.startElement("img", paramUIComponent);
				writer.writeAttribute("id", paramUIComponent.getClientId(paramFacesContext), "id");
				writer.writeAttribute("src", str1, null);

				if (loginControl.getMypicStyleClass() != null)
					writer.writeAttribute("class", loginControl.getMypicStyleClass(), null);
				 
				if (loginControl.getMypicStyle() != null)
					writer.writeAttribute("style", loginControl.getMypicStyle(), null);
				
				writer.endElement("img");
				
			}
		}
		
		/* Welcome Phrase */
		writer.startElement("span", null);
		writer.writeAttribute("id", paramUIComponent.getClientId(paramFacesContext) + ":welcomePhrase", "id");
		 
		if (loginControl.getWelcomePhraseStyleClass() != null)
			writer.writeAttribute("class", loginControl.getWelcomePhraseStyleClass(), null);
		 
		if (loginControl.getWelcomePhraseStyle() != null)
			writer.writeAttribute("style", loginControl.getWelcomePhraseStyle(), null);
		
		writer.write(loginControl.getWelcomePhrase());
		 
		writer.endElement("span");
		 
		/* User Name */
		writer.startElement("span", null);
		writer.writeAttribute("id", paramUIComponent.getClientId(paramFacesContext) + ":userName", "id");
		 
		if (loginControl.getUserNameTextStyleClass() != null)
			writer.writeAttribute("class", loginControl.getUserNameTextStyleClass(), null);

		if (loginControl.getUserNameTextStyle() != null)
			writer.writeAttribute("style", loginControl.getUserNameTextStyle(), null);
		 

		// here
		if (!isAnonymous) {
			writer.write(commonName);
		} else {
			writer.write("Anonymous");
		}
	
		
		
		writer.endElement("span");		 
		writer.write(" ");
		 
		/* Log In / Log Out Action */	     
		writer.startElement("a", null);
		writer.writeAttribute("id", paramUIComponent.getClientId(paramFacesContext) + ":authLink", "id");
		if (isAnonymous) {
			writer.writeAttribute("href", "javascript:true;", null);
			writer.writeAttribute("onclick",
					"dijit.popup.open({ around: dojo.byId('" + paramUIComponent.getClientId(paramFacesContext) + ":authLink')," +
					"popup: dijit.byId('" + paramUIComponent.getClientId(paramFacesContext) + ":loginDialog')," +
					"orient: {BR: 'TR', BL: 'TL'}});", null);
		} else {
			writer.writeAttribute("href", "/names.nsf?logout", null);
		}

		if (loginControl.getLogInOutTextStyleClass() != null)
			writer.writeAttribute("class", loginControl.getLogInOutTextStyleClass(), null);
		 
		if (loginControl.getLogInOutTextStyle() != null)		 
			writer.writeAttribute("style", loginControl.getLogInOutTextStyle(), null);
		 
		if (isAnonymous) {
			if (loginControl.getLoginPhrase() == null) {
				writer.write("Log In");
			} else {
				writer.write(loginControl.getLoginPhrase());
			}
		 } else {
			if (loginControl.getLogoutPhrase() == null) {
				writer.write("Log Out");
			} else {
				writer.write(loginControl.getLogoutPhrase());
			}
		}

		writer.endElement("a");	 
		writer.endElement("div");
		 
        if (isAnonymous) {
            // Add the dijit.TooltipDialog module
            DojoModuleResource module = new DojoModuleResource("dijit.TooltipDialog");
            UIViewRootEx rootEx = (UIViewRootEx)paramFacesContext.getViewRoot();
            rootEx.addEncodeResource(module);

            ScriptResource res = new ScriptResource("mypic.js", true);
            rootEx.addEncodeResource(res);            
            
        	writer.startElement("div", null);
        	writer.writeAttribute("id", paramUIComponent.getClientId(paramFacesContext) + ":loginDialog", "id");
        	writer.writeAttribute("style", "display:none;", null);
        	writer.writeAttribute("closable", "true", null);

        	writer.writeAttribute("dojoType", "dijit.TooltipDialog", null);
        	writer.startElement("script", null);
        	writer.writeAttribute("type", "dojo/connect", null);
        	writer.writeAttribute("event", "onBlur", null);
        	writer.write("dijit.popup.close(this);");
        	writer.endElement("script");
        	        	
        	writer.startElement("table", null);
        	writer.writeAttribute("id", paramUIComponent.getClientId(paramFacesContext) + ":loginDialog:dataTable", "id");
        	writer.startElement("tr", null);
        	writer.startElement("td", null);
        	writer.writeAttribute("colspan","3",null);

        	writer.startElement("div", null);
        	writer.writeAttribute("style", "color:red;", "");
        	writer.writeAttribute("id", paramUIComponent.getClientId(paramFacesContext) + ":loginDialog:errorPanel", "id");
        	writer.endElement("div");

        	writer.endElement("td");
        	writer.endElement("tr");
        	writer.startElement("tr", null);
        	writer.startElement("td", null);
        	writer.write("user");
        	writer.endElement("td");
        	writer.startElement("td", null);
        	writer.write("&nbsp;");
        	writer.endElement("td");        	
        	writer.startElement("td", null);
        	writer.startElement("input", null);
        	writer.writeAttribute("id", paramUIComponent.getClientId(paramFacesContext) + ":loginDialog:dataTable:userInput", "id");
        	writer.writeAttribute("type","text",null);
        	writer.endElement("input");
        	writer.endElement("td");
        	writer.endElement("tr");
        	writer.startElement("tr", null);
        	writer.startElement("td", null);
        	writer.write("password");
        	writer.endElement("td");
        	writer.startElement("td", null);
        	writer.write("&nbsp;");
        	writer.endElement("td");        	
        	writer.startElement("td", null);
        	writer.startElement("input", null);
        	writer.writeAttribute("id", paramUIComponent.getClientId(paramFacesContext) + ":loginDialog:dataTable:passInput", "id");        	
        	writer.writeAttribute("type","password",null);        	
        	writer.endElement("input");
        	writer.endElement("td");
        	writer.endElement("tr");
        	writer.startElement("tr", null);
        	writer.startElement("td", null);
        	writer.write("&nbsp;");
        	writer.endElement("td");	
        	writer.startElement("td", null);
        	writer.write("&nbsp;");
        	writer.endElement("td");
        	writer.startElement("td", null);
        	writer.writeAttribute("align","right",null);
        	writer.startElement("input", null);
        	writer.writeAttribute("type","button",null);
        	writer.writeAttribute("value","Log In",null);
        	writer.writeAttribute("onclick", "mypic.doLogin(dojo.byId('" + 
        			paramUIComponent.getClientId(paramFacesContext) + ":loginDialog:dataTable:userInput" + 
        			"').value, dojo.byId('" + 
        			paramUIComponent.getClientId(paramFacesContext) + ":loginDialog:dataTable:passInput" + 
        			"').value, dojo.byId('" +
        			paramUIComponent.getClientId(paramFacesContext) + ":loginDialog:errorPanel" +
        			"'), '" +
        			loginControl.getLoginSuccessUrl() +
        			"')", null);
        	writer.endElement("input");
        	writer.endElement("td");
        	writer.endElement("tr");        	
        	writer.endElement("table");

        	writer.startElement("input", null);
        	writer.writeAttribute("id", paramUIComponent.getClientId(paramFacesContext) + ":loginDialog:dataTable:maintainLogin", "id");        	
        	writer.writeAttribute("type", "checkbox", null);
        	writer.endElement("input");
        	writer.startElement("label", null);
        	writer.writeAttribute("for", paramUIComponent.getClientId(paramFacesContext) + ":loginDialog:dataTable:maintainLogin", null);
        	writer.write("Keep me logged in.");
        	writer.endElement("label");
        	
        	writer.endElement("div");
        }
        
	}
}
