package com.gmail.voronovskyi.yaroslav.chatbot.service;

import com.gmail.voronovskyi.yaroslav.chatbot.Constants;
import com.gmail.voronovskyi.yaroslav.chatbot.config.AppConfig;
import com.gmail.voronovskyi.yaroslav.chatbot.model.Advertisement;
import com.gmail.voronovskyi.yaroslav.chatbot.model.User;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBotService extends TelegramLongPollingBot {

    private final AppConfig config;
    private final IUserService userService;
    private final IAdvertisementService advertisementService;

    @Autowired
    public TelegramBotService(AppConfig config, IUserService userService, IAdvertisementService advertisementService) {
        this.config = config;
        this.userService = userService;
        this.advertisementService = advertisementService;
        List<BotCommand> botCommandsList = new ArrayList<>();
        botCommandsList.add(new BotCommand("/start", "get a welcome message"));
        botCommandsList.add(new BotCommand("/register", "get you register"));
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

            if (messageText.contains("/send") && config.getOwnerId() == chatId) {
                var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                var users = userService.getAll();
                for (User user : users) {
                    prepareAndSendMessage(user.getChatId(), textToSend);
                }
            } else {
                switch (messageText) {

                    case "/start":
                        userService.registerUser(update.getMessage());
                        startCommandReceived(chatId, name);
                        break;

                    case "/register":
                        register(chatId);
                        break;

                    case "/help":
                        prepareAndSendMessage(chatId, Constants.HELP_TEXT);
                        break;

                    default:
                        prepareAndSendMessage(chatId, "Sorry, command was not recognized ...");
                }
            }
        } else if (update.hasCallbackQuery()) {
            String callbackQuery = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackQuery.equals(Constants.YES_BUTTON)) {
                String text = "You pressed YES button";
                executeEditMessageText(text, chatId, messageId);
            } else if (callbackQuery.equals(Constants.NO_BUTTON)) {
                String text = "You pressed NO Button";
                executeEditMessageText(text, chatId, messageId);
            }
        }
    }

    private void register(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Do you really want to register?");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLineList = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        var yesButton = new InlineKeyboardButton();
        yesButton.setText("Yes");
        yesButton.setCallbackData(Constants.YES_BUTTON);

        var noButton = new InlineKeyboardButton();
        noButton.setText("No");
        noButton.setCallbackData(Constants.NO_BUTTON);

        rowInLine.add(yesButton);
        rowInLine.add(noButton);

        rowsInLineList.add(rowInLine);
        inlineKeyboardMarkup.setKeyboard(rowsInLineList);
        message.setReplyMarkup(inlineKeyboardMarkup);

        executeMessage(message);
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = EmojiParser.parseToUnicode("Hi, " + name + ", nice to meet you! " + ":blush:");
        log.info("Replied to user {}", name);
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRowsList = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add("weather");
        keyboardRow.add("get random joke");

        keyboardRowsList.add(keyboardRow);

        keyboardRow = new KeyboardRow();
        keyboardRow.add("register");
        keyboardRow.add("check my data");
        keyboardRow.add("delete my data");

        keyboardRowsList.add(keyboardRow);

        replyKeyboardMarkup.setKeyboard(keyboardRowsList);
        message.setReplyMarkup(replyKeyboardMarkup);

        executeMessage(message);
    }

    private void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException exception) {
            log.error(Constants.ERROR_MESSAGE, exception.getMessage());
        }
    }

    private void executeEditMessageText(String text, long chatId, long messageId) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setText(text);
        editMessageText.setMessageId((int) messageId);
        try {
            execute(editMessageText);
        } catch (TelegramApiException exception) {
            log.error(Constants.ERROR_MESSAGE, exception.getMessage());
        }
    }

    @Scheduled(cron = "${cron.scheduler}")
    private void sendAdvertisements() {
        var advertisements = advertisementService.getAll();
        var users = userService.getAll();

        for (Advertisement advertisement : advertisements) {
            for (User user : users) {
                prepareAndSendMessage(user.getChatId(), advertisement.getMessage());
            }
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
