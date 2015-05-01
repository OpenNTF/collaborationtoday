package org.openntf.ui;

import java.io.Serializable;

public class Page implements Serializable {
	private static final long serialVersionUID = -810514190365137779L;
	private String label;
	private String url;
	private String icon;
	private boolean newWindow;

	public Page(String label, String icon, String url) {
		this.label = label;
		this.icon = icon;
		this.url = url;
		newWindow = false;
	}
	
	public Page(String label, String icon, String url, boolean newWindow) {
		this.label = label;
		this.icon = icon;
		this.url = url;
		this.newWindow = newWindow;
	}

	public String getLabel() {
		return label;
	}

	public String getUrl() {
		return url;
	}

	public String getIcon() {
		return icon;
	}

	public boolean isNewWindow() {
		return newWindow;
	}
	
	
}
