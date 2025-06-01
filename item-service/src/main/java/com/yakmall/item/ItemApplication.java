package com.yakmall.item;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@OpenAPIDefinition(
        info = @Info(
                title = "商品服务API文档",
                version = "1.0.0",
                description = "商品微服务接口文档"
        )
)
@MapperScan("com.yakmall.item.mapper")
@SpringBootApplication(scanBasePackages = {"com.yakmall.item" ,"com.yakmall.common.aop"})
public class ItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItemApplication.class, args);
    }
}