package com.example.house.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CommunityVO {
    private String id;
    private String name;
    private String district;    // 对应 adname
    private String address;
    private Double distance;    // 单位：km
    private Integer housingCount; // 对应 house_counts
    private List<String> photos; // 图片列表
    private BigDecimal latitude;
    private BigDecimal longitude;
}