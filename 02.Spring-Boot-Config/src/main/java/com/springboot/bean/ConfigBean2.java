package com.springboot.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author: wyq
 * @create time: 2022/1/27
 * @description:
 * 通过@ConfigurationProperties读取配置信息并与bean绑定。
 */
@Component
@ConfigurationProperties(prefix="my.config.get")
public class ConfigBean2 {
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

	@Override
	public String toString() {
		return "ConfigBean{" +
				"name='" + name + '\'' +
				", title='" + title + '\'' +
				", wholeTitle='" + wholeTitle + '\'' +
				'}';
	}
}
