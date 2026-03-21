package com.example.nagoyameshi.repository;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Store;
import com.example.nagoyameshi.entity.User;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

    Optional<Favorite> findByUserAndStore(User user, Store store);

    boolean existsByUserAndStore(User user, Store store);

    void deleteByUserAndStore(User user, Store store);

    Page<Favorite> findByUser(User user, Pageable pageable);
}