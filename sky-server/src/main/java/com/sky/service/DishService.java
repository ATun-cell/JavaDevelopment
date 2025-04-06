package com.sky.service;

import com.sky.dto.DishDTO;
import org.springframework.stereotype.Service;


public interface DishService {
    /**
     * 新增菜品
     * @param dish
     */
    void save(DishDTO dish);
}
