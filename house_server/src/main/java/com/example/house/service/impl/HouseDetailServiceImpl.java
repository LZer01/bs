package com.example.house.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.house.entity.HouseDetail;
import com.example.house.mapper.HouseDetailMapper;
import com.example.house.service.HouseDetailService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HouseDetailServiceImpl extends ServiceImpl<HouseDetailMapper, HouseDetail> implements HouseDetailService {

    @Override
    public Map<String, Integer> getHouseCountByCommunityIds(List<String> communityIds) {
        if (communityIds == null || communityIds.isEmpty()) {
            return new HashMap<>();
        }

        // 构造聚合查询：SELECT community_id, count(*) as count FROM house_detail ... GROUP BY community_id
        QueryWrapper<HouseDetail> wrapper = new QueryWrapper<>();
        wrapper.select("community_id", "count(*) as total")
                .in("community_id", communityIds)
                .eq("status", 1) // 只统计在售/在租的
                .groupBy("community_id");

        // 使用 listMaps 获取原始聚合结果
        List<Map<String, Object>> maps = this.listMaps(wrapper);

        // 将 List<Map> 转换为 Map<String, Integer> 方便 Controller 调用
        return maps.stream().collect(Collectors.toMap(
                m -> m.get("community_id").toString(),
                m -> Integer.parseInt(m.get("total").toString())
        ));
    }

    @Override
    public Map<String, Integer> getExistingHouseCounts(List<String> communityIds) {
        if (communityIds == null || communityIds.isEmpty()) {
            return new HashMap<>();
        }

        // 构造查询：SELECT community_id, COUNT(*) as count FROM house_detail ... GROUP BY ... HAVING count > 0
        QueryWrapper<HouseDetail> wrapper = new QueryWrapper<>();
        wrapper.select("community_id", "COUNT(*) as total")
                .in("community_id", communityIds)
                .eq("status", 1) // 必须是在租状态
                .groupBy("community_id")
                .having("total > 0"); // 确保返回的计数值大于0

        List<Map<String, Object>> maps = this.listMaps(wrapper);

        // 转换为 Map 结构供 Controller 快速匹配
        return maps.stream().collect(Collectors.toMap(
                m -> (String) m.get("community_id"),
                m -> Integer.parseInt(m.get("total").toString())
        ));
    }
}