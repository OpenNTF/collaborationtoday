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

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.stylekit.ThemeControl;

public class MypicLoginControl extends UIComponentBase implements ThemeControl {

	public static final String COMPONENT_TYPE = "com.ZetaOne.mypic.Login";
	public static final String COMPONENT_FAMILY = "javax.faces.Panel";
	public static final String RENDERER_TYPE = "com.ZetaOne.xsp.mypic.Login";

	protected String loginPhrase;
	protected String logoutPhrase;
	protected String welcomePhrase;
	protected String ImageSize;
	protected String ImageName;
	protected String ServiceUrl;
	protected String Format;
	protected String containerStyle;
	protected String containerStyleClass;
	protected String mypicStyle;
	protected String mypicStyleClass;
	protected String logInOutTextStyle;
	protected String logInOutTextStyleClass;
	protected String userNameTextStyle;
	protected String userNameTextStyleClass;
	protected String welcomePhraseStyle;
	protected String welcomePhraseStyleClass;
	protected String loginSuccessUrl;

	public MypicLoginControl() {
		setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public String getStyleKitFamily() {
		return ".mypic.loginControl";
	}

	public String getContainerStyle() {
		// return containerStyle;
		if (null != this.containerStyle) {
			return this.containerStyle;
		}
		ValueBinding _vb = getValueBinding("containerStyle");
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext
					.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setContainerStyle(String containerStyle) {
		this.containerStyle = containerStyle;
	}

	public String getContainerStyleClass() {
		// return containerStyleClass;
		if (null != this.containerStyleClass) {
			return this.containerStyleClass;
		}
		ValueBinding _vb = getValueBinding("containerStyleClass");
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext
					.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setContainerStyleClass(String containerStyleClass) {
		this.containerStyleClass = containerStyleClass;
	}

	public String getMypicStyle() {
		// return mypicStyle;
		if (null != this.mypicStyle) {
			return this.mypicStyle;
		}
		ValueBinding _vb = getValueBinding("mypicStyle");
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext
					.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setMypicStyle(String mypicStyle) {
		this.mypicStyle = mypicStyle;
	}

	public String getMypicStyleClass() {
		// return mypicStyleClass;
		if (null != this.mypicStyleClass) {
			return this.mypicStyleClass;
		}
		ValueBinding _vb = getValueBinding("mypicStyleClass");
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext
					.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setMypicStyleClass(String mypicStyleClass) {
		this.mypicStyleClass = mypicStyleClass;
	}

	public String getLoginPhrase() {
		// return loginPhrase;
		if (null != this.loginPhrase) {
			return this.loginPhrase;
		}
		ValueBinding _vb = getValueBinding("loginPhrase");
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext
					.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setLoginPhrase(String loginPhrase) {
		this.loginPhrase = loginPhrase;
	}

	public String getLogoutPhrase() {
		// return logoutPhrase;
		if (null != this.logoutPhrase) {
			return this.logoutPhrase;
		}
		ValueBinding _vb = getValueBinding("logoutPhrase");
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext
					.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setLogoutPhrase(String logoutPhrase) {
		this.logoutPhrase = logoutPhrase;
	}

	public String getWelcomePhrase() {
		// return welcomePhrase;
		if (null != this.welcomePhrase) {
			return this.welcomePhrase;
		}
		ValueBinding _vb = getValueBinding("welcomePhrase");
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext
					.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setWelcomePhrase(String welcomePhrase) {
		this.welcomePhrase = welcomePhrase;
	}

	public String getLogInOutTextStyle() {
		// return logInOutTextStyle;
		if (null != this.logInOutTextStyle) {
			return this.logInOutTextStyle;
		}
		ValueBinding _vb = getValueBinding("logInOutTextStyle");
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext
					.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setLogInOutTextStyle(String logInOutTextStyle) {
		this.logInOutTextStyle = logInOutTextStyle;
	}

	public String getLogInOutTextStyleClass() {
		// return logInOutTextStyleClass;
		if (null != this.logInOutTextStyleClass) {
			return this.logInOutTextStyleClass;
		}
		ValueBinding _vb = getValueBinding("logInOutTextStyleClass");
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext
					.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setLogInOutTextStyleClass(String logInOutTextStyleClass) {
		this.logInOutTextStyleClass = logInOutTextStyleClass;
	}

	public String getUserNameTextStyle() {
		// return userNameTextStyle;
		if (null != this.userNameTextStyle) {
			return this.userNameTextStyle;
		}
		ValueBinding _vb = getValueBinding("userNameTextStyle");
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext
					.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setUserNameTextStyle(String userNameTextStyle) {
		this.userNameTextStyle = userNameTextStyle;
	}

	public String getUserNameTextStyleClass() {
		// return userNameTextStyleClass;
		if (null != this.userNameTextStyleClass) {
			return this.userNameTextStyleClass;
		}
		ValueBinding _vb = getValueBinding("userNameTextStyleClass");
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext
					.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setUserNameTextStyleClass(String userNameTextStyleClass) {
		this.userNameTextStyleClass = userNameTextStyleClass;
	}

	public String getWelcomePhraseStyle() {
		// return welcomePhraseStyle;
		if (null != this.welcomePhraseStyle) {
			return this.welcomePhraseStyle;
		}
		ValueBinding _vb = getValueBinding("welcomePhraseStyle");
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext
					.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setWelcomePhraseStyle(String welcomePhraseStyle) {
		this.welcomePhraseStyle = welcomePhraseStyle;
	}

	public String getWelcomePhraseStyleClass() {
		// return welcomePhraseStyleClass;
		if (null != this.welcomePhraseStyleClass) {
			return this.welcomePhraseStyleClass;
		}
		ValueBinding _vb = getValueBinding("welcomePhraseStyleClass");
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext
					.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setWelcomePhraseStyleClass(String welcomePhraseStyleClass) {
		this.welcomePhraseStyleClass = welcomePhraseStyleClass;
	}

	public String getImageSize() {
		// return ImageSize;
		if (null != this.ImageSize) {
			return this.ImageSize;
		}
		ValueBinding _vb = getValueBinding("ImageSize");
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext
					.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setImageSize(String imageSize) {
		ImageSize = imageSize;
	}

	public String getImageName() {
		// return ImageName;
		if (null != this.ImageName) {
			return this.ImageName;
		}
		ValueBinding _vb = getValueBinding("ImageName");
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext
					.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setImageName(String imageName) {
		ImageName = imageName;
	}

	public String getServiceUrl() {
		// return ServiceUrl;
		if (null != this.ServiceUrl) {
			return this.ServiceUrl;
		}
		ValueBinding _vb = getValueBinding("ServiceUrl");
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext
					.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setServiceUrl(String serviceUrl) {
		ServiceUrl = serviceUrl;
	}

	public String getFormat() {
		// return Format;
		if (null != this.Format) {
			return this.Format;
		}
		ValueBinding _vb = getValueBinding("Format");
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext
					.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setFormat(String format) {
		Format = format;
	}

	public String getLoginSuccessUrl() {
		//return loginSuccessURL;
		if (null != this.loginSuccessUrl) {
			return this.loginSuccessUrl;
		}
		ValueBinding _vb = getValueBinding("loginSuccessUrl");
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext
					.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setLoginSuccessUrl(String loginSuccessUrl) {
		this.loginSuccessUrl = loginSuccessUrl;
	}

	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);

		this.loginPhrase = (String) _values[1];
		this.logoutPhrase = (String) _values[2];
		this.welcomePhrase = (String) _values[3];
		this.ImageSize = (String) _values[4];
		this.ImageName = (String) _values[5];
		this.ServiceUrl = (String) _values[6];
		this.Format = (String) _values[7];
		this.containerStyle = (String) _values[8];
		this.containerStyleClass = (String) _values[9];
		this.mypicStyle = (String) _values[10];
		this.mypicStyleClass = (String) _values[11];
		this.logInOutTextStyle = (String) _values[12];
		this.logInOutTextStyleClass = (String) _values[13];
		this.userNameTextStyle = (String) _values[14];
		this.userNameTextStyleClass = (String) _values[15];
		this.welcomePhraseStyle = (String) _values[16];
		this.welcomePhraseStyleClass = (String) _values[17];
		this.loginSuccessUrl = (String) _values[18];

	}

	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[19];
		_values[0] = super.saveState(_context);
		_values[1] = loginPhrase;
		_values[2] = logoutPhrase;
		_values[3] = welcomePhrase;
		_values[4] = ImageSize;
		_values[5] = ImageName;
		_values[6] = ServiceUrl;
		_values[7] = Format;
		_values[8] = containerStyle;
		_values[9] = containerStyleClass;
		_values[10] = mypicStyle;
		_values[11] = mypicStyleClass;
		_values[12] = logInOutTextStyle;
		_values[13] = logInOutTextStyleClass;
		_values[14] = userNameTextStyle;
		_values[15] = userNameTextStyleClass;
		_values[16] = welcomePhraseStyle;
		_values[17] = welcomePhraseStyleClass;
		_values[18] = loginSuccessUrl;
			
		return _values;
	}

}
