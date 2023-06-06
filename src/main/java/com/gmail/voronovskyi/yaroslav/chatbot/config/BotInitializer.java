package com.gmail.voronovskyi.yaroslav.chatbot.config;

import com.gmail.voronovskyi.yaroslav.chatbot.Constants;
import com.gmail.voronovskyi.yaroslav.chatbot.service.TelegramBotService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Data
@Slf4j
@Component
public class BotInitializer {

    @Autowired
    private TelegramBotService bot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException exception) {
            log.error(Constants.ERROR_MESSAGE, exception.getMessage());
        }
    }
}
