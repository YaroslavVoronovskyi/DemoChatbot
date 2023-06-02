package com.gmail.voronovskyi.yaroslav.chatbot.service.impl;

import com.gmail.voronovskyi.yaroslav.chatbot.model.User;
import com.gmail.voronovskyi.yaroslav.chatbot.repository.IUserRepository;
import com.gmail.voronovskyi.yaroslav.chatbot.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class UserService implements IUserService {

    private final IUserRepository userRepository;

    @Autowired
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void registerUser(Message message) {
        if (userRepository.findById(message.getChatId()).isEmpty()) {
            Long chatId = message.getChatId();
            Chat chat = message.getChat();
            User user = User.builder()
                    .chatId(chatId)
                    .firstName(chat.getFirstName())
                    .lastName(chat.getLastName())
                    .userName(chat.getUserName())
                    .registeredAt(LocalDateTime.now())
                    .build();
            userRepository.save(user);
            log.info("User saved: {}", user);
        }
    }

    @Override
    public User getByChatId(Message message) {
        return userRepository.getReferenceById(message.getChatId());
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }
}
