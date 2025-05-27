package com.yakmall.user;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@OpenAPIDefinition(
        info = @Info(
                title = "用户服务API文档",
                version = "1.0.0",
                description = "用户微服务接口文档"
        )
)
@MapperScan("com.yakmall.user.mapper")
@EnableConfigurationProperties()
@SpringBootApplication(scanBasePackages = {"com.yakmall.user",  "com.yakmall.common"})
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}