package com.example.demo.aspect;

import com.example.demo.annotation.Log;
import com.example.demo.dto.LogDTO;
import com.example.demo.utils.IpUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 操作日志切面
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 定义切入点：拦截所有带 @Log 注解的方法
     */
    @Pointcut("@annotation(com.example.demo.annotation.Log)")
    public void logPointcut() {
    }

    /**
     * 环绕通知
     */
    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取请求属性
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        // 创建日志对象
        LogDTO logDTO = new LogDTO();

        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取 @Log 注解
        Log logAnnotation = method.getAnnotation(Log.class);
        if (logAnnotation != null) {
            logDTO.setDescription(logAnnotation.value());
        }

        // 设置基本信息
        if (request != null) {
            logDTO.setIp(IpUtils.getIpAddr(request));
            logDTO.setUrl(request.getRequestURI());
            logDTO.setHttpMethod(request.getMethod());
        }

        logDTO.setMethod(signature.getDeclaringTypeName() + "." + signature.getName());
        logDTO.setParams(Arrays.toString(joinPoint.getArgs()));

        try {
            // 执行目标方法
            Object result = joinPoint.proceed();

            // 记录成功日志
            long endTime = System.currentTimeMillis();
            logDTO.setTime(endTime - startTime);
            logDTO.setSuccess(true);
            logDTO.setResult(objectMapper.writeValueAsString(result));

            log.info("操作日志: {}", logDTO);

            return result;
        } catch (Exception e) {
            // 记录失败日志
            long endTime = System.currentTimeMillis();
            logDTO.setTime(endTime - startTime);
            logDTO.setSuccess(false);
            logDTO.setErrorMsg(e.getMessage());

            log.error("操作异常: {}", logDTO, e);

            throw e;
        }
    }
}
