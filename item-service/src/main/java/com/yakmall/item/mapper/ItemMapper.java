package com.yakmall.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yakmall.item.domain.po.Item;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Repository
public interface ItemMapper extends BaseMapper<Item> {

//    // Mapper 接口
//    @Update("<script>" +
//            "<foreach collection='paramMap' index='id' item='quantity' separator=';'>" +
//            "UPDATE item " +
//            "SET stock = stock - #{quantity} " +
//            "WHERE id = #{id} " +
//            "AND stock >= #{quantity}" +
//            "</foreach>" +
//            "</script>")
    @Transactional
    void batchDeductByMap(@Param("paramMap") Map<Long, Integer> paramMap);
}
