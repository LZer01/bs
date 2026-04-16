package com.example.house.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.house.entity.HouseFavorite;
import com.example.house.mapper.HouseFavoriteMapper;
import com.example.house.service.IHouseFavoriteService;
import com.example.house.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HouseFavoriteServiceImpl extends ServiceImpl<HouseFavoriteMapper, HouseFavorite>
        implements IHouseFavoriteService {

    @Override
    public boolean addFavorite(Long houseId) {
        Long userId = UserContext.getCurrentUserId();   // 请替换为你的用户获取方式

        HouseFavorite favorite = new HouseFavorite();
        favorite.setUserId(userId);
        favorite.setHouseId(houseId);

        // 利用唯一索引，插入失败说明已存在
        return this.save(favorite);
    }

    @Override
    public boolean cancelFavorite(Long houseId) {
        Long userId = UserContext.getCurrentUserId();

        return this.remove(new LambdaQueryWrapper<HouseFavorite>()
                .eq(HouseFavorite::getUserId, userId)
                .eq(HouseFavorite::getHouseId, houseId));
    }

    @Override
    public boolean toggleFavorite(Long houseId) {
        Long userId = UserContext.getCurrentUserId();

        // 先查询是否已收藏
        boolean exists = this.exists(new LambdaQueryWrapper<HouseFavorite>()
                .eq(HouseFavorite::getUserId, userId)
                .eq(HouseFavorite::getHouseId, houseId));

        if (exists) {
            // 已收藏 → 取消
            cancelFavorite(houseId);
            return false;   // 切换后为未收藏
        } else {
            // 未收藏 → 添加
            addFavorite(houseId);
            return true;    // 切换后为已收藏
        }
    }
}