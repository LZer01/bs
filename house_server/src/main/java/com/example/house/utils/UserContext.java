package com.example.house.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 当前登录用户上下文工具类（临时方案）
 * 后期建议换成 Spring Security 的 SecurityContextHolder
 */
public class UserContext {

    /**
     * 获取当前登录用户ID
     * 这里先从请求头中获取，名为 "user-id"（可自行修改）
     */
    public static Long getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new RuntimeException("无法获取当前用户，请登录");
        }

        String userIdStr = attributes.getRequest().getHeader("user-id");
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            throw new RuntimeException("用户未登录或登录已过期");
        }

        try {
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new RuntimeException("用户ID格式错误");
        }
    }
}