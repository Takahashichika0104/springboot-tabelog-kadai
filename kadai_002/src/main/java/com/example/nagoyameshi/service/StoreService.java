package com.example.nagoyameshi.service;

import com.example.nagoyameshi.entity.Store;
import com.example.nagoyameshi.repository.StoreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class StoreService {

  private final StoreRepository storeRepository;

  public StoreService(StoreRepository storeRepository) {
    this.storeRepository = storeRepository;
  }

  public Page<Store> search(String keyword, Pageable pageable) {

    // キーワードなしなら全件
    if (keyword == null || keyword.isEmpty()) {
      return storeRepository.findAll(pageable);
    }

    // 検索
    return storeRepository.findByNameContaining(keyword, pageable);
  }
}