package com.example.nagoyameshi.controller;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Store;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.security.CustomUserDetails;
import com.example.nagoyameshi.service.FavoriteService;
import com.example.nagoyameshi.service.StoreService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {

  private final FavoriteService favoriteService;
  private final StoreService storeService;

  public FavoriteController(
      FavoriteService favoriteService,
      StoreService storeService) {

    this.favoriteService = favoriteService;
    this.storeService = storeService;
  }

  // トグル処理（登録 or 削除）
  @PostMapping("/toggle/{storeId}")
  public String toggle(
      @PathVariable Integer storeId,
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestHeader(value = "Referer", required = false) String referer) {

    User user = userDetails.getUser();
    Store store = storeService.findById(storeId);

    favoriteService.toggle(user, store);

    // 元の画面へ戻る
    return "redirect:" + referer;
  }

  // お気に入り一覧
  @GetMapping
  public String index(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      Pageable pageable,
      Model model) {

    User user = userDetails.getUser();

    Page<Favorite> favoritePage = favoriteService.findByUser(user, pageable);

    model.addAttribute("favoritePage", favoritePage);

    return "favorites/index";
  }
}