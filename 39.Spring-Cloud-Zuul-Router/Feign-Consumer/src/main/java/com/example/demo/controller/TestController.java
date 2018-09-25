package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TestController {

    @Autowired
    private UserService userService;

    @GetMapping("user/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.get(id);
    }

    @GetMapping("user")
    public List<User> getUsers() {
        return userService.get();
    }

    @PostMapping("user")
    public void addUser() {
        User user = new User(1L, "mrbird", "123456");
        userService.add(user);
    }

    @PutMapping("user")
    public void updateUser() {
        User user = new User(1L, "mrbird", "123456");
        userService.update(user);
    }

    @DeleteMapping("user/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id);
    }
}
