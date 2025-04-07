package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.sky.constant.MessageConstant.DISH_BE_RELATED_BY_SETMEAL;
import static com.sky.constant.MessageConstant.DISH_ON_SALE;
import static com.sky.constant.StatusConstant.ENABLE;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品
     *
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

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //开启分页插件
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id批量删除菜品
     *
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(List<Long> ids) {
        //判断这些id对应的菜品是否正在售卖，是则不能删除
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (Objects.equals(dish.getStatus(), ENABLE)) {
                throw new DeletionNotAllowedException(DISH_ON_SALE);
            }
        }
        //判断这些id对应的菜品是否被套餐关联，是则不能删除
        List<Long> setmealId = setmealDishMapper.getByDishId(ids);
        if (setmealId != null && !setmealId.isEmpty()) {
            throw new DeletionNotAllowedException(DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品和这些菜品对应的风味
        dishMapper.deleteByIds(ids);//删除菜品表中项目
        dishFlavorMapper.deleteByDishIds(ids);
    }

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    @Override
    public DishVO seachById(Long id) {
        DishVO dishVO = new DishVO();
        //查询到菜品信息并赋值给dishVO
        Dish dish = dishMapper.getById(id);
        BeanUtils.copyProperties(dish, dishVO);
        //查询到风味信息并赋值给dishVO
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    /**
     * 修改菜品信息
     *
     * @param dishDTO
     */
    @Override
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //先修改菜品表基本信息
        dishMapper.update(dish);
        //删除风味表对应菜品
        dishFlavorMapper.deleteByDishId(dish.getId());
        //加入新的风味信息
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            for (DishFlavor df : flavors) {
                df.setDishId(dishDTO.getId());
            }
        }
        dishFlavorMapper.save(flavors);
    }

    /**
     * 菜品状态转换
     *
     * @param id
     * @param status
     */
    @Override
    public void switchStatus(Long id, Integer status) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }
}
