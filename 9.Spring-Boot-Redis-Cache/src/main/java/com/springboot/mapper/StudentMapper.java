package com.springboot.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.cache.annotation.CacheConfig;

import com.springboot.bean.Student;

@Mapper
@CacheConfig(cacheNames = "student")
public interface StudentMapper {

	@Update("update student set sname=#{name},ssex=#{sex} where sno=#{sno}")
	int update(Student student);

	@Delete("delete from student where sno=#{sno}")
	void deleteStudentBySno(String sno);

	@Select("select * from student where sno=#{sno}")
	@Results(id = "student", value = { @Result(property = "sno", column = "sno", javaType = String.class),
			@Result(property = "name", column = "sname", javaType = String.class),
			@Result(property = "sex", column = "ssex", javaType = String.class) })
	Student queryStudentBySno(String sno);
}
