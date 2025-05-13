package com.yak.gateway.util;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.yak.gateway.constant.JwtConstant;
import com.yakmall.common.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.time.Duration;
import java.util.Date;


@Slf4j
@Component
public class JwtTool {

    private final JWTSigner jwtSigner;



    private static final String PAYLOAD_TOKEN_TYPE = "type";

    /**
     * KeyPair 提供了非对称加密所需的密钥对，使得 JWT 的签名（私钥）和验证（公钥）
     * 过程安全分离，确保 Token 的不可伪造性和合法性，适用于需要高安全性的场景。
     * @param keyPair
     */
    public JwtTool(KeyPair keyPair) {
        this.jwtSigner = JWTSignerUtil.createSigner("RS256", keyPair);
    }


    /**
     * 通过用户的id来创建一个token，并且设置有效期
     * @param userid 用户id
     * @param ttl 有效期
     * @return
     */
    public String createJWT(Long userid, Duration ttl) {
        return JWT.create()
                .setPayload(JwtConstant.PAYLOAD_USER_KEY, userid)
                .setExpiresAt(new Date(System.currentTimeMillis() + ttl.toMillis()))
                .setSigner(jwtSigner)
                .sign();
    }


    public Long parseToken(String token) {
        validateTokenNotEmpty(token);
        JWT jwt = parseAndVerifyJwt(token);
        validateExpiration(jwt);
        return extractUserId(jwt);
    }


    private void validateTokenNotEmpty(String token) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("未登录");
        }
    }


    private JWT parseAndVerifyJwt(String token) {
        try {
            JWT jwt = JWT.of(token).setSigner(jwtSigner);
            if (!jwt.verify()) {
                throw new UnauthorizedException("无效的Token签名");
            }
            return jwt;
        } catch (Exception e) {
            log.error("Token解析失败: {}", token, e);
            throw new UnauthorizedException("无效的Token");
        }
    }


    private void validateExpiration(JWT jwt) {
        try {
            JWTValidator.of(jwt).validateDate();
        } catch (ValidateException e) {
            log.warn("Token已过期: {}", jwt.getPayload());
            throw new UnauthorizedException("Token已过期");
        }
    }


    private Long extractUserId(JWT jwt) {
        Object userPayload = jwt.getPayload(JwtConstant.PAYLOAD_USER_KEY);
        if (userPayload == null) {
            throw new UnauthorizedException("Token缺少用户信息");
        }

        try {
            if (userPayload instanceof Number) {
                return ((Number) userPayload).longValue();
            } else {
                return Long.parseLong(userPayload.toString());
            }
        } catch (NumberFormatException e) {
            log.error("用户ID格式错误: {}", userPayload);
            throw new UnauthorizedException("无效的用户ID格式");
        }
    }


}
