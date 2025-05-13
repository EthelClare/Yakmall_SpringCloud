package com.yakmall.trade;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@OpenAPIDefinition(
        info = @Info(
                title = "交易服务API文档",
                version = "1.0.0",
                description = "交易服务API文档"
        )
)
@MapperScan("com.yakmall.trade.mapper")
@EnableConfigurationProperties()
@SpringBootApplication
public class TradeApplication {
    public static void main(String[] args) {
        SpringApplication.run(TradeApplication.class, args);
    }
}