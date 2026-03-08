package com.example.nagoyameshi.repository;

import com.example.nagoyameshi.entity.Category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

  // カテゴリ名で部分一致検索
  Page<Category> findByNameContaining(String keyword, Pageable pageable);

}