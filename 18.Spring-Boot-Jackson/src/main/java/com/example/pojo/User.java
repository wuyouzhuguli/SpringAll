package com.example.pojo;

import java.io.Serializable;
import java.util.Date;

import com.example.config.UserDeserializer;
import com.example.config.UserSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

//@JsonIgnoreProperties({ "password", "age" })
//@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
//@JsonSerialize(using = UserSerializer.class)
//@JsonDeserialize (using = UserDeserializer.class)
public class User implements Serializable {

	private static final long serialVersionUID = 6222176558369919436L;

	public interface UserNameView {
	};

	public interface AllUserFieldView extends UserNameView {
	};

	@JsonView(UserNameView.class)
	private String userName;
	
	@JsonView(AllUserFieldView.class)
	private int age;

	// @JsonIgnore
	@JsonView(AllUserFieldView.class)
	private String password;

	// @JsonProperty("bth")
	// @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonView(AllUserFieldView.class)
	private Date birthday;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getAge() {
		return age;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

}
