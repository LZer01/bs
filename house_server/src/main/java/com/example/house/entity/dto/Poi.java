package com.example.house.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class Poi {
    private String id;
    private String name;
    private String address;
    private String adname;
    private String distance;
    private String location; // 新增：高德返回的经纬度字符串 "经度,纬度"
    private List<PoiPhoto> photos;
}
