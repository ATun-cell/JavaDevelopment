package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 新增菜品风味
     * @param dishFlavors
     */
    void save(List<DishFlavor> dishFlavors);
}
