package com.example.nagoyameshi.controller;

import com.example.nagoyameshi.service.StoreService;
import com.example.nagoyameshi.service.CategoryService;
import com.example.nagoyameshi.service.ReviewService;
import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.Store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StoreController {

  private final StoreService storeService;
  private final CategoryService categoryService;
  private final ReviewService reviewService;

  public StoreController(
      StoreService storeService,
      CategoryService categoryService,
      ReviewService reviewService) {

    this.storeService = storeService;
    this.categoryService = categoryService;
    this.reviewService = reviewService;
  }

  // 店舗一覧
  @GetMapping("/stores")
  public String index(

      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Integer categoryId,
      @RequestParam(required = false) Integer priceMin,
      @RequestParam(required = false) Integer priceMax,

      Pageable pageable,
      Model model) {

    Page<Store> stores = storeService.search(
        keyword,
        categoryId,
        priceMin,
        priceMax,
        pageable);

    model.addAttribute("stores", stores);
    model.addAttribute("keyword", keyword);
    model.addAttribute("categoryId", categoryId);
    model.addAttribute("priceMin", priceMin);
    model.addAttribute("priceMax", priceMax);
    model.addAttribute("categories", categoryService.findAll());

    return "stores/index";
  }

  // 店舗詳細
  @GetMapping("/stores/{id}")
  public String show(
      @PathVariable Integer id,
      Model model) {

    Store store = storeService.findById(id);

    model.addAttribute("store", store);
    // ★予約オブジェクト作成
    Reservation reservation = new Reservation();
    reservation.setStore(store); // ★店舗をセット

    model.addAttribute("reservation", reservation);

    // レビュー一覧
    model.addAttribute(
        "reviews",
        reviewService.findByStore(id));

    return "stores/show";
  }

}