package com.example.demo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * 演示切面（各种通知类型）
 */
@Slf4j
@Aspect
@Component
public class DemoAspect {

    /**
     * 定义切入点
     */
    @Pointcut("execution(* com.example.demo.controller.UserController.*(..))")
    public void controllerPointcut() {}

    /**
     * 前置通知：在目标方法执行前执行
     */
    @Before("controllerPointcut()")
    public void before(JoinPoint joinPoint) {
        log.info("【前置通知】方法准备执行: {}", joinPoint.getSignature());
    }

    /**
     * 后置通知：在目标方法执行后执行（无论成功还是失败）
     */
    @After("controllerPointcut()")
    public void after(JoinPoint joinPoint) {
        log.info("【后置通知】方法执行完毕: {}", joinPoint.getSignature());
    }

    /**
     * 返回通知：在目标方法成功返回后执行
     */
    @AfterReturning(pointcut = "controllerPointcut()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        log.info("【返回通知】方法成功返回: {}, 返回值: {}", joinPoint.getSignature(), result);
    }

    /**
     * 异常通知：在目标方法抛出异常后执行
     */
    @AfterThrowing(pointcut = "controllerPointcut()", throwing = "exception")
    public void afterThrowing(JoinPoint joinPoint, Exception exception) {
        log.error("【异常通知】方法抛出异常: {}, 异常: {}", joinPoint.getSignature(), exception.getMessage());
    }

    /**
     * 环绕通知：在目标方法执行前后都执行（最强大的通知类型）
     *
     * 特点：
     * 1. 可以控制目标方法是否执行
     * 2. 可以修改目标方法的参数
     * 3. 可以修改目标方法的返回值
     * 4. 可以捕获/处理异常
     * 5. 可以统计方法执行时间
     */
    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();

        // 前置逻辑
        log.info("【环绕通知-前置】方法准备执行: {}", methodName);
        long startTime = System.currentTimeMillis();

        Object result = null;
        try {
            // 执行目标方法（必须调用，否则目标方法不会执行）
            result = joinPoint.proceed();

            // 后置逻辑（方法成功返回）
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            log.info("【环绕通知-后置】方法执行成功: {}, 耗时: {}ms", methodName, executionTime);

            return result;
        } catch (Exception e) {
            // 异常逻辑（方法抛出异常）
            log.error("【环绕通知-异常】方法执行失败: {}, 异常: {}", methodName, e.getMessage());
            throw e;  // 可以选择抛出或处理异常
        } finally {
            // 最终逻辑（无论成功还是失败都会执行）
            log.info("【环绕通知-最终】方法执行结束: {}", methodName);
        }
    }
}
