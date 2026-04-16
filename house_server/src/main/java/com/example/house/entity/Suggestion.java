package com.example.house.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("suggestion")
public class Suggestion {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String communityId;

    private LocalDateTime lastUrgeTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}