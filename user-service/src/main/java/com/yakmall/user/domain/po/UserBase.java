package com.yakmall.user.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yakmall.user.enums.UserStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 这是用户信息的基础信息，将用户信息分表查询，不常使用的字段将其放到另一个详细中去
 * 对每个实体类都要实现 Serializable 接口的原因--》 是因为 redis、session还有mybatis-plus等等都需要实体类实现这个Serializable接口
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("userBase")
public class UserBase implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * 这个用作给系统来统计和判断用户的唯一性[唯一]
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 账户名【可修改，但需要限定时间和次数】
     */
    private String username;

    /**
     * 密码【加密储存】
     */
    private String password;

    /**
     * 手机号【唯一】
     */
    private String mobile;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     使用状态
     *
     */
    private UserStatus status;

    /**
     *
     * 用户最后登陆时间 TODO 这里用作后期将它弄成一个微服务项目之后，开启一个单独的服务用来给用户发消息优惠消息等等
     */
    private LocalDateTime lastLoginTime;
}
