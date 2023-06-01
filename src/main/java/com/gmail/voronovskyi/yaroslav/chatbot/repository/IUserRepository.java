package com.gmail.voronovskyi.yaroslav.chatbot.repository;

import com.gmail.voronovskyi.yaroslav.chatbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<User, Long> {
}
