package com.example.mongodb.controller;

import com.example.mongodb.domain.User;
import com.example.mongodb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author MrBird
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @PostMapping
    public User createUser(User user) {
        return userService.createUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable String id, User user) {
        userService.updateUser(id, user);
    }

    /**
     * 根据用户 id查找
     * 存在返回，不存在返回 null
     */
    @GetMapping("/{id}")
    public User getUser(@PathVariable String id) {
        return userService.getUser(id).orElse(null);
    }

    /**
     * 根据年龄段来查找
     */
    @GetMapping("/age/{from}/{to}")
    public List<User> getUserByAge(@PathVariable Integer from, @PathVariable Integer to) {
        return userService.getUserByAge(from, to);
    }

    /**
     * 根据用户名查找
     */
    @GetMapping("/name/{name}")
    public List<User> getUserByName(@PathVariable String name) {
        return userService.getUserByName(name);
    }

    /**
     * 根据用户描述模糊查找
     */
    @GetMapping("/description/{description}")
    public List<User> getUserByDescription(@PathVariable String description) {
        return userService.getUserByDescription(description);
    }

    /**
     * 根据多个检索条件查询
     */
    @GetMapping("/condition")
    public Page<User> getUserByCondition(int size, int page, User user) {
        return userService.getUserByCondition(size, page, user);
    }

}
