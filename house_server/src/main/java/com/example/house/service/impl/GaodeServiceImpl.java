package com.example.house.service.impl;

import com.example.house.service.GaodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class GaodeServiceImpl implements GaodeService {

    @Value("${gaode.api.key}")
    private String gaodeApiKey;

    private final RestTemplate restTemplate;

    public GaodeServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 1. 核心原子方法：只负责地理编码
    public Map<String, String> getGeocode(String address) {
        String url = String.format("https://restapi.amap.com/v3/geocode/geo?address=%s&key=%s", address, gaodeApiKey);
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && "1".equals(responseBody.get("status"))) {
                List<Map<String, Object>> geocodes = (List<Map<String, Object>>) responseBody.get("geocodes");
                if (geocodes != null && !geocodes.isEmpty()) {
                    String location = (String) geocodes.get(0).get("location");
                    String[] latLon = location.split(",");
                    return Map.of("lat", latLon[1], "lon", latLon[0]);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // 2. 核心原子方法：只负责根据经纬度搜索 POI（支撑所有搜索场景）
    public List<Map<String, Object>> searchPoi(String lat, String lon, String keyword, int radius, int page, int pageSize) {
        String url = String.format(
                "https://restapi.amap.com/v3/place/around?location=%s,%s&keywords=%s&radius=%d&page=%d&offset=%d&key=%s",
                lon, lat, keyword, radius, page, pageSize, gaodeApiKey
        );
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && "1".equals(responseBody.get("status"))) {
                return (List<Map<String, Object>>) responseBody.get("pois");
            }
        } catch (Exception e) { e.printStackTrace(); }
        return Collections.emptyList();
    }

    // 3. 业务入口方法：封装“地址 -> 经纬度 -> 搜索”的流程
    @Override
    public List<Map<String, Object>> getPoi(String address, int radius, int page, int pageSize) {
        // 先解析地址，只解析一次
        Map<String, String> location = getGeocode(address);
        if (location == null) return Collections.emptyList();

        // 固定房源关键词
        String keyword = "住宅小区|公寓|别墅";

        // 调用核心搜索方法
        return searchPoi(location.get("lat"), location.get("lon"), keyword, radius, page, pageSize);
    }

    // 4. 为了兼容老代码（如果有地方只传了这两个参数）
    public List<Map<String, Object>> getPoi(String address, int radius) {
        return getPoi(address, radius, 1, 20); // 默认第一页，20条
    }
}

