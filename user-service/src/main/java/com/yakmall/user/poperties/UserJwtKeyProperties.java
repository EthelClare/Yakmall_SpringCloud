package com.yakmall.user.poperties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Data
@Configuration
@Component
@ConfigurationProperties(prefix = "jwt.key")
public class UserJwtKeyProperties {
    private String keyLocation;  // jks文件路径
    private String alias;        // 密钥别名
    private String keyPass;      // 密钥密码
    private String storePass;    // 存储密码
}
