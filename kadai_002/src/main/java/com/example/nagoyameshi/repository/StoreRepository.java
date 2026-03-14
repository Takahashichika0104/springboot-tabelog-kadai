package com.example.nagoyameshi.repository;

import java.util.List;
import com.example.nagoyameshi.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoreRepository extends JpaRepository<Store, Integer> {

  // 店舗名で部分一致検索
  Page<Store> findByNameContaining(String keyword, Pageable pageable);

  // 新着店舗
  List<Store> findTop6ByOrderByCreatedAtDesc();

  // 店舗検索（キーワード + カテゴリ + 価格帯）
  @Query("""
            SELECT s
            FROM Store s
            WHERE
            (:keyword IS NULL OR s.name LIKE %:keyword%)
            AND (:categoryId IS NULL OR s.category.id = :categoryId)
            AND (:priceMin IS NULL OR s.minPrice >= :priceMin)
            AND (:priceMax IS NULL OR s.maxPrice <= :priceMax)
            """)
  Page<Store> searchStores(
      @Param("keyword") String keyword,
      @Param("categoryId") Integer categoryId,
      @Param("priceMin") Integer priceMin,
      @Param("priceMax") Integer priceMax,
      Pageable pageable);

}