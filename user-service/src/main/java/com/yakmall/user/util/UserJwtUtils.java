package com.yakmall.user.util;

import com.yakmall.user.poperties.UserJwtKeyProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

//TODO 配置redis
@Slf4j
@Component
@RequiredArgsConstructor
public class UserJwtUtils {

    private final UserJwtKeyProperties userJwtKeyProperties;
    private final RedisTemplate<String, Object> redisTemplate;


    // 获取公钥
    public PublicKey getPublicKey() {
        try {
            // 打印调试信息
            log.info("加载 JKS 文件: 路径={}, 别名={}", userJwtKeyProperties.getKeyLocation(), userJwtKeyProperties.getAlias());

            KeyStore keyStore = KeyStore.getInstance("JKS");
            ClassPathResource resource = new ClassPathResource(userJwtKeyProperties.getKeyLocation());
            try (InputStream inputStream = resource.getInputStream()) {
                keyStore.load(inputStream, userJwtKeyProperties.getStorePass().toCharArray());
                return keyStore.getCertificate(userJwtKeyProperties.getAlias()).getPublicKey();
            }
        } catch (Exception e) {
            log.error("公钥加载失败", e);
            throw new RuntimeException("公钥加载失败");
        }
    }

    // 获取私钥
    private PrivateKey getPrivateKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            ClassPathResource resource = new ClassPathResource(userJwtKeyProperties.getKeyLocation());
            try (InputStream inputStream = resource.getInputStream()) {
                keyStore.load(inputStream, userJwtKeyProperties.getStorePass().toCharArray());
                return (PrivateKey) keyStore.getKey(
                        userJwtKeyProperties.getAlias(),
                        userJwtKeyProperties.getKeyPass().toCharArray()
                );
            }
        } catch (Exception e) {
            log.error("私钥加载失败", e);
            throw new RuntimeException("私钥加载失败");
        }
    }


    // 生成Token
    //这里使用的userId来作为生成token
    public String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000)) // 30分钟
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    // 验证Token有效性
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getPublicKey())
                    .build()
                    .parseClaimsJws(token);
            return !isTokenBlacklisted(token);
        } catch (Exception e) {
            return false;
        }
    }

    // 检查令牌黑名单
    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("jwt:blacklist:" + token));
    }

    // 解析用户 ID（Long 类型）
    public Long getUserIdFromToken(String token) {
        try {
            String subject = Jwts.parserBuilder()
                    .setSigningKey(getPublicKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            return Long.parseLong(subject); // 将字符串转换为 Long
        } catch (NumberFormatException e) {
            log.error("Token 中的用户 ID 格式错误: {}", token, e);
            throw new IllegalArgumentException("无效的用户 ID");
        } catch (Exception e) {
            log.error("解析 Token 失败: {}", token, e);
            throw new RuntimeException("Token 解析失败");
        }
    }

    // 解析用户名
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

//    // 在JwtUtils中添加
//    public void addToBlacklist(String token) {
//        Date expiration = getExpirationFromToken(token);
//        long ttl = expiration.getTime() - System.currentTimeMillis();
//        if (ttl > 0) {
//            redisTemplate.opsForValue().set(
//                    "jwt:blacklist:" + token,
//                    "revoked",
//                    ttl,
//                    TimeUnit.MILLISECONDS
//            );
//        }
//    }
}