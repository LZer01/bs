package com.example.house.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户收藏房源表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("house_favorite")
public class HouseFavorite {

    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    //用户ID（租客）
    private Long userId;

    //房源ID
    private Long houseId;

    //收藏时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}