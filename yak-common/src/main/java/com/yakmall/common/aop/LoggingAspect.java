package com.yakmall.common.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;


@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    // 慢方法阈值（毫秒）
    private static final long SLOW_METHOD_THRESHOLD = 1000;

    // 需要过滤的敏感参数名（不区分大小写）
    private static final Set<String> SENSITIVE_PARAM_NAMES = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("password", "token", "secret", "creditcard"))
    );


    // JSON序列化屏蔽正则
    private static final Pattern SENSITIVE_VALUE_PATTERN =
            Pattern.compile("(\"(password|token|secret|creditcard)\"\\s*:\\s*)\"(.*?)\"", Pattern.CASE_INSENSITIVE);
    @Around("execution(* com.yakmall..service..*(..)) || execution(* com.yakmall..service.impl..*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();


        //1.入参日志记录
        if(logger.isDebugEnabled()){
            try {
                String sanitizedArgs = sanitizeSensitiveData(convertArgsToJson(args));
                logger.debug(">>> {} \n 参数： {}", methodName, sanitizedArgs);
            } catch (JsonProcessingException e) {
                logger.debug(">>> {} \n参数序列化失败：{}", methodName, Arrays.toString(args));
            }
        } else if (logger.isInfoEnabled()) {
            logger.info("Service方法调用开始: {}", methodName);
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
           logger.error("!!! 方法执行异常: {}\n异常类型: {}\n异常信息: {}",
                   methodName, e.getClass().getSimpleName(), e.getMessage(), e);
           throw e;
        } finally {
            stopWatch.stop();
            long duration = stopWatch.getTotalTimeMillis();
            
            if (logger.isDebugEnabled() && result != null) {
                try {
                    String sanitizedResult = sanitizeSensitiveData(objectMapper.writeValueAsString(result));
                    logger.debug("<<< {}\n返回结果: {}\n耗时: {}ms", methodName, sanitizedResult, duration);
                } catch (JsonProcessingException e) {
                    logger.debug("<<< {}\\n返回结果序列化失败: {}\\n耗时: {}ms", methodName, result, duration);
                }
            } else {
                logger.info("Service方法调用结束: {}, 耗时: {}ms", methodName, duration);
            }

            // 5. 慢方法告警
            if (duration > SLOW_METHOD_THRESHOLD) {
                logger.warn("⚠️ 慢方法告警: {} 耗时 {}ms (超过阈值{}ms)", methodName, duration, SLOW_METHOD_THRESHOLD);
            }


        }
    }

    // 转换参数为JSON格式
    private String convertArgsToJson(Object[] args) throws JsonProcessingException {
        if (args == null || args.length == 0) return "无参数";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(String.format("参数%d: ", i + 1));
            sb.append(objectMapper.writeValueAsString(args[i]));
            if (i < args.length - 1) sb.append("\n");
        }
        return sb.toString();
    }

    // 敏感信息过滤（使用正则替换）
    private String sanitizeSensitiveData(String input) {
        return SENSITIVE_VALUE_PATTERN.matcher(input).replaceAll("$1\"******\"");
    }
}
