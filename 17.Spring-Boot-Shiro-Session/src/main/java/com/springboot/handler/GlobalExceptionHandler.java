package com.springboot.handler;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.session.ExpiredSessionException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {
	
	@ExceptionHandler(value = AuthorizationException.class)
	public String handleAuthorizationException() {
		return "403";
	}
	
	@ExceptionHandler(value = ExpiredSessionException.class )
	public String handleExpiredSessionException() {
		return "login";
	}
}
