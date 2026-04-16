package com.example.house.service;

import java.util.List;
import java.util.Map;

/**
 * 高德地图服务接口
 */
public interface GaodeService {

    /**
     * 根据地址查询经纬度（地理编码）
     * @param address 地址
     * @return 包含纬度(lat)和经度(lon)的Map，失败返回null
     */
    Map<String, String> getGeocode(String address);

    List<Map<String, Object>> searchPoi(String lat, String lon, String keyword, int radius, int page, int pageSize);
    List<Map<String, Object>> getPoi(String address, int radius, int page, int pageSize);
}