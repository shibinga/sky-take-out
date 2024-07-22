package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    /**
     * 条件查询购物车
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);


    /**
     * 数量+1
     * @param shoppingCart
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    /**
     * 插入
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, amount, create_time, number) " +
    "values (#{name}, #{image}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{amount}, #{createTime}, #{number})"
    )
    void insert(ShoppingCart shoppingCart);

    /**
     * 根据userId删除
     * @param UserId
     */
    @Delete("delete from shopping_cart where user_id = #{UserId}")
    void deleteByUserId(Long UserId);

    /**
     * 根据Id删除
     * @param id
     */
    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(Long id);
}
