package com.example.demo;

import com.example.demo.annotation.Log;
import com.example.demo.controller.UserController;
import com.example.demo.dto.UserRegisterDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoggingAndCorsConfigTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAddLogAnnotationsToThreeUserControllerMethods() throws Exception {
        assertLogAnnotation(UserController.class.getMethod("getUser", Long.class), "查询单个用户", "用户管理");
        assertLogAnnotation(UserController.class.getMethod("getUserList"), "查询用户列表", "用户管理");
        assertLogAnnotation(
                UserController.class.getMethod("register", UserRegisterDTO.class),
                "用户注册",
                "用户管理"
        );
    }

    @Test
    void shouldAllowCorsForLocalhost5173() throws Exception {
        mockMvc.perform(options("/user/list")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    void shouldRejectCorsForOtherOrigin() throws Exception {
        mockMvc.perform(options("/user/list")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());
    }

    private void assertLogAnnotation(Method method, String expectedValue, String expectedModule) {
        assertTrue(method.isAnnotationPresent(Log.class));
        Log log = method.getAnnotation(Log.class);
        assertEquals(expectedValue, log.value());
        assertEquals(expectedModule, log.module());
    }
}
