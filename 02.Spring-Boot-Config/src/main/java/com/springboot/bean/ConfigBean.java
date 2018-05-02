package com.springboot.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="mrbird.blog")
public class ConfigBean {
	private String name;
	private String title;
	private String wholeTitle;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getWholeTitle() {
		return wholeTitle;
	}
	public void setWholeTitle(String wholeTitle) {
		this.wholeTitle = wholeTitle;
	}	
	
}
