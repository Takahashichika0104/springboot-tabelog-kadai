package com.example.nagoyameshi.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.entity.EmailVerificationToken;
import com.example.nagoyameshi.repository.EmailVerificationTokenRepository;

@Service
public class EmailVerificationTokenService {
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    public EmailVerificationTokenService(EmailVerificationTokenRepository emailVerificationTokenRepository) {
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
    }

    @Transactional
    public void create(User user, String token) {
        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();

        emailVerificationToken.setUser(user);
        emailVerificationToken.setToken(token);

        emailVerificationToken.setExpiresAt(LocalDateTime.now().plusHours(24));

        emailVerificationTokenRepository.save(emailVerificationToken);
    }

    // トークンの文字列で検索した結果を返す
    public EmailVerificationToken getVerificationToken(String token) {
        return emailVerificationTokenRepository.findByToken(token);
    }
}