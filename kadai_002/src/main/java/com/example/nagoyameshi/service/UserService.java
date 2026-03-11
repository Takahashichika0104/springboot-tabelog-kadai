package com.example.nagoyameshi.service;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ユーザー検索
    public Page<User> search(String keyword, Pageable pageable) {

        if (keyword == null || keyword.isEmpty()) {
            return userRepository.findAll(pageable);
        }

        return userRepository.findByEmailContaining(keyword, pageable);
    }

    // ユーザーIDで検索
    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    // ユーザー保存
    public void save(User user) {
        userRepository.save(user);
    }

    // 会員登録
    public void register(User user) {

        userRepository.save(user);

    }

}