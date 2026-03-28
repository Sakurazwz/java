package com.example.profiledemo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Environment environment;

    @Test
    void shouldReturnCurrentEnvironmentInfo() throws Exception {
        String[] activeProfiles = environment.getActiveProfiles();
        String currentProfile = activeProfiles.length > 0
                ? activeProfiles[0]
                : environment.getProperty("spring.profiles.active", "default");

        mockMvc.perform(get("/info"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.environment").value(currentProfile))
                .andExpect(jsonPath("$.port").value(environment.getProperty("server.port")))
                .andExpect(jsonPath("$.applicationName").value(environment.getProperty("spring.application.name")));
    }

    @Test
    void shouldSwitchProfileAndRequireRestart() throws Exception {
        mockMvc.perform(get("/switch/test"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.targetProfile").value("test"))
                .andExpect(jsonPath("$.restartRequired").value(true))
                .andExpect(jsonPath("$.message").value("请重启应用并使用 --spring.profiles.active=test 以生效"));
    }
}
