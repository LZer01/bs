package com.example.house.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.house.entity.User;

public interface UserService extends IService<User> {
    /**
     * 用户注册
     * @param user 用户信息（含明文密码）
     * @return 是否注册成功
     */
    boolean register(User user);

    /**
     * 用户登录
     * @param username 用户名
     * @param password 明文密码
     * @return 登录成功的用户（密码已脱敏），失败返回 null
     */
    User login(String username, String password);

    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);
}