package com.yakmall.trade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yakmall.trade.domain.po.Order;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface OrderMapper extends BaseMapper<Order> {

    @Update("update orders set status = #{status} where id = #{orderId}")
    void updateStatus(@Param("orderId") Long orderId, @Param("status") Integer status);

}
