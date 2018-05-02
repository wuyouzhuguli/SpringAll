package com.springboot.dao;

import org.apache.ibatis.annotations.Mapper;

import com.springboot.pojo.User;

@Mapper
public interface UserMapper {
	User findByUserName(String userName);
}
