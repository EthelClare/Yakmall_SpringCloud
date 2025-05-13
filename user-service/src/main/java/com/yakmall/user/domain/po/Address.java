package com.yakmall.user.domain.po;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)  //仅基于当前类的字段生成方法（默认行为）。
@NoArgsConstructor
@TableName("address")
@Accessors(chain = true) //这个注解是用来修改setter方法，使其支持链式调用，setter方法会返回这个对象本身
@Schema(description = "地址实体")
public class Address implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 这是Address在数据库中的唯一id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 这是这个地址对应的 用户的id
     */
    private Long userId;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 县/ 区
     */
    private String town;

    /**
     * 详细地址
     */
    private String street;

    /**
     * 收货人手机号码
     */
    private String mobile;

    /**
     * 是否是默认地址
     */
    private Integer isDefault;

    /**
     * 地址标签
     * TODO 这里需要使用一个枚举类型类枚举这些标签
     *
     */
    private String note;
}
