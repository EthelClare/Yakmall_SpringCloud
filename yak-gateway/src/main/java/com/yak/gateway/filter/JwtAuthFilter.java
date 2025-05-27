package com.yak.gateway.filter;

import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.yakmall.common.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final JwtUtils jwtUtils;

    
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/users/login",
            "/users/register",
            "/swagger-ui/**"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 白名单直接放行
        if (WHITE_LIST.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        // 获取Token
        String token = request.getHeaders().getFirst("Authorization");
        if (StringUtils.isEmpty(token) || !token.startsWith("Bearer ")) {
            return unauthorizedResponse(exchange, "缺少有效令牌");
        }
        token = token.substring(7);

        // 验证Token
        if (!jwtUtils.validateToken(token)) {
            return unauthorizedResponse(exchange, "令牌无效或已过期");
        }
//
//        // 传递用户信息到下游服务
//        String username = jwtUtils.getUsernameFromToken(token);
//        exchange.getRequest().mutate()
//                .header("X-User-Id", username)
//                .build();

        try {
            Long userId = jwtUtils.getUserIdFromToken(token);

            // 将 userId 添加到请求头
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", userId.toString())
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            return unauthorizedResponse(exchange, "令牌解析失败");
        }
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");
        Map<String, Object> result = Map.of(
                "code", 401,
                "message", message,
                "timestamp", System.currentTimeMillis()
        );
        DataBuffer buffer = response.bufferFactory().wrap(JSONUtil.toJsonStr(result).getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return 0; // 高优先级
    }
}