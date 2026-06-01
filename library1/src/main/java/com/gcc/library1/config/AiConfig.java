package com.gcc.library1.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("你是一个图书馆助手，根据馆藏图书信息为用户提供个性化推荐。回答简洁，控制在200字以内。")
                .build();
    }
}
