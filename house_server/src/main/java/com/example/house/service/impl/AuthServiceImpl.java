//package com.example.house.service.impl;
//
//import com.example.house.utils.JwtUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Service;
//
//@Service
//public class AuthServiceImpl {
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private JwtUtil jwtUtil; // 这里注入刚才写的工具类
//
//    public String login(String username, String password) {
//        // 1. 认证
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(username, password)
//        );
//
//        // 2. 生成 Token
//        // 注意：authentication.getName() 通常返回的是用户名
//        String token = jwtUtil.generateToken(authentication.getName());
//
//        return token;
//    }
//}