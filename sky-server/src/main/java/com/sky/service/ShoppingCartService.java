package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    void add(ShoppingCartDTO shoppingCartDTO);

    /**
     * 显示购物车列表
     * @return
     */
    List<ShoppingCart> list();

    /**
     * 清空购物车
     */
    void cleanCart();

    /**
     * 删除购物车中一个商品
     * @param shoppingCartDTO
     */
    void sub(ShoppingCartDTO shoppingCartDTO);
}
