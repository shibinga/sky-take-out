package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 订单超时
     */
    @Scheduled(cron = "0 0/1 * * * ? ")
    //@Scheduled(cron = "0/5 * * * * ? ")
    public void timeoutOrder(){
        log.info("超时订单: {}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().minusMinutes(15);
        List<Orders> ordersList = orderMapper.getByStatusWithOrderTime(Orders.PENDING_PAYMENT,time);
        if (ordersList != null && ordersList.size() > 0){
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }


    /**
     * 持续派送订单
     */
    @Scheduled(cron = "0 0 1 * * ? ")
    //@Scheduled(cron = "0/10 * * * * ? ")
    public void deliveryOrder(){
        log.info("持续派送订单: {}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().minusHours(1);
        List<Orders> ordersList = orderMapper.getByStatusWithOrderTime(Orders.DELIVERY_IN_PROGRESS,time);
        if (ordersList != null && ordersList.size() > 0){
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }
}
