package com.example.house.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.house.entity.Suggestion;

/**
 * 建议表 服务类
 */
public interface SuggestionService extends IService<Suggestion> {
    /**
     * 处理催促逻辑：存在则更新时间，不存在则创建
     * @param userId 用户ID
     * @param communityId 小区ID
     * @return 更新后的对象
     */
    Suggestion processUrge(Long userId, String communityId);
}