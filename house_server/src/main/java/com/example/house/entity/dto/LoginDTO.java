package com.example.house.entity.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 登录传输对象
 */
@Data
public class LoginDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    public @NotBlank(message = "用户名不能为空") String getUsername() {
        return username;
    }

    public @NotBlank(message = "密码不能为空") String getPassword() {
        return password;
    }

    @NotBlank(message = "密码不能为空")
    private String password;
}