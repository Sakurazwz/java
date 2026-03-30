package com.example.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Pointcut 表达式测试控制器
 */
@Slf4j
@RestController
@RequestMapping("/pointcut-test")
@RequiredArgsConstructor
public class PointcutTestController {

    // 测试1：无参方法
    @GetMapping("/no-params")
    public String noParams() {
        log.info("执行：noParams()");
        return "无参数方法";
    }

    // 测试2：单参数方法
    @GetMapping("/one-param")
    public String oneParam(@RequestParam String name) {
        log.info("执行：oneParam({})", name);
        return "单参数方法：" + name;
    }

    // 测试3：双参数方法
    @GetMapping("/two-params")
    public String twoParams(@RequestParam String name, @RequestParam Integer age) {
        log.info("执行：twoParams({}, {})", name, age);
        return "双参数方法：" + name + ", " + age;
    }

    // 测试4：POST 方法
    @PostMapping("/post-method")
    public String postMethod(@RequestBody String data) {
        log.info("执行：postMethod({})", data);
        return "POST 方法";
    }

    // 测试5：PUT 方法
    @PutMapping("/put-method")
    public String putMethod(@RequestBody String data) {
        log.info("执行：putMethod({})", data);
        return "PUT 方法";
    }

    // 测试6：以 get 开头的方法
    @GetMapping("/get-data")
    public String getData() {
        log.info("执行：getData()");
        return "GET 开头的方法";
    }

    // 测试7：public 方法（所有都是 public，测试是否能匹配）
    @DeleteMapping("/delete-data")
    public String deleteData() {
        log.info("执行：deleteData()");
        return "DELETE 方法";
    }

    // 测试8：返回 Integer 的方法
    @GetMapping("/count")
    public Integer getCount() {
        log.info("执行：getCount()");
        return 100;
    }
}