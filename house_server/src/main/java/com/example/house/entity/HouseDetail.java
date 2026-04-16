package com.example.house.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;

/**
 * 房源详情实体类
 *
 * @author yourname
 * @date 2026-04-13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("house_detail")
public class HouseDetail {

    /**
     * 房源主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联小区ID
     */
    private String communityId;

    /**
     * 房源标题
     */
    private String title;

    /**
     * 价格（单位：元）
     */
    private BigDecimal price;

    /**
     * 面积（单位：平方米）
     */
    private Integer area;

    /**
     * 卧室数量（室）
     * 默认值：1
     */
    private Integer roomCount;

    /**
     * 客厅数量（厅）
     * 默认值：0
     */
    private Integer hallCount;

    /**
     * 所在楼层
     */
    private Integer floor;

    /**
     * 总楼层数
     */
    private Integer totalFloor;

    /**
     * 房屋朝向（如：南、北、东南、南北通透等）
     */
    private String direction;

    /**
     * 标签位掩码
     * 1 - 阳台
     * 2 - 独立卫浴
     * 4 - 近地铁
     * 8 - 精装修
     * 16 - 拎包入住
     * 默认值：0
     */
    private Integer tagMask;

    /**
     * 房源状态
     * 0 - 下架
     * 1 - 在售
     * 2 - 已售
     * 默认值：1
     */
    private Integer status;

    /**
     * 房屋描述（详细介绍）
     */
    private String description;

    /**
     * 房源照片 URL（逗号分隔）
     */
    private String photos;

    /**
     * 创建时间
     */
    private java.time.LocalDateTime createTime;

    /**
     * 更新时间
     */
    private java.time.LocalDateTime updateTime;

}