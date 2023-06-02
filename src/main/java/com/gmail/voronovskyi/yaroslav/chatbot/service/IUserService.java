package com.gmail.voronovskyi.yaroslav.chatbot.service;

import com.gmail.voronovskyi.yaroslav.chatbot.model.User;
import org.springframework.stereotype.Repository;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Repository
public interface IUserService {

    void registerUser(Message message);
    User getByChatId(Message message);
    List<User> getAll();
}
