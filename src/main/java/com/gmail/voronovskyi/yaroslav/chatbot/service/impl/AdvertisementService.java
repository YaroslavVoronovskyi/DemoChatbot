package com.gmail.voronovskyi.yaroslav.chatbot.service.impl;

import com.gmail.voronovskyi.yaroslav.chatbot.model.Advertisement;
import com.gmail.voronovskyi.yaroslav.chatbot.repository.IAdvertisementRepository;
import com.gmail.voronovskyi.yaroslav.chatbot.service.IAdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdvertisementService implements IAdvertisementService {

    private final IAdvertisementRepository advertisementRepository;

    @Autowired
    public AdvertisementService(IAdvertisementRepository advertisementRepository) {
        this.advertisementRepository = advertisementRepository;
    }

    @Override
    public List<Advertisement> getAll() {
        return advertisementRepository.findAll();
    }
}
