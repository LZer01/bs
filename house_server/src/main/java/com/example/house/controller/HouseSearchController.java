package com.example.house.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.house.entity.HouseDetail;
import com.example.house.entity.dto.Poi;
import com.example.house.entity.vo.CommunityVO;
import com.example.house.entity.vo.HouseDetailVO;
import com.example.house.service.GaodeService;
import com.example.house.service.HouseDetailService;
import com.example.house.common.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/house")
public class HouseSearchController {

    @Autowired
    private GaodeService gaodeService;

    @Autowired
    private HouseDetailService houseDetailService;

    @GetMapping("/search")
    public Result<List<CommunityVO>> searchCommunity(
            @RequestParam String query,
            @RequestParam(defaultValue = "3000") Integer radius,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        int sampleSize = 50;
        // 调用高德服务
        List<Map<String, Object>> poiMapList = gaodeService.getPoi(query, radius, 1, sampleSize);

        if (poiMapList == null || poiMapList.isEmpty()) {
            return Result.success(Collections.emptyList());
        }

        // 1. 解析 POI 列表 (此处调用了下面的 parsePoiInfo)
        List<Poi> poiInfoList = poiMapList.stream()
                .map(this::parsePoiInfo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<String> communityIds = poiInfoList.stream().map(Poi::getId).collect(Collectors.toList());

        // 2. 批量查询数据库房源数
        Map<String, Integer> countMap = houseDetailService.getExistingHouseCounts(communityIds);

        // 3. 组装 VO (此处调用了下面的 parseDistance)
        List<CommunityVO> result = poiInfoList.stream()
                .filter(poi -> countMap.containsKey(poi.getId()))
                .map(poi -> {
                    CommunityVO vo = new CommunityVO();
                    vo.setId(poi.getId());
                    vo.setName(poi.getName());
                    vo.setAddress(poi.getAddress());
                    vo.setDistrict(poi.getAdname());
                    vo.setHousingCount(countMap.get(poi.getId()));
                    vo.setDistance(parseDistance(poi.getDistance()));

                    if (poi.getLocation() != null && poi.getLocation().contains(",")) {
                        String[] coords = poi.getLocation().split(",");
                        vo.setLongitude(new BigDecimal(coords[0]));
                        vo.setLatitude(new BigDecimal(coords[1]));
                    }
                    return vo;
                })
                .sorted(Comparator.comparingDouble(CommunityVO::getDistance))
                .limit(pageSize)
                .collect(Collectors.toList());

        return Result.success(result);
    }

    /**
     * 辅助方法：解析高德 Map 为 Poi 对象
     */
    private Poi parsePoiInfo(Map<String, Object> map) {
        try {
            Poi poi = new Poi();
            poi.setId(String.valueOf(map.get("id")));
            poi.setName(String.valueOf(map.get("name")));
            poi.setAddress(String.valueOf(map.get("address")));
            poi.setAdname(String.valueOf(map.get("adname")));
            poi.setDistance(String.valueOf(map.get("distance")));
            poi.setLocation(String.valueOf(map.get("location")));
            return poi;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 辅助方法：处理距离显示
     */
    private Double parseDistance(String distanceStr) {
        try {
            if (distanceStr == null || "[]".equals(distanceStr)) return 0.0;
            double meters = Double.parseDouble(distanceStr);
            // 米转公里，保留一位小数
            return Math.round(meters / 100.0) / 10.0;
        } catch (Exception e) {
            return 0.0;
        }
    }


    @GetMapping("/community/{communityId}/houses")
    public Result<List<HouseDetailVO>> getHousesByCommunity(
            @PathVariable String communityId,
            @RequestParam(required = false) String rentType
    ) {
        // 1. 查询数据库中该小区的所有在租房源
        QueryWrapper<HouseDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("community_id", communityId)
                .eq("status", 1); // 只要在租的

        List<HouseDetail> houses = houseDetailService.list(wrapper);

        // 2. 转换为 VO 并处理 tagMask 位掩码
        List<HouseDetailVO> voList = houses.stream().map(house -> {
            HouseDetailVO vo = new HouseDetailVO();
            BeanUtils.copyProperties(house, vo);

            // 处理位掩码标签（将数字 1, 2, 4... 转为文字）
            vo.setTags(parseTagMask(house.getTagMask()));
            return vo;
        }).collect(Collectors.toList());

        return Result.success(voList);
    }

    /**
     * 辅助方法：位掩码转文字标签
     */
    private List<String> parseTagMask(Integer mask) {
        List<String> tags = new ArrayList<>();
        if (mask == null) return tags;
        if ((mask & 1) != 0) tags.add("阳台");
        if ((mask & 2) != 0) tags.add("独立卫浴");
        if ((mask & 4) != 0) tags.add("近地铁");
        if ((mask & 8) != 0) tags.add("精装修");
        if ((mask & 16) != 0) tags.add("拎包入住");
        return tags;
    }
}