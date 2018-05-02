package com.springboot.pojo;

import java.io.Serializable;

public class Permission implements Serializable{

	private static final long serialVersionUID = 7160557680614732403L;
	private Integer id;
	private String url;
	private String name;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
