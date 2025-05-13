package com.yakmall.user.controller;


import com.yakmall.common.result.Result;
import com.yakmall.user.domain.dto.LoginFormDTO;
import com.yakmall.user.domain.dto.UserRegisterDTO;
import com.yakmall.user.domain.vo.UserLoginVO;
import com.yakmall.user.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;


    /**
     * 用户登陆
     * @param loginFormDTO
     * @return
     */
    @Operation(summary = "用户登陆接口")
    @PostMapping("login")
    public Result<UserLoginVO> login(@RequestBody @Valid LoginFormDTO loginFormDTO){
        return userService.login(loginFormDTO);
    }


    /**
     * 用户注册
     * @param userRegisterDTO
     * @return
     */
    @Operation(summary = "用户注册接口")
    @PostMapping("register")
    public  Result<Void> register (@Valid @RequestBody UserRegisterDTO userRegisterDTO){
        return userService.register(userRegisterDTO);
    }

//  退出功能稍后完成TODO
//
//    以下是基于你提供的 JwtTool 类，结合 ​​短期 Token + 刷新 Token​​ 方案实现退出登录的改进代码：
//
//    一、新增功能说明
//​​生成两种 Token​​：
//            ​​Access Token​​：短期有效（如 30 分钟），用于接口请求。
//            ​​Refresh Token​​：长期有效（如 7 天），存储于 Redis 用于刷新 Access Token。
//            ​​退出逻辑​​：
//    用户退出时删除 Redis 中的 Refresh Token，使其无法刷新 Access Token。
//    二、修改后的 JwtTool 类
//package com.yak.common.utils;
//
//import cn.hutool.core.exceptions.ValidateException;
//import cn.hutool.jwt.JWT;
//import cn.hutool.jwt.JWTValidator;
//import cn.hutool.jwt.signers.JWTSigner;
//import cn.hutool.jwt.signers.JWTSignerUtil;
//import com.yak.common.exception.UnauthorizedException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.security.KeyPair;
//import java.time.Duration;
//import java.util.Date;
//import java.util.UUID;
//
//    @Slf4j
//    @Component
//    public class JwtTool {
//        private final JWTSigner jwtSigner;
//
//        private static final String PAYLOAD_USER_KEY = "user";
//        private static final String PAYLOAD_TOKEN_TYPE = "type";
//
//        public JwtTool(KeyPair keyPair) {
//            this.jwtSigner = JWTSignerUtil.createSigner("RS256", keyPair);
//        }
//
//        // 生成 Access Token（短期）
//        public String createAccessToken(Long userId, Duration ttl) {
//            return JWT.create()
//                    .setPayload(PAYLOAD_USER_KEY, userId)
//                    .setPayload(PAYLOAD_TOKEN_TYPE, "access")
//                    .setExpiresAt(new Date(System.currentTimeMillis() + ttl.toMillis()))
//                    .setSigner(jwtSigner)
//                    .sign();
//        }
//
//        // 生成 Refresh Token（长期）
//        public String createRefreshToken(Long userId, Duration ttl) {
//            return JWT.create()
//                    .setPayload(PAYLOAD_USER_KEY, userId)
//                    .setPayload(PAYLOAD_TOKEN_TYPE, "refresh")
//                    .setExpiresAt(new Date(System.currentTimeMillis() + ttl.toMillis()))
//                    .setSigner(jwtSigner)
//                    .sign();
//        }
//
//        // 解析 Token 中的用户ID
//        public Long parseUserId(String token) {
//            validateTokenNotEmpty(token);
//            JWT jwt = parseAndVerifyJwt(token);
//            validateExpiration(jwt);
//            return extractUserId(jwt);
//        }
//
//        // 解析 Token 类型（Access/Refresh）
//        public String parseTokenType(String token) {
//            validateTokenNotEmpty(token);
//            JWT jwt = parseAndVerifyJwt(token);
//            return jwt.getPayload(PAYLOAD_TOKEN_TYPE).toString();
//        }
//
//        // 其他原有方法保持不变...
//    }
//    三、服务层实现退出逻辑
//1. 登录接口生成双 Token
//    @PostMapping("/login")
//    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
//        // 1. 验证用户名密码
//        User user = userService.authenticate(request.getUsername(), request.getPassword());
//
//        // 2. 生成 Access Token（30分钟）
//        String accessToken = jwtTool.createAccessToken(user.getId(), Duration.ofMinutes(30));
//
//        // 3. 生成 Refresh Token（7天）
//        String refreshToken = jwtTool.createRefreshToken(user.getId(), Duration.ofDays(7));
//
//        // 4. 存储 Refresh Token 到 Redis（Key: refresh_token:{userId}）
//        String redisKey = "refresh_token:" + user.getId();
//        redisTemplate.opsForValue().set(
//                redisKey,
//                refreshToken,
//                Duration.ofDays(7)
//        );
//
//        return Result.success(new LoginResponse(accessToken, refreshToken));
//    }
//2. 刷新 Access Token 接口
//    @PostMapping("/refresh")
//    public Result<AccessTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
//        // 1. 解析 Refresh Token 中的用户ID和类型
//        Long userId = jwtTool.parseUserId(request.getRefreshToken());
//        String tokenType = jwtTool.parseTokenType(request.getRefreshToken());
//
//        // 2. 校验是否为 Refresh Token
//        if (!"refresh".equals(tokenType)) {
//            throw new UnauthorizedException("无效的 Token 类型");
//        }
//
//        // 3. 校验 Redis 中的 Refresh Token 是否匹配
//        String redisKey = "refresh_token:" + userId;
//        String storedRefreshToken = redisTemplate.opsForValue().get(redisKey);
//        if (!request.getRefreshToken().equals(storedRefreshToken)) {
//            throw new UnauthorizedException("Refresh Token 无效");
//        }
//
//        // 4. 生成新 Access Token
//        String newAccessToken = jwtTool.createAccessToken(userId, Duration.ofMinutes(30));
//
//        return Result.success(new AccessTokenResponse(newAccessToken));
//    }
//3. 退出登录接口
//    @PostMapping("/logout")
//    public Result<Void> logout(@RequestHeader("Authorization") String accessToken) {
//        // 1. 解析 Access Token 获取用户ID
//        Long userId = jwtTool.parseUserId(accessToken.replace("Bearer ", ""));
//
//        // 2. 删除 Redis 中的 Refresh Token（使所有设备失效）
//        String redisKey = "refresh_token:" + userId;
//        redisTemplate.delete(redisKey);
//
//        // 3. 清除 Spring Security 上下文
//        SecurityContextHolder.clearContext();
//
//        return Result.success("退出成功");
//    }
//    四、关键安全措施
//​​Token 类型区分​​：
//    Access Token 和 Refresh Token 在 Payload 中添加 type 字段，防止混淆使用。
//            ​​Redis 存储校验​​：
//    每次刷新时检查客户端提交的 Refresh Token 是否与 Redis 中一致。
//            ​​强制 HTTPS​​：
//    确保 Token 在加密通道传输，防止中间人攻击。
//            ​​短期有效期​​：
//    Access Token 有效期建议 ≤ 1 小时，Refresh Token ≤ 7 天。
//    五、客户端处理逻辑
//​​Token 存储​​：
//    Access Token：存于内存或 localStorage（需防范 XSS）。
//    Refresh Token：建议通过 HttpOnly + Secure Cookie 传输。
//            ​​自动刷新​​：
//    // 检查 Access Token 是否即将过期
//    function checkTokenExpiration() {
//  const expiresIn = getTokenExpiresIn(accessToken);
//        if (expiresIn < 300) { // 5分钟内过期
//            refreshAccessToken();
//        }
//    }
//    setInterval(checkTokenExpiration, 60000); // 每分钟检查一次
//​​退出处理​​：
//    function logout() {
//        // 调用退出接口
//        await fetch('/api/logout', { method: 'POST', headers: { Authorization: 'Bearer ' + accessToken } });
//        // 清除客户端 Token
//        localStorage.removeItem('access_token');
//    }
//    六、总结
//    通过上述改进，你的 JwtTool 可以支持生产级的退出登录功能。关键点在于：
//
//            ​​双 Token 机制​​：分离访问和刷新权限。
//            ​​Redis 状态管理​​：通过删除 Refresh Token 实现全局退出。
//            ​​类型区分​​：防止 Token 被错误使用。
//    此方案在安全性和用户体验之间取得平衡，适合大多数中大型应用场景。

}
