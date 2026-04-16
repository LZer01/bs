package com.example.house.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.house.entity.HouseListing;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HouseListingMapper extends BaseMapper<HouseListing> {
}
