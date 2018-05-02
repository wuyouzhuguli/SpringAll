package com.springboot.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.mysqldao.MysqlStudentMapper;
import com.springboot.oracledao.OracleStudentMapper;
import com.springboot.service.StudentService;

@Service("studentService")
public class StudentServiceImp implements StudentService{
	@Autowired
	private OracleStudentMapper oracleStudentMapper;
	@Autowired
	private MysqlStudentMapper mysqlStudentMapper;
	
	@Override
	public List<Map<String, Object>> getAllStudentsFromOralce() {
		return this.oracleStudentMapper.getAllStudents();
	}

	@Override
	public List<Map<String, Object>> getAllStudentsFromMysql() {
		return this.mysqlStudentMapper.getAllStudents();
	}

}
