package com.example.nagoyameshi.service;

import com.example.nagoyameshi.entity.Store;
import com.example.nagoyameshi.repository.StoreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

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

  // 店舗検索（キーワード + カテゴリ + 価格帯）
  public Page<Store> search(
      String keyword,
      Integer categoryId,
      Integer priceMin,
      Integer priceMax,
      Pageable pageable) {

    return storeRepository.searchStores(
        keyword,
        categoryId,
        priceMin,
        priceMax,
        pageable);

  }

  // 店舗登録・更新処理
  public void save(Store store) {
    storeRepository.save(store);
  }

  // 店舗詳細取得処理
  public Store findById(Integer id) {
    return storeRepository.findById(id).orElse(null);
  }

  // 店舗削除処理
  public void delete(Integer id) {
    storeRepository.deleteById(id);
  }

  // 新着店舗取得処理
  public List<Store> findLatestStores() {

    return storeRepository.findTop6ByOrderByCreatedAtDesc();
  }
}