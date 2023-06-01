package com.gmail.voronovskyi.yaroslav.chatbot.service;

import com.gmail.voronovskyi.yaroslav.chatbot.model.User;
import org.springframework.stereotype.Repository;
import org.telegram.telegrambots.meta.api.objects.Message;

@Repository
public interface IUserService {

    void registerUser(Message message);
    User getByChatId(Message message);
}
