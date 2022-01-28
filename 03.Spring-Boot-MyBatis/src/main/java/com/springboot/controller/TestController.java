package com.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.bean.Student;
import com.springboot.service.StudentService;

@RestController
public class TestController {

	@Autowired
	private StudentService studentService;

	/**
	 * http://localhost:8003/web/querystudent?sno=002
	 */
	@RequestMapping( value = "/querystudent", method = RequestMethod.GET)
	public Student queryStudentBySno(String sno) {
		return studentService.queryStudentBySno(sno);
	}
}
