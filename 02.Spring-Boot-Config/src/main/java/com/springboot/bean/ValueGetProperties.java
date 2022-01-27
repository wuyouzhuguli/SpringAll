package com.springboot.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: wyq
 * @create time: 2022/1/27
 * @description: 使用@Value("${property}")读取比较简单的配置信息
 */
@Component
public class ValueGetProperties {
	
	@Value("${mrbird.blog.name}")
	private String name;
	
	@Value("${mrbird.blog.title}")
	private String title;

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

	@Override
	public String toString() {
		return "BlogProperties{" +
				"name='" + name + '\'' +
				", title='" + title + '\'' +
				'}';
	}
}
