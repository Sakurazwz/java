package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiDocumentationIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldExposeOpenApiSpec() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.paths['/user/get']").exists())
                .andExpect(jsonPath("$.paths['/order/{id}']").exists());
    }

    @Test
    void shouldValidateRegisterRequestBody() throws Exception {
        String invalidJson = """
                {
                  "username": "ab",
                  "password": "123",
                  "confirmPassword": "",
                  "email": "invalid-email"
                }
                """;

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void shouldAcceptGenderEnumInRegisterRequest() throws Exception {
        String validJson = """
                {
                  "username": "testuser",
                  "password": "Password123",
                  "confirmPassword": "Password123",
                  "email": "test@example.com",
                  "age": 25,
                  "gender": "MALE"
                }
                """;

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void shouldRejectInvalidGenderEnumValue() throws Exception {
        String invalidJson = """
                {
                  "username": "testuser",
                  "password": "Password123",
                  "confirmPassword": "Password123",
                  "email": "test@example.com",
                  "age": 25,
                  "gender": "INVALID"
                }
                """;

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void shouldIncludeGenderEnumAllowableValuesInOpenApiDocs() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.schemas.UserRegisterDTO.properties.gender.enum").isArray())
                .andExpect(jsonPath("$.components.schemas.UserRegisterDTO.properties.gender.enum[0]").value("MALE"))
                .andExpect(jsonPath("$.components.schemas.UserRegisterDTO.properties.gender.enum[1]").value("FEMALE"))
                .andExpect(jsonPath("$.components.schemas.UserRegisterDTO.properties.gender.enum[2]").value("OTHER"));
    }
}
