package com.example.demo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * 参数校验日志切面
 * 使用 AOP 拦截全局异常处理器中的参数校验方法，记录校验失败的详细日志
 * 记录内容：请求路径、参数、错误消息、时间
 */
@Slf4j
@Aspect
@Component
public class ValidationLogAspect {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 拦截全局异常处理器中的参数校验异常（RequestBody）和参数绑定异常（RequestParam）处理方法
     */
    @Around("execution(* com.example.demo.exception.GlobalExceptionHandler.handleValidationException(..))" +
            " || execution(* com.example.demo.exception.GlobalExceptionHandler.handleBindException(..))")
    public Object logValidationFailure(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object[] args = joinPoint.getArgs();
        String requestPath = "unknown";
        String requestParams = "";
        String errorMsg = "unknown";

        for (Object arg : args) {
            if (arg instanceof HttpServletRequest request) {
                requestPath = request.getRequestURI();
                String queryString = request.getQueryString();
                requestParams = queryString != null ? queryString : "";
            }
            if (arg instanceof MethodArgumentNotValidException ex) {
                // For request body validation, include field name and rejected value for debugging
                errorMsg = ex.getBindingResult().getFieldErrors().stream()
                        .map(fe -> fe.getField() + "='" + fe.getRejectedValue() + "': " + fe.getDefaultMessage())
                        .collect(Collectors.joining("; "));
            }
            if (arg instanceof BindException ex) {
                // For query param / ModelAttribute binding, include field name and rejected value
                errorMsg = ex.getBindingResult().getFieldErrors().stream()
                        .map(fe -> fe.getField() + "='" + fe.getRejectedValue() + "': " + fe.getDefaultMessage())
                        .collect(Collectors.joining("; "));
            }
        }

        Object result = joinPoint.proceed();

        long elapsed = System.currentTimeMillis() - startTime;
        String time = LocalDateTime.now().format(FORMATTER);

        log.warn("[ValidationLog] time={}, path={}, params={}, errors={}, elapsed={}ms",
                time, requestPath, requestParams, errorMsg, elapsed);

        return result;
    }
}
