package org.openntf.ui;

import java.io.Serializable;

public class Page implements Serializable {
	private static final long serialVersionUID = -810514190365137779L;
	private String label;
	private String url;
	private String icon;

	public Page(String label, String icon, String url) {
		this.label = label;
		this.icon = icon;
		this.url = url;
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
	
	
}
