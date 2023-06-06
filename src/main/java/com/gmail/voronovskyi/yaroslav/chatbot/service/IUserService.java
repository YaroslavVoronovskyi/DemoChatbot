package com.gmail.voronovskyi.yaroslav.chatbot.service;

import com.gmail.voronovskyi.yaroslav.chatbot.model.User;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

public interface IUserService {

    void registerUser(Message message);
    List<User> getAll();
}
