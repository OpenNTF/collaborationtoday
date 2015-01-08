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
package com.ZetaOne.util;

import java.util.Map;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;

import lotus.domino.Database;
import lotus.domino.Session;
import lotus.domino.Name;
import lotus.domino.NotesException;

import com.ibm.xsp.designer.context.XSPContext;

@SuppressWarnings("unchecked")
public class JSFUtil {

	public static XSPContext getContext() {
		return XSPContext.getXSPContext(FacesContext.getCurrentInstance());
	}
	
	public static Map getApplicationScope() {
		return (Map) resolveVariable("applicationScope");
	}
	
	public static Database getCurrentDatabase() {
		return (Database) resolveVariable("database");
	}
	
	public static Map getRequestScope() {
		return (Map) resolveVariable("requestScope");
	}
	
	public static Session getSession() {
		return (Session) resolveVariable("session");
	}

	public static Session getSessionAsSigner() {
		return (Session) resolveVariable("sessionAsSigner");
	}
	
	public static Map getSessionScope() {
		return (Map) resolveVariable("sessionScope");
	}
	
	public static Map getViewScope() {
		return (Map) resolveVariable("viewScope");
	}
	
	public static Name getCurrentUser() {
		Session session = getSession();
		try {
			return session.createName(session.getEffectiveUserName());
		} catch (NotesException e) {
			e.printStackTrace();
			return null;
		}			
	}
	
	public static Object resolveVariable(String variable) {
		return FacesContext.getCurrentInstance().getApplication()
				.getVariableResolver().resolveVariable(
						FacesContext.getCurrentInstance(), variable);
	}
    public static UIComponent findComponent(UIComponent topComponent, String compId) {
        if (compId==null)
                throw new NullPointerException("Component identifier cannot be null");

        if (compId.equals(topComponent.getId()))
                return topComponent;

        if (topComponent.getChildCount()>0) {
                List<UIComponent> childComponents=topComponent.getChildren();

                for (UIComponent currChildComponent : childComponents) {
                        UIComponent foundComponent=findComponent(currChildComponent, compId);
                        if (foundComponent!=null)
                                return foundComponent;
                }
        }
        return null;
    }
}
