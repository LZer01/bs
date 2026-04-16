package com.example.house.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 对应数据库中的 community 表
 */
@Data
@TableName("community")
public class Community {
    @TableId
    private String id;          // 高德 POI ID
    private String name;        // 小区名称
    private Integer houseCounts;// 小区内房源总数
    private String pname;       // 省份
    private String cityname;    // 城市
    private String adname;      // 区域(如江宁区)
    private String address;     // 详细地址
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String photoUrl;    // 小区封面图
}