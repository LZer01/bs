package com.example.house.entity.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class HouseDetailVO {
    private Long id;
    private String title;
    private BigDecimal price;
    private Integer area;
    private Integer roomCount;
    private Integer hallCount;
    private String direction;
    private List<String> tags; // 由 tagMask 转换而来的字符串列表，如 ["阳台", "精装修"]
    private String description;
}