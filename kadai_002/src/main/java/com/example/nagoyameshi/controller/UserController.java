package com.example.nagoyameshi.controller;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.event.SignupEventPublisher;
import com.example.nagoyameshi.security.CustomUserDetails;
import com.example.nagoyameshi.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class UserController {

  private final UserService userService;
  private final SignupEventPublisher signupEventPublisher;

  public UserController(UserService userService, SignupEventPublisher signupEventPublisher) {
    this.userService = userService;
    this.signupEventPublisher = signupEventPublisher;
  }

  // 会員メニューページ
  @GetMapping("/member-menu")
  public String memberMenu(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      Model model) {

    User user = userService.findById(userDetails.getUser().getId());

    model.addAttribute("user", user);

    return "users/menu";
  }

  // 会員情報確認ページ

  @GetMapping("/mypage")
  public String mypage(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      Model model) {

    User user = userService.findById(userDetails.getUser().getId());

    model.addAttribute("user", user);

    return "users/show";
  }

  // 会員情報編集ページ表示
  @GetMapping("/users/edit")
  public String edit(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      Model model) {

    User user = userService.findById(userDetails.getUser().getId());

    model.addAttribute("user", user);

    return "users/edit";
  }

  // 会員情報更新処理
  @PostMapping("/users/update")
  public String update(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid User user,
      BindingResult result,
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse,
      Model model) {

    log.info("User update request received for email: {}", user.getEmail());

    if (result.hasErrors()) {
      log.warn("Validation errors detected:");
      result.getFieldErrors()
          .forEach(error -> log.warn("Field: {}, Message: {}", error.getField(), error.getDefaultMessage()));
      return "users/edit";
    }

    try {
      User currentUser = userDetails.getUser();
      log.info("Current email: {}, New email: {}", currentUser.getEmail(), user.getEmail());

      // メールアドレスが変更されている場合、再度メール認証が必要
      boolean emailChanged = !currentUser.getEmail().equals(user.getEmail());

      User updatedUser = userService.update(currentUser.getId(), user, emailChanged);
      log.info("User update completed successfully");

      // メールアドレスが変更された場合、認証メールを再送
      if (emailChanged) {
        String requestUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/register")
            .toUriString();
        signupEventPublisher.publishSignupEvent(updatedUser, requestUrl);
        new SecurityContextLogoutHandler().logout(
          httpServletRequest,
          httpServletResponse,
          SecurityContextHolder.getContext().getAuthentication());
        model.addAttribute("successMessage",
            "会員情報を更新しました。メールアドレスが変更されたため、再度メール認証が必要です。ご入力いただいたメールアドレスに認証メールを送信しました。");
        return "redirect:/login";
      }

      model.addAttribute("successMessage", "会員情報を更新しました。");
      return "redirect:/mypage";

    } catch (Exception e) {
      log.error("Error during user update: ", e);
      model.addAttribute("errorMessage", "更新処理中にエラーが発生しました: " + e.getMessage());
      return "users/edit";
    }
  }
}
