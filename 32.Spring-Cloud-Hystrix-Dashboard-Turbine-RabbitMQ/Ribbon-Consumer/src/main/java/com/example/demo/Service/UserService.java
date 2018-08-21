package com.example.demo.Service;

import com.example.demo.domain.User;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author MrBird
 */
@Service("userService")
public class UserService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestTemplate restTemplate;

    @HystrixCollapser(batchMethod = "findUserBatch", collapserProperties = {
            @HystrixProperty(name = "timerDelayInMilliseconds", value = "100")
    })
    public Future<User> findUser(Long id) {
        log.info("获取单个用户信息");
        // return new AsyncResult<User>() {
        //     @Override
        //     public User invoke() {
        //         return restTemplate.getForObject("http://Server-Provider/user/{id}", User.class, id);
        //     }
        // };
        return null;
    }

    @HystrixCommand
    public List<User> findUserBatch(List<Long> ids) {
        log.info("批量获取用户信息,ids: " + ids);
        User[] users = restTemplate.getForObject("http://Server-Provider/user/users?ids={1}", User[].class, StringUtils.join(ids, ","));
        return Arrays.asList(users);
    }

    public String getCacheKey(Long id) {
        return String.valueOf(id);
    }

    @CacheResult(cacheKeyMethod = "getCacheKey")
    @HystrixCommand(fallbackMethod = "getUserDefault", commandKey = "getUserById", groupKey = "userGroup",
            threadPoolKey = "getUserThread")
    public User getUser(Long id) {
        log.info("获取用户信息");
        return restTemplate.getForObject("http://Server-Provider/user/{id}", User.class, id);
    }

    @HystrixCommand(fallbackMethod = "getUserDefault2")
    public User getUserDefault(Long id) {
        String a = null;
        // 测试服务降级
        a.toString();
        User user = new User();
        user.setId(-1L);
        user.setUsername("defaultUser");
        user.setPassword("123456");
        return user;
    }

    public User getUserDefault2(Long id, Throwable e) {
        System.out.println(e.getMessage());
        User user = new User();
        user.setId(-2L);
        user.setUsername("defaultUser2");
        user.setPassword("123456");
        return user;
    }

    public List<User> getUsers() {
        return this.restTemplate.getForObject("http://Server-Provider/user", List.class);
    }

    public String addUser() {
        User user = new User(1L, "mrbird", "123456");
        HttpStatus status = this.restTemplate.postForEntity("http://Server-Provider/user", user, null).getStatusCode();
        if (status.is2xxSuccessful()) {
            return "新增用户成功";
        } else {
            return "新增用户失败";
        }
    }

    @CacheRemove(commandKey = "getUserById")
    @HystrixCommand
    public void updateUser(@CacheKey("id") User user) {
        this.restTemplate.put("http://Server-Provider/user", user);
    }

    public void deleteUser(@PathVariable Long id) {
        this.restTemplate.delete("http://Server-Provider/user/{1}", id);
    }
}
