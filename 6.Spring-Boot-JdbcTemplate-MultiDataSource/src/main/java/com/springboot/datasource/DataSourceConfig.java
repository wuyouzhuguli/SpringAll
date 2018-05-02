package com.springboot.datasource;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;

@Configuration
public class DataSourceConfig {
	@Primary
	@Bean(name = "mysqldatasource")
	@ConfigurationProperties("spring.datasource.druid.mysql")
	public DataSource dataSourceOne(){
	    return DruidDataSourceBuilder.create().build();
	}
	
	@Bean(name = "oracledatasource")
	@ConfigurationProperties("spring.datasource.druid.oracle")
	public DataSource dataSourceTwo(){
	    return DruidDataSourceBuilder.create().build();
	}

	@Bean(name = "mysqlJdbcTemplate")
	public JdbcTemplate primaryJdbcTemplate(
	        @Qualifier("mysqldatasource") DataSource dataSource) {
	    return new JdbcTemplate(dataSource);
	}
	
	@Bean(name = "oracleJdbcTemplate")
	public JdbcTemplate secondaryJdbcTemplate(
	        @Qualifier("oracledatasource") DataSource dataSource) {
	    return new JdbcTemplate(dataSource);
	}
}
