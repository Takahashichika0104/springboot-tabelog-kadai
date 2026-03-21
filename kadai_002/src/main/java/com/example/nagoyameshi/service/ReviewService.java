package com.example.nagoyameshi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.repository.ReviewRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    //投稿
    public void save(Review review) {
        reviewRepository.save(review);
    }

    //店舗レビュー取得
    public List<Review> findByStore(Integer storeId) {

        return reviewRepository.findByStoreId(storeId);

    }

}