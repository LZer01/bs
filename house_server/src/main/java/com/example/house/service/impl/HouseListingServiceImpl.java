package com.example.house.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.house.mapper.HouseListingMapper;
import com.example.house.entity.HouseListing;
import com.example.house.service.HouseListingService;
import org.springframework.stereotype.Service;

@Service
public class HouseListingServiceImpl extends ServiceImpl<HouseListingMapper, HouseListing> implements HouseListingService {

    /**
     * 上下架房源
     * @param houseId 房源ID
     * @param publish 是否上架（true 上架，false 下架）
     */
    public void publishHouse(Integer houseId, boolean publish) {
        HouseListing house = this.getById(houseId);
        if (house != null) {
            house.setIsPublished(publish);
            this.updateById(house);
        }
    }
}
