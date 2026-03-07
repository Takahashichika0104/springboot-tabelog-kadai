package com.example.nagoyameshi.repository;

import com.example.nagoyameshi.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Integer> {

  // 店舗名で部分一致検索
  Page<Store> findByNameContaining(String keyword, Pageable pageable);
}