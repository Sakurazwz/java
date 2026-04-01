package com.example.demo;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class LoggingAndCorsHomeworkTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ListAppender<ILoggingEvent> logAspectAppender;
    private ListAppender<ILoggingEvent> demoAspectAppender;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        Logger logAspectLogger = (Logger) LoggerFactory.getLogger("com.example.demo.aspect.LogAspect");
        logAspectAppender = new ListAppender<>();
        logAspectAppender.start();
        logAspectLogger.addAppender(logAspectAppender);

        Logger demoAspectLogger = (Logger) LoggerFactory.getLogger("com.example.demo.aspect.DemoAspect");
        demoAspectAppender = new ListAppender<>();
        demoAspectAppender.start();
        demoAspectLogger.addAppender(demoAspectAppender);
    }

    @AfterEach
    void tearDown() {
        Logger logAspectLogger = (Logger) LoggerFactory.getLogger("com.example.demo.aspect.LogAspect");
        if (logAspectAppender != null) {
            logAspectLogger.detachAppender(logAspectAppender);
            logAspectAppender.stop();
        }

        Logger demoAspectLogger = (Logger) LoggerFactory.getLogger("com.example.demo.aspect.DemoAspect");
        if (demoAspectAppender != null) {
            demoAspectLogger.detachAppender(demoAspectAppender);
            demoAspectAppender.stop();
        }
    }

    @Test
    void shouldLogRequestIpParamsAndCostForAnnotatedUserControllerMethods() throws Exception {
        Map<String, Object> body = Map.of(
                "username", "new_user",
                "password", "Abc12345!",
                "confirmPassword", "Abc12345!",
                "email", "new_user@example.com",
                "phone", "13800138000",
                "age", 21
        );

        mockMvc.perform(get("/user/get")
                        .header("X-Real-IP", "10.20.30.40"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/user/list")
                        .header("X-Real-IP", "10.20.30.41"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Real-IP", "10.20.30.42")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        List<String> messages = logAspectAppender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .collect(Collectors.toList());

        List<String> operationLogs = messages.stream()
                .filter(msg -> msg.contains("操作日志:"))
                .collect(Collectors.toList());
        assertThat(operationLogs).hasSize(3);

        assertThat(operationLogs.stream().filter(msg -> msg.contains("description=查询单个用户")).count()).isEqualTo(1);
        assertThat(operationLogs.stream().filter(msg -> msg.contains("description=查询用户列表")).count()).isEqualTo(1);
        assertThat(operationLogs.stream().filter(msg -> msg.contains("description=用户注册")).count()).isEqualTo(1);
        assertThat(operationLogs).allMatch(msg -> msg.contains("ip=10.20.30.4"));
        assertThat(operationLogs).allMatch(msg -> msg.contains("params="));
        assertThat(operationLogs).allMatch(msg -> msg.contains("time="));
    }

    @Test
    void shouldApplyGlobalCorsForAllowedOriginAndRejectOtherOrigins() throws Exception {
        mockMvc.perform(options("/user/get")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));

        mockMvc.perform(options("/user/get")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldTriggerBeforeAfterAfterReturningAndAfterThrowingAdvice() throws Exception {
        mockMvc.perform(get("/user/get"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/user/exception"))
                .andExpect(status().isInternalServerError());

        List<String> messages = demoAspectAppender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .collect(Collectors.toList());

        assertThat(messages).anyMatch(msg -> msg.contains("【前置通知】"));
        assertThat(messages).anyMatch(msg -> msg.contains("【后置通知】"));
        assertThat(messages).anyMatch(msg -> msg.contains("【返回通知】"));
        assertThat(messages).anyMatch(msg -> msg.contains("【异常通知】"));
    }
}
