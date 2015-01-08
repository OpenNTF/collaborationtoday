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

import com.ibm.xsp.component.xp.XspGraphicImage;

public class MypicImage extends XspGraphicImage {

	public static final String COMPONENT_TYPE = "com.ZetaOne.mypic.MypicImage";
	public static final String RENDERER_TYPE = "com.ZetaOne.mypic.xsp.MypicImage";
	protected String ImageSize;
	protected String ImageName;
	protected String UserName;
	protected String UserId;
	protected String ServiceUrl;
	protected String Format;

	public MypicImage() {
		setRendererType(MypicImage.RENDERER_TYPE);
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
	 * UserName
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
	 * Format
	 * 
	 * @return
	 */
	public String getFormat() {
		if (this.Format != null) {
			return this.Format;
		}

		ValueBinding vb = getValueBinding("Format");
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setFormat(String format) {
		this.Format = format;
	}

	/**
	 * ImageSize
	 * 
	 * @return
	 */
	public String getImageSize() {
		if (this.ImageSize != null) {
			return this.ImageSize;
		}

		ValueBinding vb = getValueBinding("ImageSize");
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setImageSize(String imageSize) {
		this.ImageSize = imageSize;
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
	 * ImageName
	 * 
	 * @return
	 */
	public String getImageName() {
		if (this.ImageName != null) {
			return this.ImageName;
		}

		ValueBinding vb = getValueBinding("ImageName");
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setImageName(String imageName) {
		this.ImageName = imageName;
	}

	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
		this.UserName = (String) _values[1];
		this.ImageSize = (String) _values[2];
		this.ServiceUrl = (String) _values[3];
		this.ImageName = (String) _values[4];
		this.Format = (String) _values[5];
		this.UserId = (String) _values[6];
	}

	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[7];
		_values[0] = super.saveState(_context);
		_values[1] = getUserName();
		_values[2] = getImageSize();
		_values[3] = getServiceUrl();
		_values[4] = getImageName();
		_values[5] = getFormat();
		_values[6] = getUserId();

		return _values;
	}
}
