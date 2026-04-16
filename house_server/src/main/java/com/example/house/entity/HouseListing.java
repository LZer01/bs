package com.example.house.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("house_listing")
public class HouseListing {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String houseName;
    private String address;
    private BigDecimal area;
    private String layout;
    private BigDecimal price;
    private String description;
    private String status;
    private Boolean isPublished;
    private String imageUrl;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private LocalDateTime deletedAt;
}
