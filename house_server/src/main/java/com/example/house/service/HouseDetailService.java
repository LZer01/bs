package com.example.house.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.house.entity.HouseDetail;
import java.util.Map;
import java.util.List;

public interface HouseDetailService extends IService<HouseDetail> {

    /**
     * 根据小区ID列表，统计每个小区的在租房源数量
     * @param communityIds 小区ID集合
     * @return Map<小区ID, 房源数量>
     */
    Map<String, Integer> getHouseCountByCommunityIds(List<String> communityIds);

    Map<String, Integer> getExistingHouseCounts(List<String> communityIds);
    // 后续可以增加：根据价格、标签筛选房源的方法
}