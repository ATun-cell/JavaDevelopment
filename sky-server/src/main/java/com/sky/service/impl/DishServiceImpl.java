package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品
     * @param dish
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(DishDTO dish) {
        //将DTO转为对应实体对象
        Dish dishEntity = new Dish();
        BeanUtils.copyProperties(dish, dishEntity);
        //向菜品表中添加一项
        dishMapper.save(dishEntity);
        //获取菜品的id赋给falor对象
        List<DishFlavor> flavors = dish.getFlavors();
        for (DishFlavor df : flavors) {
            df.setDishId(dishEntity.getId());
        }
        //向菜品风味表添加菜品对应口味
        dishFlavorMapper.save(flavors);

    }
}
