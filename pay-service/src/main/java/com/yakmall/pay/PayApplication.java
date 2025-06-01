package com.yakmall.pay;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@OpenAPIDefinition(
        info = @Info(
                title = "支付服务API文档",
                version = "1.0.0",
                description = "支付微服务接口文档"
        )
)
@MapperScan("com.yakmall.pay.mapper")
@SpringBootApplication(scanBasePackages = {"com.yakmall.pay" ,"com.yakmall.common.aop"})
public class PayApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class, args);
    }
}