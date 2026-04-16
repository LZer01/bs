package com.example.house.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.house.entity.Suggestion;
import com.example.house.mapper.SuggestionMapper;
import com.example.house.service.SuggestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class SuggestionServiceImpl extends ServiceImpl<SuggestionMapper, Suggestion> implements SuggestionService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Suggestion processUrge(Long userId, String communityId) {
        // 1. 构造查询条件
        LambdaQueryWrapper<Suggestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Suggestion::getUserId, userId)
                .eq(Suggestion::getCommunityId, communityId);

        // 2. 尝试获取现有记录
        Suggestion suggestion = this.getOne(queryWrapper);

        if (suggestion == null) {
            // 3. 记录不存在：新建并插入
            suggestion = new Suggestion();
            suggestion.setUserId(userId);
            suggestion.setCommunityId(communityId);
            suggestion.setLastUrgeTime(LocalDateTime.now());
            this.save(suggestion);
        } else {
            // 4. 记录已存在：更新最后催促时间
            suggestion.setLastUrgeTime(LocalDateTime.now());
            this.updateById(suggestion);
        }

        return suggestion;
    }
}