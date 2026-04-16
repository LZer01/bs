package com.example.house.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.house.common.Result;
import com.example.house.entity.Suggestion;
import com.example.house.entity.dto.SuggestionDTO;
import com.example.house.service.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/suggestion")
public class SuggestionController {

    @Autowired
    private SuggestionService suggestionService;

    @PostMapping("/urge")
    public Result urge(@RequestBody SuggestionDTO urgeDto) {
        // 1. 参数校验
        System.out.println(urgeDto.getCommunityId());
        System.out.println(urgeDto.getUserId());
        if (urgeDto.getUserId() == null || urgeDto.getCommunityId() == null) {
            return Result.error("参数不能为空");
        }

        // 2. 查询是否已经存在该用户的记录
        LambdaQueryWrapper<Suggestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Suggestion::getUserId, urgeDto.getUserId())
                .eq(Suggestion::getCommunityId, urgeDto.getCommunityId());

        Suggestion existingRecord = suggestionService.getOne(queryWrapper);

        if (existingRecord == null) {
            // 3. 情况A：首次提交建议/催促，插入记录
            Suggestion newRecord = new Suggestion();
            newRecord.setUserId(urgeDto.getUserId());
            newRecord.setCommunityId(urgeDto.getCommunityId());
            newRecord.setLastUrgeTime(LocalDateTime.now());
            suggestionService.save(newRecord);
            return Result.success("首次提交成功");
        } else {
            // 4. 情况B：已存在记录，仅更新最后催促时间
            existingRecord.setLastUrgeTime(LocalDateTime.now());
            suggestionService.updateById(existingRecord);
            return Result.success("催促成功，已提醒后台");
        }
    }
}