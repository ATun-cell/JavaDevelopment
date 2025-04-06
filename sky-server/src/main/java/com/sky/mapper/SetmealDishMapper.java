package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品ids获取对应套餐id
     * @param dishIds
     * @return
     */
    List<Long> getByDishId(List<Long> dishIds);
}
