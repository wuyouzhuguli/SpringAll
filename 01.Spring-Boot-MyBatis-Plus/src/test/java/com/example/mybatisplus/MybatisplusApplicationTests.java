package com.example.mybatisplus;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.mybatisplus.dao.StudentMapper;
import com.example.mybatisplus.entity.Student;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
@RunWith(SpringRunner.class)
@SpringBootTest()
class MybatisplusApplicationTests {
    @Resource
    StudentMapper studentMapper;
    @Transactional()
    @Test
    void contextLoads() {
        Student student=new Student();
        student.setName("xuyu");
        student.setSex("man");
        student.setSno("24320142202513");
        studentMapper.insert(student);
        System.out.println("dafdsa");
    }
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Test
    void readStudent(){
        Student xuyu = studentMapper.selectOne(Wrappers.<Student>lambdaQuery().eq(Student::getName, "xuyu"));
        System.out.println(xuyu.getSno());
    }
}
