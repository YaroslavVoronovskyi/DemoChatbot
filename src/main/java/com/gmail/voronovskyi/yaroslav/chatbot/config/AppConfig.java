package com.gmail.voronovskyi.yaroslav.chatbot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Data
@EnableScheduling
@Configuration
@PropertySource("application.properties")
public class AppConfig {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.owner}")
    private Long ownerId;
}
