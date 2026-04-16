package com.example.house.controller;

import com.example.house.common.Result;
import com.example.house.entity.vo.HouseFavoriteVO;
import com.example.house.service.IHouseFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 房源收藏 Controller
 */
@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class HouseFavoriteController {

    private final IHouseFavoriteService favoriteService;

    /**
     * 添加收藏
     */
    @PostMapping("/add")
    public Result<Void> addFavorite(@RequestBody HouseFavoriteVO vo) {
        boolean success = favoriteService.addFavorite(vo.getHouseId());
        if (success) {
            return Result.success(null);
        }
        return Result.error("收藏失败，该房源可能已被收藏");
    }

    /**
     * 取消收藏
     */
    @PostMapping("/cancel")
    public Result<Void> cancelFavorite(@RequestBody HouseFavoriteVO vo) {
        boolean success = favoriteService.cancelFavorite(vo.getHouseId());
        if (success) {
            return Result.success(null);
        }
        return Result.error("取消收藏失败，该房源可能未被收藏");
    }

    /**
     * 切换收藏状态（强烈推荐前端使用此接口）
     * 返回切换后的收藏状态（true=已收藏，false=未收藏）
     */
    @PostMapping("/toggle")
    public Result<Boolean> toggleFavorite(@RequestBody HouseFavoriteVO vo) {
        boolean isFavorited = favoriteService.toggleFavorite(vo.getHouseId());
        return Result.success(isFavorited);
    }
}