package com.springboot.service;

import java.util.List;
import java.util.Map;

import com.springboot.bean.Student;

public interface StudentService {
	int add(Student student);
    int update(Student student);
    int deleteBysno(String sno);
    List<Map<String, Object>> queryStudentListMap();
    Student queryStudentBySno(String sno);
}
