package com.springboot.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import com.springboot.pojo.Role;

@Mapper
public interface UserRoleMapper {
	
	List<Role> findByUserName(String userName);
}
