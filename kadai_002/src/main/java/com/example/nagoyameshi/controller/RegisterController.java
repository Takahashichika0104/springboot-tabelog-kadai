package com.example.nagoyameshi.controller;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.service.UserService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class RegisterController {

  private final UserService userService;

  public RegisterController(UserService userService) {
    this.userService = userService;
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
      Model model) {

    log.info("Registration request received for email: {}", user.getEmail());

    if (result.hasErrors()) {
      log.warn("Validation errors detected:");
      result.getFieldErrors().forEach(error -> 
        log.warn("Field: {}, Message: {}", error.getField(), error.getDefaultMessage())
      );
      return "auth/register";
    }

    user.setRole("USER");
    user.setMembershipType("FREE");

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

    return "redirect:/login";
  }

}