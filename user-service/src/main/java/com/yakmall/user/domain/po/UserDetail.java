package com.yakmall.user.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 这个类中放的是用户的详细信息【相对来说查询的p】
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("userDetail")
public class UserDetail implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     *  对应的是 UserBase 中的 id。
     */
    private Long userId;

    /**
     * 表示性别，1表示男， 0表示女
     * TODO 修改成将 1 和 男对应， 0和女对应
     */
    private Integer gender;

    /**
     * 生日
     */
    private LocalDateTime birthday;

    /**
     * 邮箱
     */
    private String email;


    /**
     * 头像地址
     */
    private String avatarUrl;



}
