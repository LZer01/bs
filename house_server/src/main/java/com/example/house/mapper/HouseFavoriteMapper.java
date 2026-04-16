package com.example.house.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.house.entity.HouseFavorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户收藏房源 Mapper 接口
 */
@Mapper
public interface HouseFavoriteMapper extends BaseMapper<HouseFavorite> {

    // 如果后续需要写自定义SQL，可以在这里添加方法
    // 例如：统计某个房源被收藏次数、查询用户收藏列表等
}