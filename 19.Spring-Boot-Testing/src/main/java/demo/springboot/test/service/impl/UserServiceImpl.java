package demo.springboot.test.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import demo.springboot.test.domain.User;
import demo.springboot.test.service.UserService;
import tk.mybatis.mapper.entity.Example;

@Repository("userService")
public class UserServiceImpl extends BaseService<User> implements UserService {

	@Override
	public User findByName(String userName) {
		Example example = new Example(User.class);
		example.createCriteria().andCondition("username=", userName);
		List<User> userList = this.selectByExample(example);
		if (userList.size() != 0)
			return userList.get(0);
		else
			return null;
	}

	@Override
	public void saveUser(User user) {
		user.setId(this.getSequence("seq_user"));
		user.setCreateTime(new Date());
		this.save(user);
	}

}
