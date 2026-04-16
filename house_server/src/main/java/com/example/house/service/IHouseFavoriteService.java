package com.example.house.service;

public interface IHouseFavoriteService {

    /**
     * 添加收藏
     */
    boolean addFavorite(Long houseId);

    /**
     * 取消收藏
     */
    boolean cancelFavorite(Long houseId);

    /**
     * 切换收藏状态（推荐使用）
     * @return 切换后是否为已收藏状态
     */
    boolean toggleFavorite(Long houseId);
}