springboot & shiro & jwt 简单demo

这是一个springboot+shiro+jwt的简单demo，无数据库无redis。
系统内置模拟了两个用户：

| 用户名        | 密码   |  角色  | 权限 |
| :-----   | :-----  | :----  |:----
| admin     | 123456 |   admin     | "user:add","user:view"
| scott        |   123456   |   regist   | "user:view"

参加 com.example.demo.utils.SystemUtils#users

测试样例使用postman导入resources/postman.json即可。