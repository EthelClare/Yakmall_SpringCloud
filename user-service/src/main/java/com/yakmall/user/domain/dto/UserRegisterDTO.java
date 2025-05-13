package com.yakmall.user.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotNull;


@Data
@Schema(description = "用户注册表单实体")
public class UserRegisterDTO {
    @Schema(description = "用户名")
    @NotNull(message = "用户名不能为空")
    private String username;


    @Schema(description = "登陆密码")
    @NotNull(message = "密码不能为空")
    private String password;


    @Schema(description = "手机号")
    @NotNull(message = "手机号不能为空")
    private String mobile;
}
