package com.example.house.controller;

import com.example.house.common.ApiResponse;
import com.example.house.entity.User;
import com.example.house.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController { // 建议改名为 AuthController 更规范，但保持原名也可以

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody Map<String, String> req) {
        // 1. 参数获取与校验
        String username = req.get("username");
        String password = req.get("password");
        String phone = req.get("phone");
        String email = req.get("email");
        // 注意：数据库里没有 nickname，所以这里不处理 nickname

        if (username == null || username.trim().length() < 3) {
            return ApiResponse.error("用户名至少3个字符");
        }
        if (password == null || password.length() < 6) {
            return ApiResponse.error("密码至少6位");
        }
        if (phone == null || phone.isEmpty()) {
            return ApiResponse.error("手机号不能为空");
        }

        // 2. 构建用户对象
        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(password); // Service 层会加密
        user.setPhone(phone);
        user.setEmail(email);

        // 如果前端没传 role，默认为 0 (租客)
        try {
            String roleStr = req.get("role");
            if (roleStr != null && !roleStr.isEmpty()) {
                user.setRole(Integer.parseInt(roleStr));
            } else {
                user.setRole(0); // 默认租客
            }
        } catch (NumberFormatException e) {
            user.setRole(0); // 异常时默认租客
        }

        // 3. 执行注册
        try {
            boolean success = userService.register(user);
            if (!success) {
                return ApiResponse.error("注册失败，用户名可能已存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("注册异常: " + e.getMessage());
        }

        // 4. 返回脱敏数据
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("role", user.getRole());

        return ApiResponse.success("注册成功", result);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String password = req.get("password");

//        System.out.println("🚀 收到登录请求: " + username);

        // 1. 手动校验
        if (username == null || password == null) {
            return ApiResponse.error("用户名和密码不能为空");
        }

        // 2. 执行登录
        User user = null;
        try {
            user = userService.login(username, password);
            System.out.println("???");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "登录服务异常: " + e.getMessage());
        }

        if (user == null) {
            return ApiResponse.error(401, "用户名或密码错误");
        }

        // 3. 生成简易 Token,测试用
        String token = "debug-token-" + user.getId();

        // 4. 组装响应
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user); // 密码已在 Service 层清空
//        result.put("id",user.getId());

        return ApiResponse.success("登录成功",result);
    }
}

