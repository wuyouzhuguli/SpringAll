package com.springboot.service;

import java.util.List;

import com.springboot.pojo.UserOnline;

public interface SessionService {
	
	List<UserOnline> list();
	boolean forceLogout(String sessionId);
}
