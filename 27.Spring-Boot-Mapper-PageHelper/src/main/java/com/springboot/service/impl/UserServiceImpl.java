package com.springboot.service.impl;

import org.springframework.stereotype.Repository;

import com.springboot.bean.User;
import com.springboot.service.UserService;

@Repository("userService")
public class UserServiceImpl extends BaseService<User> implements UserService{

	
}
