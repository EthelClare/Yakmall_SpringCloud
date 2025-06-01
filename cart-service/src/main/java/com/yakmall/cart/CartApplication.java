package com.yakmall.cart;

import com.yakmall.api.config.DefaultFeignConfig;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@OpenAPIDefinition(
        info = @Info(
                title = "购物车服务API文档",
                version = "1.0.0",
                description = "购物车微服务接口文档"
        )
)
@EnableFeignClients(basePackages = "com.yakmall.api.client", defaultConfiguration = DefaultFeignConfig.class)
@MapperScan("com.yakmall.cart.mapper")
@SpringBootApplication(scanBasePackages = {"com.yakmall.cart" ,"com.yakmall.common.aop"})
public class CartApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class, args);
    }
}