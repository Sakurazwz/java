package com.example.demo.exception;

import com.example.demo.common.Result;
import com.example.demo.common.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(com.example.demo.exception.BusinessException.class)
    public Result<?> handleBusinessException(com.example.demo.exception.BusinessException e, HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        String params = extractRequestParams(request);
        e.withRequestPath(requestPath).withRequestParams(params);
        log.warn("业务异常: [{}] {}, 请求路径: {}, 请求参数: {}", e.getCode(), e.getMessage(), requestPath, params);
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常（RequestBody）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}, 请求路径: {}, 请求参数: {}",
                errorMsg, request.getRequestURI(), extractRequestParams(request));
        return Result.error(ResultCode.BAD_REQUEST.getCode(), errorMsg);
    }

    /**
     * 参数绑定异常（RequestParam 或 ModelAttribute）
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBindException(BindException e, HttpServletRequest request) {
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数绑定失败: {}, 请求路径: {}, 请求参数: {}",
                errorMsg, request.getRequestURI(), extractRequestParams(request));
        return Result.error(ResultCode.BAD_REQUEST.getCode(), errorMsg);
    }

    /**
     * 非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("非法参数: {}, 请求路径: {}, 请求参数: {}",
                e.getMessage(), request.getRequestURI(), extractRequestParams(request));
        return Result.error(ResultCode.BAD_REQUEST.getCode(), e.getMessage());
    }

    /**
     * 算术异常（如除零错误）
     */
    @ExceptionHandler(ArithmeticException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleArithmeticException(ArithmeticException e, HttpServletRequest request) {
        log.error("算术异常: {}, 请求路径: {}, 请求参数: {}",
                e.getMessage(), request.getRequestURI(), extractRequestParams(request), e);
        return Result.error(ResultCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        log.error("空指针异常: {}, 请求路径: {}, 请求参数: {}",
                e.getMessage(), request.getRequestURI(), extractRequestParams(request), e);
        return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "系统内部错误");
    }

    /**
     * 其他未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: {}, 请求路径: {}, 请求参数: {}",
                e.getMessage(), request.getRequestURI(), extractRequestParams(request), e);
        return Result.error(ResultCode.INTERNAL_SERVER_ERROR);
    }

    private String extractRequestParams(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            return queryString;
        }

        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.isEmpty()) {
            return "{}";
        }

        return parameterMap.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + Arrays.toString(entry.getValue()))
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
