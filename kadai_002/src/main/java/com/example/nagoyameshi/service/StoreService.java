package com.example.nagoyameshi.service;

import com.example.nagoyameshi.entity.Store;
import com.example.nagoyameshi.repository.StoreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

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
    MultipartFile imageFile = store.getImage();

    if (imageFile != null && !imageFile.isEmpty()) {
      String imageName = imageFile.getOriginalFilename();
      String hashedImageName = generateNewFileName(imageName);
      Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
      copyImageFile(imageFile, filePath);
      store.setImagePath(hashedImageName);
    }

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

  // UUIDを使って生成したファイル名を返す
  public String generateNewFileName(String fileName) {
    String[] fileNames = fileName.split("\\.");
    for (int i = 0; i < fileNames.length - 1; i++) {
      fileNames[i] = UUID.randomUUID().toString();
    }
    String hashedFileName = String.join(".", fileNames);
    return hashedFileName;
  }

  // 画像ファイルを指定したファイルにコピーする
  public void copyImageFile(MultipartFile imageFile, Path filePath) {
    try {
      Files.copy(imageFile.getInputStream(), filePath);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}