package com.springboot.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import com.springboot.bean.Student;

@CacheConfig(cacheNames = "student")
public interface StudentService {
	@CachePut(key = "#p0.sno")
	Student update(Student student);

	@CacheEvict(key = "#p0", allEntries = true)
	void deleteStudentBySno(String sno);
	
	@Cacheable(key = "#p0")
	Student queryStudentBySno(String sno);
}
