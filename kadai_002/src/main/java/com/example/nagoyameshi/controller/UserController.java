package com.example.nagoyameshi.controller;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.security.CustomUserDetails;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

  // 会員情報確認ページ

  @GetMapping("/mypage")
  public String mypage(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      Model model) {

    User user = userDetails.getUser();

    model.addAttribute("user", user);

    return "users/show";
  }
}
