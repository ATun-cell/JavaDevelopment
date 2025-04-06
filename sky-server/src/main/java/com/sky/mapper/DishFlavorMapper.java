package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 新增菜品风味
     * @param dishFlavors
     */
    void save(List<DishFlavor> dishFlavors);

    /**
     * 根绝菜品ids删除风味
     * @param ids
     */
    void deleteByDishIds(List<Long> ids);

    /**
     * 根据菜品id查询对应风味
     * @param id
     * @return
     */
    @Select("select * from dish_flavor where dish_id=#{id}")
    List<DishFlavor> getByDishId(Long id);

    /**
     * 根据菜品id删除风味
     * @param id
     */
    @Delete("delete from dish_flavor where dish_id=#{id}")
    void deleteByDishId(Long id);
}
