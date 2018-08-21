package com.springboot;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.springboot.bean.User;
import com.springboot.service.UserService;

import tk.mybatis.mapper.entity.Example;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class ApplicationTest {

	@Autowired
	private UserService userService;

	@Test
	public void test() throws Exception {

		// User user = new User();
		// user.setId(userService.getSequence("seq_user"));
		// user.setUsername("scott");
		// user.setPasswd("ac089b11709f9b9e9980e7c497268dfa");
		// user.setCreateTime(new Date());
		// user.setStatus("0");
		// this.userService.save(user);

		
//		Example example = new Example(User.class);
//		example.createCriteria().andCondition("username like '%i%'");
//		example.setOrderByClause("id desc");
//		List<User> userList = this.userService.selectByExample(example);
//		for (User u : userList) {
//			System.out.println(u.getUsername());
//		}
//		
//		List<User> all = this.userService.selectAll();
//		for (User u : all) {
//			System.out.println(u.getUsername());
//		}
//		
//		User user = new User();
//		user.setId(1l);
//		user = this.userService.selectByKey(user);
//		System.out.println(user.getUsername());
//
//		user.setId(4l);
//		this.userService.delete(user);

		PageHelper.startPage(2, 2);
		List<User> list = userService.selectAll();
		PageInfo<User> pageInfo = new PageInfo<User>(list);
		List<User> result = pageInfo.getList();
		for (User u : result) {
			System.out.println(u.getUsername());
		}
	}
}
