package com.example.testingapp.service;

import com.example.testingapp.entity.User;

public interface MailService {
    void sendVerificationCode(User user);
    void sendUrl(User user);
}
