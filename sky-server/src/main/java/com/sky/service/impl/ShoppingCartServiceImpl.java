package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService{

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //查询购物车是否重复
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);

        //重复则数量+1
        if (shoppingCartList !=null && shoppingCartList.size() > 0){
            shoppingCart = shoppingCartList.get(0);
            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(shoppingCart);
        }else {//不重复则插入
            //判断为套餐还是菜品
            if (shoppingCart.getDishId() != null){
                Dish dish = dishMapper.getById(shoppingCart.getDishId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }else {
                Setmeal setmeal = setmealMapper.getById(shoppingCart.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            //插入数据
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 显示购物车列表
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        ShoppingCart shoppingCar = ShoppingCart.builder()
                .userId(BaseContext.getCurrentId())
                .build();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCar);
        return shoppingCartList;
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanCart() {
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
    }

    /**
     * 删除购物车中一个商品
     * @param shoppingCartDTO
     */
    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);

        if (shoppingCartList !=null && shoppingCartList.size() > 0){
            shoppingCart = shoppingCartList.get(0);
            Integer number = shoppingCart.getNumber();
            if (number > 1){
            shoppingCart.setNumber(shoppingCart.getNumber() - 1);
            shoppingCartMapper.updateNumberById(shoppingCart);
            }else {
                shoppingCartMapper.deleteById(shoppingCart.getId());
            }
        }
    }
}
