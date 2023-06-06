package com.gmail.voronovskyi.yaroslav.chatbot.repository;

import com.gmail.voronovskyi.yaroslav.chatbot.model.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAdvertisementRepository extends JpaRepository<Advertisement, Long> {
}
