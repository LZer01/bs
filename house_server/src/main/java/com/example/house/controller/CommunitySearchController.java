package com.example.house.controller;

import com.example.house.entity.Community;
import com.example.house.entity.vo.CommunityVO;
import com.example.house.mapper.CommunityMapper;
import com.example.house.service.GaodeService;
import com.example.house.service.impl.GaodeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController // 建议改为 RestController 处理 API 请求
@RequestMapping("/api/community")
public class CommunitySearchController {

    private final GaodeServiceImpl gaodeServiceimpl;
    private final CommunityMapper communityRepository; // 必须注入仓库层
    @Autowired
    private GaodeService gaodeService;

    public CommunitySearchController(GaodeServiceImpl gaodeServiceimpl, CommunityMapper communityRepository) {
        this.gaodeServiceimpl = gaodeServiceimpl;
        this.communityRepository = communityRepository;
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam(name = "query", required = false, defaultValue = "") String query,
            @RequestParam(name = "radius", required = false, defaultValue = "1000") Integer radius
    ) {
        if (query.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "msg", "查询条件不能为空"));
        }


        List<Map<String, Object>> pois = gaodeService.getPoi(query, radius,1,50);

        if (pois == null || pois.isEmpty()) {
            return ResponseEntity.ok(Map.of("code", 200, "data", Collections.emptyList()));
        }

        // 3. 数据聚合：提取 ID 并从数据库批量查询
        List<String> poiIds = pois.stream()
                .map(poi -> poi.get("id").toString())
                .collect(Collectors.toList());

        // 注意：这里根据你的 MyBatis-Plus 或 JPA 实际方法名调整
        List<Community> dbCommunities = communityRepository.selectBatchIds(poiIds);

        // 将数据库结果转为 Map，方便 O(1) 查找
        Map<String, Integer> houseCountMap = dbCommunities.stream()
                .collect(Collectors.toMap(Community::getId, Community::getHouseCounts, (v1, v2) -> v1));

        // 4. 组装 VO 结果
        List<CommunityVO> result = pois.stream().map(poi -> {
            String id = poi.get("id").toString();
            CommunityVO vo = new CommunityVO();
            vo.setId(id);
            vo.setName(poi.get("name") != null ? poi.get("name").toString() : "");
            vo.setDistrict(poi.get("adname") != null ? poi.get("adname").toString() : "");
            vo.setAddress(poi.get("address") != null ? poi.get("address").toString() : "");

            // 距离转换处理
            Object distObj = poi.get("distance");
            if (distObj != null) {
                vo.setDistance(Double.parseDouble(distObj.toString()) / 1000.0);
            }

            // 关联数据库中的房源数
            vo.setHousingCount(houseCountMap.getOrDefault(id, 0));

            // 图片处理（高德 POI 结果中通常是个 List<Map> 或 List<String>）
            Object photosObj = poi.get("photos");

            if (photosObj instanceof List<?>) {
                List<?> rawList = (List<?>) photosObj;

                List<String> urls = rawList.stream()
                        .filter(item -> item instanceof Map)
                        .map(item -> (Map<?, ?>) item)
                        .map(map -> map.get("url"))
                        .filter(url -> url != null)
                        .map(Object::toString)
                        .collect(Collectors.toList());

                vo.setPhotos(urls);
            } else {
                vo.setPhotos(Collections.emptyList());
            }

            return vo;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("code", 200, "data", result));
    }
}
