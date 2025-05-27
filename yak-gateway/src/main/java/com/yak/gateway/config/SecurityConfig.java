package com.yak.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity  // 启用 WebFlux 安全配置
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // 关闭 CSRF 防护
                .csrf().disable()
                // 其他安全配置（默认允许所有请求）
                .authorizeExchange()
                .anyExchange().permitAll()
                .and()
                .build();
    }
}
