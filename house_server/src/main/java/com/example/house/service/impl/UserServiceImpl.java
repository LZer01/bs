package com.example.house.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.house.entity.User;
import com.example.house.mapper.UserMapper;
import com.example.house.service.UserService;
import com.example.house.utils.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(User user) {
        System.out.println("🔵 [Register] 开始注册用户: " + user.getUsername());
        try {
            // 1. 校验用户名
            User existUser = getByUsername(user.getUsername());
            if (existUser != null) {
                System.out.println("🔴 [Register] 用户已存在");
                return false;
            }

            // 2. 密码加密
            System.out.println("🟡 [Register] 原始密码: " + user.getPassword());
            user.setPassword(PasswordUtil.encode(user.getPassword()));
            System.out.println("🟢 [Register] 加密后密码前10位: " + user.getPassword().substring(0, 10) + "...");

            // 3. 设置默认值
            if (user.getStatus() == null) user.setStatus(1);
            if (user.getRole() == null) user.setRole(0); // 默认租客

            // 4. 插入数据库
            boolean success = save(user);
            System.out.println("🟢 [Register] 插入结果: " + success);
            return success;

        } catch (Exception e) {
            System.err.println("💥 [Register] 注册异常: " + e.getMessage());
            e.printStackTrace(); // ⚠️ 打印详细堆栈，这是找 500 错误的关键
            throw new RuntimeException("注册失败", e);
        }
    }

    @Override
    public User login(String username, String password) {
        System.out.println("🔵 [Login] 开始登录: " + username);
        try {
            // 1. 查询用户
            User user = getByUsername(username);

            if (user == null) {
                System.out.println("🔴 [Login] 用户不存在");
                return null;
            }

            System.out.println("🟡 [Login] 找到用户 ID: " + user.getId());
            System.out.println("🟡 [Login] 前端传入明文密码: " + password);

            // 2. 密码比对
            boolean isMatch = PasswordUtil.matches(password, user.getPassword());

            System.out.println("🟢 [Login] 密码比对结果: " + isMatch);

            if (!isMatch) {
//                System.out.println("no");
                return null;
            }

            // 3. 状态校验
            if (user.getStatus() != null && user.getStatus() == 0) {
                System.out.println("🔴 [Login] 账号已被禁用");
                throw new RuntimeException("账号已被禁用");
            }

            // 4. 脱敏
            user.setPassword(null);
            System.out.println("🎉 [Login] 登录成功");
            return user;

        } catch (Exception e) {
            System.err.println("💥 [Login] 登录异常: " + e.getMessage());
            e.printStackTrace(); // ⚠️ 打印详细堆栈
            throw new RuntimeException("登录服务异常", e);
        }
    }

    @Override
    public User getByUsername(String username) {
        System.out.println("🔍 [Query] 正在查询用户: " + username);
        try {
            User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
            System.out.println("🔍 [Query] 查询结果: " + (user != null ? "找到用户" : "未找到"));
            return user;
        } catch (Exception e) {
            System.err.println("💥 [Query] 查询异常: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("查询用户失败", e);
        }
    }
}