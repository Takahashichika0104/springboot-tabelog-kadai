package com.example.nagoyameshi.service;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Store;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.FavoriteRepository;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FavoriteService {

  private final FavoriteRepository favoriteRepository;

  public FavoriteService(FavoriteRepository favoriteRepository) {
    this.favoriteRepository = favoriteRepository;
  }

  // トグル処理（登録 or 削除）
  @Transactional
  public void toggle(User user, Store store) {

    boolean exists = favoriteRepository.existsByUserAndStore(user, store);

    if (exists) {
      favoriteRepository.deleteByUserAndStore(user, store);
    } else {
      Favorite favorite = new Favorite();
      favorite.setUser(user);
      favorite.setStore(store);
      favoriteRepository.save(favorite);
    }
  }

  // お気に入り済みか判断
  public boolean isFavorite(User user, Store store) {
    return favoriteRepository.existsByUserAndStore(user, store);
  }

  public Page<Favorite> findByUser(User user, Pageable pageable) {
    return favoriteRepository.findByUser(user, pageable);
  }
}