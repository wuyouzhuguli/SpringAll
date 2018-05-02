package com.springboot.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.springboot.dao.MysqlStudentDao;

@Repository
public class MysqlStudentDaoImp implements MysqlStudentDao{
	
	@Autowired
	@Qualifier("mysqlJdbcTemplate")
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<Map<String, Object>> getAllStudents() {
		return this.jdbcTemplate.queryForList("select * from student");
	}

}
