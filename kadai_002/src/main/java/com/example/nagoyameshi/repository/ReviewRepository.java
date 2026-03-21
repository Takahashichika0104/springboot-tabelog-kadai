package com.example.nagoyameshi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    //店舗レビュー取得
    List<Review> findByStoreId(Integer storeId);

    Optional<Review> findById(Integer id);
}