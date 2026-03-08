package com.example.nagoyameshi.service;

import java.util.List;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.repository.CategoryRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // 検索
    public Page<Category> search(String keyword, Pageable pageable) {

        if (keyword == null || keyword.isEmpty()) {
            return categoryRepository.findAll(pageable);
        }

        return categoryRepository.findByNameContaining(keyword, pageable);
    }

    //カテゴリ全件取得（プルダウン用）
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    // カテゴリ登録・更新処理
    public void save(Category category) {
        categoryRepository.save(category);
    }

    // カテゴリ詳細取得処理
    public Category findById(Integer id) {
        return categoryRepository.findById(id).orElse(null);
    }

    // カテゴリ削除処理
    public void delete(Integer id) {
        categoryRepository.deleteById(id);
    }

}