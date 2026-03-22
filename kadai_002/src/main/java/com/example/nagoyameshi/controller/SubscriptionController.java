package com.example.nagoyameshi.controller;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.security.CustomUserDetails;
import com.example.nagoyameshi.service.SubscriptionService;
import com.example.nagoyameshi.service.UserService;
import com.stripe.exception.StripeException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
public class SubscriptionController {

  private final UserService userService;
  private final SubscriptionService subscriptionService;

  public SubscriptionController(UserService userService, SubscriptionService subscriptionService) {
    this.userService = userService;
    this.subscriptionService = subscriptionService;
  }

  // 有料会員登録ページ
  @GetMapping("/membership")
  public String newMembership(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      Model model) {

    User user = userService.findById(userDetails.getUser().getId());
    model.addAttribute("user", user);

    return "subscription/new";
  }

  // Stripe Checkoutセッション作成 → Stripeホスト画面へリダイレクト
  @PostMapping("/membership/checkout")
  public String checkout(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      HttpServletRequest request,
      RedirectAttributes redirectAttributes) {

    User user = userService.findById(userDetails.getUser().getId());

    String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
        .replacePath(null)
        .build()
        .toUriString();

    try {
      String checkoutUrl = subscriptionService.createCheckoutSession(user, baseUrl);
      return "redirect:" + checkoutUrl;
    } catch (StripeException e) {
      redirectAttributes.addFlashAttribute("errorMessage",
          "決済処理の開始に失敗しました: " + e.getMessage());
      return "redirect:/membership";
    }
  }

  // Stripe決済完了後のコールバック
  @GetMapping("/membership/success")
  public String success(
      @RequestParam("session_id") String sessionId,
      @AuthenticationPrincipal CustomUserDetails userDetails,
      RedirectAttributes redirectAttributes) {

    User user = userService.findById(userDetails.getUser().getId());

    try {
      subscriptionService.handleCheckoutSuccess(sessionId, user);
      redirectAttributes.addFlashAttribute("successMessage", "有料会員登録が完了しました。");
    } catch (StripeException e) {
      redirectAttributes.addFlashAttribute("errorMessage",
          "登録処理中にエラーが発生しました: " + e.getMessage());
    }

    return "redirect:/member-menu";
  }

  // Stripe決済キャンセル後のコールバック
  @GetMapping("/membership/checkout-cancel")
  public String checkoutCancel() {
    return "redirect:/membership";
  }

  // 有料会員解約ページ
  @GetMapping("/membership/cancel")
  public String cancelMembership(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam(value = "successMessage", required = false) String successMessage,
      @RequestParam(value = "errorMessage", required = false) String errorMessage,
      Model model) {

    User user = userService.findById(userDetails.getUser().getId());
    model.addAttribute("user", user);

    if (successMessage != null) {
      model.addAttribute("successMessage", successMessage);
    }
    if (errorMessage != null) {
      model.addAttribute("errorMessage", errorMessage);
    }

    return "subscription/cancel";
  }

  // 有料会員解約処理
  @PostMapping("/membership/cancel")
  public String cancelMembershipExecute(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      RedirectAttributes redirectAttributes) {

    User user = userService.findById(userDetails.getUser().getId());

    try {
      subscriptionService.cancelSubscription(user);
      redirectAttributes.addFlashAttribute("successMessage", "有料会員を解約しました。");
      return "redirect:/member-menu";
    } catch (StripeException | IllegalStateException e) {
      redirectAttributes.addFlashAttribute("errorMessage", "解約処理に失敗しました: " + e.getMessage());
      return "redirect:/membership/cancel";
    }
  }

  // クレジットカード情報編集ページ
  @GetMapping("/credit-card/edit")
  public String editCreditCard(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam(value = "successMessage", required = false) String successMessage,
      @RequestParam(value = "errorMessage", required = false) String errorMessage,
      Model model) {

    User user = userService.findById(userDetails.getUser().getId());
    model.addAttribute("user", user);

    if (successMessage != null) {
      model.addAttribute("successMessage", successMessage);
    }
    if (errorMessage != null) {
      model.addAttribute("errorMessage", errorMessage);
    }

    return "subscription/credit-card-edit";
  }

  // Stripe Billing Portalへ遷移してカード情報を編集
  @PostMapping("/credit-card/edit/portal")
  public String openBillingPortal(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      HttpServletRequest request,
      RedirectAttributes redirectAttributes) {

    User user = userService.findById(userDetails.getUser().getId());

    String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
        .replacePath(null)
        .build()
        .toUriString();

    try {
      String portalUrl = subscriptionService.createBillingPortalSession(user, baseUrl);
      return "redirect:" + portalUrl;
    } catch (StripeException | IllegalStateException e) {
      redirectAttributes.addFlashAttribute("errorMessage", "カード編集画面への遷移に失敗しました: " + e.getMessage());
      return "redirect:/credit-card/edit";
    }
  }

  // Billing Portalから戻った後、カード情報をDBへ同期
  @GetMapping("/credit-card/edit/refresh")
  public String refreshCreditCard(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      RedirectAttributes redirectAttributes) {

    User user = userService.findById(userDetails.getUser().getId());

    try {
      subscriptionService.syncLatestCard(user);
      redirectAttributes.addFlashAttribute("successMessage", "クレジットカード情報を更新しました。");
    } catch (StripeException | IllegalStateException e) {
      redirectAttributes.addFlashAttribute("errorMessage", "クレジットカード情報の更新に失敗しました: " + e.getMessage());
    }

    return "redirect:/credit-card/edit";
  }
}

