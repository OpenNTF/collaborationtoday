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

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.component.xp.XspOutputLink;

public class MypicCard extends XspOutputLink {

	public static final String COMPONENT_TYPE = "com.ZetaOne.mypic.MypicCard";
	public static final String RENDERER_TYPE = "com.ZetaOne.xsp.mypic.MypicCard";
	protected String UserName;
	protected String UserId;
	protected String ServiceUrl;
	protected String Text;

	public MypicCard() {
		setRendererType(MypicCard.RENDERER_TYPE);
	}

	@Override
	public Object getValue() {
		return "";
	}

	/**
	 * UserName
	 * 
	 * @return
	 */
	public String getUserName() {
		if (this.UserName != null) {
			return this.UserName;
		}

		ValueBinding vb = getValueBinding("UserName");
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setUserName(String userName) {
		this.UserName = userName;
	}

	/**
	 * UserID
	 * 
	 * @return
	 */
	public String getUserId() {
		if (this.UserId != null) {
			return this.UserId;
		}

		ValueBinding vb = getValueBinding("UserId");
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setUserId(String userName) {
		this.UserId = userName;
	}

	/**
	 * ServiceUrl
	 * 
	 * @return
	 */
	public String getServiceUrl() {
		if (this.ServiceUrl != null) {
			return this.ServiceUrl;
		}

		ValueBinding vb = getValueBinding("ServiceUrl");
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setServiceUrl(String serviceUrl) {
		this.ServiceUrl = serviceUrl;
	}

	/**
	 * Text
	 * 
	 * @return
	 */
	public String getText() {
		if (this.Text != null) {
			return this.Text;
		}

		ValueBinding vb = getValueBinding("Text");
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setText(String text) {
		this.Text = text;
	}	
	
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
		this.UserName = (String) _values[1];
		this.ServiceUrl = (String) _values[2];
		this.UserId = (String) _values[3];
		this.Text = (String) _values[4];
	}

	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[5];
		_values[0] = super.saveState(_context);
		_values[1] = this.UserName;
		_values[2] = this.ServiceUrl;
		_values[3] = this.UserId;
		_values[4] = this.Text;
		return _values;
	}
}
