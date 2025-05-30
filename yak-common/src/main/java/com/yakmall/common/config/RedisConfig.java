package com.yakmall.common.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {


    //optimize 由于这个类放在了没有common模块下面，没有启动器，所以导致必须手动定义RedisConnectionFactory

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}") // 如果有密码
    private String password;

    @Value("${spring.redis.database:0}")
    private int database;

    @Value("${spring.redis.connect-timeout:1000}")
    private int connectTimeout;

    @Value("${spring.redis.lettuce.pool.max-active:20}")
    private int maxActive;

    @Value("${spring.redis.lettuce.pool.max-wait:-1}")
    private long maxWait;

    @Value("${spring.redis.lettuce.pool.min-idle:0}")
    private int minIdle;

    @Value("${spring.redis.lettuce.pool.max-idle:10}")
    private int maxIdle;


    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // 1. 基础配置
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setPassword(password);
        config.setDatabase(database);

        // 2. 连接池配置
        GenericObjectPoolConfig<Object> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxWaitMillis(maxWait); // -1 表示无限等待
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxIdle(maxIdle);

        // 3. Lettuce 客户端配置
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig)
                .clientOptions(ClientOptions.builder()
                        .socketOptions(SocketOptions.builder()
                                .connectTimeout(Duration.ofMillis(connectTimeout))
                                .build())
                        .build())
                .commandTimeout(Duration.ofMillis(connectTimeout))
                .build();

        // 4. 创建连接工厂
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config, clientConfig);
        factory.afterPropertiesSet(); // 确保配置生效
        return factory;
    }


    // 配置安全的 RedisTemplate
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 1. 配置安全的 ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        // 2. 创建带类型信息的 JSON 序列化器
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        serializer.setObjectMapper(objectMapper);

        // 3. 设置序列化器
        template.setKeySerializer(new StringRedisSerializer());          // String 序列化 key
        template.setValueSerializer(serializer);                         // JSON 序列化 value
        template.setHashKeySerializer(new StringRedisSerializer());      // String 序列化 hash key
        template.setHashValueSerializer(serializer);                     // JSON 序列化 hash value

        // 4. 初始化模板
        template.afterPropertiesSet();
        return template;
    }


}