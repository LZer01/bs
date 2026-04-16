package com.example.house.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    /**
     * 密码：写入时可用，读取时忽略（不返回给前端）
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String real_name;

    private String phone;

    private String email;

    private String avatar;

    /**
     * 0: 租客, 1: 房东, 2: 管理员
     */
    private Integer role;

    /**
     * 状态: 1-正常, 0-禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}