package com.example.nagoyameshi.repository;

import com.example.nagoyameshi.entity.User;

import org.springframework.data.domain.Page;      // ★追加
import org.springframework.data.domain.Pageable;  // ★追加
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    //メールアドレス検索
    Page<User> findByEmailContaining(String keyword, Pageable pageable);

}