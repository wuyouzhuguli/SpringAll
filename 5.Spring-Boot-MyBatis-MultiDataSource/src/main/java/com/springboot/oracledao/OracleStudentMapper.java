package com.springboot.oracledao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OracleStudentMapper {
	List<Map<String, Object>> getAllStudents();
}
