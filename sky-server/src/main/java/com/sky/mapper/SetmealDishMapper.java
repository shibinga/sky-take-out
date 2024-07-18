package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {


    /**
     * 根据菜品id查询套餐id
     * @param ids
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> ids);

    @Select("select * from setmeal_dish where dish_id = #{dishId}")
    List<SetmealDish> getSetmealIdsByDishId(Long dishId);

    /**
     * 批量插入操作
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据id删除
     * @param setmealIds
     */
    void deleteByIds(List<Long> setmealIds);

    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteById(Long setmealId);

    /**
     * 根据套餐id查寻关联菜品
     * @param setmealId
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id =#{setmealId}")
    List<SetmealDish> getSetmealdishBySetmeal(Long setmealId);


    List<SetmealDish> getSetmealdishBySetmeals();
}
