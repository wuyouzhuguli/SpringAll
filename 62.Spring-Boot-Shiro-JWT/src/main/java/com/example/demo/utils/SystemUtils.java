package com.example.demo.utils;

import com.example.demo.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 系统工具类
 *
 * @author MrBird
 */
public class SystemUtils {

    private static Logger log = LoggerFactory.getLogger(SystemUtils.class);

    /**
     * 模拟两个用户
     *
     * @return List<User>
     */
    private static List<User> users() {
        List<User> users = new ArrayList<>();
        // 模拟两个用户：
        // 1. 用户名 admin，密码 123456，角色 admin（管理员），权限 "user:add"，"user:view"
        // 1. 用户名 scott，密码 123456，角色 regist（注册用户），权限 "user:view"
        users.add(new User(
                "admin",
                "bfc62b3f67a4c3e57df84dad8cc48a3b",
                new HashSet<>(Collections.singletonList("admin")),
                new HashSet<>(Arrays.asList("user:add", "user:view"))));
        users.add(new User(
                "scott",
                "11bd73355c7bbbac151e4e4f943e59be",
                new HashSet<>(Collections.singletonList("regist")),
                new HashSet<>(Collections.singletonList("user:view"))));
        return users;
    }

    /**
     * 获取用户
     *
     * @param username 用户名
     * @return 用户
     */
    public static User getUser(String username) {
        List<User> users = SystemUtils.users();
        return users.stream().filter(user -> StringUtils.equalsIgnoreCase(username, user.getUsername())).findFirst().orElse(null);
    }

}
