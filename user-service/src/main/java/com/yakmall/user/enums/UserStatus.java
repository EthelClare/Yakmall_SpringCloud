package com.yakmall.user.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 用于表示用户的状态
 */
public enum UserStatus {
    FROZEN(0, "禁止使用"),
    NORMAL(1, "正常状态");

    @EnumValue
    int value;
    String desc;
    UserStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 使用了便利，扩展之后也不用修改这个方法
     * 当数据库存储用户状态为整数（如0或1）时，此方法可将查询到的整数值转换为对应的枚举类型，方便代码中使用枚举而非硬编码数字。
     * 若前端传入状态值为整数（如通过API参数），此方法将其转换为枚举，确保后续逻辑处理类型安全。
     * @param value
     * @return
     */
    public static UserStatus of(int value) {
        for (UserStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的用户状态值: " + value);
    }

}
