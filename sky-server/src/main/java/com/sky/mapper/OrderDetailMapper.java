package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderDetailMapper {

    /**
     * 批量插入
     * @param orderDetails
     */
    void insertBatch(List<OrderDetail> orderDetails);

    /**
     * id查询
     * @param orderId
     * @return
     */
    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> getsByOrderId(Long orderId);

    /**
     * 查询销量前10
     * @param beginTime
     * @param endTime
     * @return
     */
    List<GoodsSalesDTO> SalesTop10(LocalDateTime beginTime, LocalDateTime endTime);
}
