package com.springboot.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.springboot.pojo.Permission;

@Mapper
public interface UserPermissionMapper {
	
	List<Permission> findByUserName(String userName);
}
