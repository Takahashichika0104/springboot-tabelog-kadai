package com.example.nagoyameshi.controller;

import com.example.nagoyameshi.entity.EmailVerificationToken;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.event.SignupEventPublisher;
import com.example.nagoyameshi.service.EmailVerificationTokenService;
import com.example.nagoyameshi.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class RegisterController {

  private final UserService userService;
  private final SignupEventPublisher signupEventPublisher;
  private final EmailVerificationTokenService emailVerificationTokenService;

  public RegisterController(UserService userService, SignupEventPublisher signupEventPublisher,
      EmailVerificationTokenService emailVerificationTokenService) {
    this.userService = userService;
    this.signupEventPublisher = signupEventPublisher;
    this.emailVerificationTokenService = emailVerificationTokenService;
  }

  // 登録画面
  @GetMapping("/register")
  public String register(Model model) {

    model.addAttribute("user", new User());

    return "auth/register";
  }

  // 登録処理
  @PostMapping("/register")
  public String store(
      @Valid User user,
      BindingResult result,
      HttpServletRequest httpServletRequest,
      Model model) {

    log.info("Registration request received for email: {}", user.getEmail());

    if (result.hasErrors()) {
      log.warn("Validation errors detected:");
      result.getFieldErrors()
          .forEach(error -> log.warn("Field: {}, Message: {}", error.getField(), error.getDefaultMessage()));
      return "auth/register";
    }

    user.setRole("USER");
    user.setMembershipType("FREE");
    user.setEnabled(false);

    log.info("Validation passed, setting default values");

    try {
      log.info("Calling userService.register()");
      userService.register(user);
      log.info("User registration completed successfully");
    } catch (Exception e) {
      log.error("Error during registration: ", e);
      model.addAttribute("errorMessage", "登録処理中にエラーが発生しました: " + e.getMessage());
      return "auth/register";
    }

    String requestUrl = new String(httpServletRequest.getRequestURL());
    signupEventPublisher.publishSignupEvent(user, requestUrl);
    model.addAttribute("successMessage", "ご入力いただいたメールアドレスに認証メールを送信しました。メールに記載されているリンクをクリックし、会員登録を完了してください。");
    return "redirect:/login";
  }

  @GetMapping("/register/verify")
  public String verify(@RequestParam(name = "token") String token, Model model) {
    log.info("Email verification request received with token: {}", token);
    EmailVerificationToken verificationToken = emailVerificationTokenService.getVerificationToken(token);

    if (verificationToken != null) {
      User user = verificationToken.getUser();
      userService.enableUser(user);
      String successMessage = "会員登録が完了しました。";
      model.addAttribute("successMessage", successMessage);
    } else {
      String errorMessage = "トークンが無効です。";
      model.addAttribute("errorMessage", errorMessage);
    }

    return "redirect:/login";
  }
}