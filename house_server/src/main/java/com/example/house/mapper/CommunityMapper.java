package com.example.house.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.house.entity.Community;
import org.apache.ibatis.annotations.Mapper;

/**
 * 继承 BaseMapper 后，你就拥有了 selectBatchIds, selectById 等基础增删改查功能
 */
@Mapper
public interface CommunityMapper extends BaseMapper<Community> {
    // 如果有复杂的自定义 SQL 可以在这里写，目前基础功能够用了
}