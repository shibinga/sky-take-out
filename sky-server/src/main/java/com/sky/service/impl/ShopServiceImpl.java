package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置营业状态
     * @param status
     */
    @Override
    public void setStatus(Integer status) {
        redisTemplate.opsForValue().set(StatusConstant.SHOP_STATUS,status);
    }

    /**
     * 获取营业状态
     * @return
     */
    @Override
    public Integer getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(StatusConstant.SHOP_STATUS);
        return status;
    }
}
