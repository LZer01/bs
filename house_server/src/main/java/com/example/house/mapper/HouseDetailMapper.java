package com.example.house.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.house.entity.HouseDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 房源详情 Mapper 接口
 * 继承 BaseMapper 即可获得 CRUD 以及聚合查询能力
 */
@Mapper
public interface HouseDetailMapper extends BaseMapper<HouseDetail> {

    // 如果后续有极其复杂的自定义 SQL（比如复杂的关联查询），可以在这里定义方法
    // 并在对应的 XML 文件中实现
}