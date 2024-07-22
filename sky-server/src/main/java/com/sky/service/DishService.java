package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    void saveWithFlavor(DishDTO dishDTO);

    /**
     * 分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 删除
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    DishVO selectById(Long id);

    /**
     * 修改
     * @param dishDTO
     */
    void update(DishDTO dishDTO);

    /**
     * 修改状态
     * @param status
     * @param id
     */
    void changeStatus(Integer status, Long id);

    /**
     * 根据分类返回菜品
     * @param dishDTO
     * @return
     */
    List<Dish> list(DishDTO dishDTO);


    /**
     * 根据分类id查询菜品
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}
