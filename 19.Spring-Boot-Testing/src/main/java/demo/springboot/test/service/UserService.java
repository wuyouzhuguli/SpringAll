package demo.springboot.test.service;

import demo.springboot.test.domain.User;

public interface UserService extends IService<User>{
	User findByName(String userName);
	
	void saveUser(User user);
}
