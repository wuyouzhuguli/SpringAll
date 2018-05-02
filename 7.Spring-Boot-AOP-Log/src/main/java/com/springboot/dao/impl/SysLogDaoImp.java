package com.springboot.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.springboot.dao.SysLogDao;
import com.springboot.domain.SysLog;

@Repository
public class SysLogDaoImp implements SysLogDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public void saveSysLog(SysLog syslog) {
		StringBuffer sql = new StringBuffer("insert into sys_log ");
		sql.append("(id,username,operation,time,method,params,ip,create_time) ");
		sql.append("values(seq_sys_log.nextval,:username,:operation,:time,:method,");
		sql.append(":params,:ip,:createTime)");

		NamedParameterJdbcTemplate npjt = new NamedParameterJdbcTemplate(this.jdbcTemplate.getDataSource());
		npjt.update(sql.toString(), new BeanPropertySqlParameterSource(syslog));
	}

}
