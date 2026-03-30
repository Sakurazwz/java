package com.example.demo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Pointcut 表达式练习切面
 * 每个练习都会打印日志，观察控制台输出
 */
@Slf4j
@Aspect
@Component
public class PointcutPracticeAspect {

    // ==================== 练习1：匹配特定类的方法 ====================

    /**
     * 练习1：匹配 PointcutTestController 中的所有方法
     */
    @Pointcut("execution(* com.example.demo.controller.PointcutTestController.*(..))")
    public void pointcutTestControllerMethods() {}

    @Before("pointcutTestControllerMethods()")
    public void beforeAnyMethodInTestClass() {
        log.info("✅ 练习1：匹配到了 PointcutTestController 中的方法");
    }

    // ==================== 练习2：匹配特定包的方法 ====================

    /**
     * 练习2：匹配 controller 包下所有类的所有方法
     */
    @Pointcut("execution(* com.example.demo.controller.*.*(..))")
    public void allControllerMethods() {}

    @Before("allControllerMethods()")
    public void beforeAnyControllerMethod() {
        log.info("✅ 练习2：匹配到了 controller 包下的方法");
    }

    // ==================== 练习3：匹配无参方法 ====================

    /**
     * 练习3：匹配所有不带参数的方法
     */
    @Pointcut("execution(* com.example.demo.controller.PointcutTestController.*())")
    public void noParameterMethods() {}

    @Before("noParameterMethods()")
    public void beforeNoParameterMethod() {
        log.info("✅ 练习3：匹配到了无参数方法");
    }

    // ==================== 练习4：匹配单参数方法 ====================

    /**
     * 练习4：匹配 PointcutTestController 中所有带一个 String 参数的方法
     */
    @Pointcut("execution(* com.example.demo.controller.PointcutTestController.*(String))")
    public void oneStringParameterMethods() {}

    @Before("oneStringParameterMethods()")
    public void beforeOneStringParameterMethod() {
        log.info("✅ 练习4：匹配到了单 String 参数方法");
    }

    // ==================== 练习5：匹配双参数方法 ====================

    /**
     * 练习5：匹配 PointcutTestController 中所有带两个参数的方法
     */
    @Pointcut("execution(* com.example.demo.controller.PointcutTestController.*(String, Integer))")
    public void twoParameterMethods() {}

    @Before("twoParameterMethods()")
    public void beforeTwoParameterMethod() {
        log.info("✅ 练习5：匹配到了双参数方法 (String, Integer)");
    }

    // ==================== 练习6：匹配特定返回类型 ====================

    /**
     * 练习6：匹配 PointcutTestController 中所有返回 String 的方法
     */
    @Pointcut("execution(java.lang.String com.example.demo.controller.PointcutTestController.*(..))")
    public void stringReturnMethods() {}

    @Before("stringReturnMethods()")
    public void beforeStringReturnMethod() {
        log.info("✅ 练习6：匹配到了返回 String 的方法");
    }

    // ==================== 练习7：匹配以 get 开头的方法 ====================

    /**
     * 练习7：匹配 PointcutTestController 中所有以 "get" 开头的方法
     */
    @Pointcut("execution(* com.example.demo.controller.PointcutTestController.get*(..))")
    public void getterMethods() {}

    @Before("getterMethods()")
    public void beforeGetterMethod() {
        log.info("✅ 练习7：匹配到了以 get 开头的方法");
    }

    // ==================== 练习8：匹配特定注解的方法 ====================

    /**
     * 练习8：匹配所有带 @GetMapping 注解的方法
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void getMappings() {}

    @Before("getMappings()")
    public void beforeGetMapping() {
        log.info("✅ 练习8：匹配到了 @GetMapping 注解的方法");
    }

    // ==================== 练习9：组合 Pointcut（与） ====================

    /**
     * 练习9：匹配 PointcutTestController 中带 @GetMapping 的方法
     */
    @Pointcut("pointcutTestControllerMethods() && getMappings()")
    public void testControllerGetMappings() {}

    @Before("testControllerGetMappings()")
    public void beforeTestControllerGetMapping() {
        log.info("✅ 练习9：匹配到了 PointcutTestController 中的 GET 方法");
    }

    // ==================== 练习10：组合 Pointcut（或） ====================

    /**
     * 练习10：匹配 @PostMapping 或 @PutMapping 的方法
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void postOrPutMappings() {}

    @Before("postOrPutMappings()")
    public void beforePostOrPutMapping() {
        log.info("✅ 练习10：匹配到了 POST 或 PUT 方法");
    }
}