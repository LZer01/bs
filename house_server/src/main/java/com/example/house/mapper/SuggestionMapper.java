package com.example.house.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.house.entity.Suggestion;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SuggestionMapper extends BaseMapper<Suggestion> {
    // 基础的 CRUD 已经由 BaseMapper 提供
}