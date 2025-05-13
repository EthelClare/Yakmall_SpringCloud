package com.yakmall.cart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yakmall.cart.domain.po.Cart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Set;

public interface CarMapper extends BaseMapper<Cart> {

    @Update("UPDATE cart SET num = num + 1 WHERE user_id = #{userId} AND item_id = #{itemId}")
    void updateNum(@Param("itemId") Long itemId, @Param("userId") Long userId);

    // 在CartMapper中添加
    @Update("UPDATE cart SET num = num + #{num} WHERE item_id = #{itemId} AND user_id = #{userId}")
    int incrementNum(@Param("itemId") Long itemId, @Param("userId") Long userId, @Param("num") Integer num);

    @Delete("<script>" +
            "DELETE FROM cart " +
            "WHERE user_id = #{userId} " +
            "AND item_id IN " +
            "<foreach collection='itemIds' item='id' open='(' separator=',' close=')'>" +
            "   #{id}" +
            "</foreach>" +
            "</script>")
    int deleteByUserIdAndItemIds(@Param("userId") Long userId,
                                 @Param("itemIds") Set<Long> itemIds);
}
