package com.gmail.voronovskyi.yaroslav.chatbot.service;

import com.gmail.voronovskyi.yaroslav.chatbot.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBotService extends TelegramLongPollingBot {

    private static final String HELP_TEXT = "This bot is created to demonstrate Spring capabilities. \n\n" +
            "You can execute commands from the main menu on the left or by typing command: \n\n" +
            "Type /start to see a welcome message \n\n" +
            "Type /mydata to see data stored about yourself \n\n" +
            "Type /deletedata to delete stored data about yourself \n\n" +
            "Type /help to see this message again \n\n" +
            "Type /settings to set your preference";
    private final AppConfig config;
    private final IUserService userService;

    @Autowired
    public TelegramBotService(AppConfig config, IUserService userService) {
        this.config = config;
        this.userService = userService;
        List<BotCommand> botCommandsList = new ArrayList<>();
        botCommandsList.add(new BotCommand("/start", "get a welcome message"));
        botCommandsList.add(new BotCommand("/mydata", "get your data store"));
        botCommandsList.add(new BotCommand("/deletedata", "delete my data"));
        botCommandsList.add(new BotCommand("/help", "info how to use this bot"));
        botCommandsList.add(new BotCommand("/settings", "set your preferences"));
        try {
            this.execute(new SetMyCommands(botCommandsList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException exception) {
            log.error("Error setting bot's command list: {}", exception.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String name = update.getMessage().getChat().getFirstName();

            switch (messageText) {

                case "/start":
                    userService.registerUser(update.getMessage());
                    startCommandReceived(chatId, name);
                    break;

                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;

                default:
                    sendMessage(chatId, "Sorry, command was not recognized ...");
            }
        }
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you!";
        log.info("Replied to user {}", name);
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException exception) {
//            throw new RuntimeException("Error!");
            log.error("Error occurred: {}", exception.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }
}
