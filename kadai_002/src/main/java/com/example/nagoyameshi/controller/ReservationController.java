package com.example.nagoyameshi.controller;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.Store;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.security.CustomUserDetails;
import com.example.nagoyameshi.service.ReservationService;
import com.example.nagoyameshi.service.StoreService;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

  private final ReservationService reservationService;
  private final StoreService storeService;

  public ReservationController(
      ReservationService reservationService,
      StoreService storeService) {

    this.reservationService = reservationService;
    this.storeService = storeService;
  }

  // 店舗詳細 → 予約確認画面
  @PostMapping("/confirm")
  public String confirm(
      @ModelAttribute @Valid Reservation reservation,
      BindingResult bindingResult,
      @AuthenticationPrincipal CustomUserDetails userDetails,
      Model model) {

    User user = userDetails.getUser();

    // membership_typeを確認
    if (!"PREMIUM".equals(user.getMembershipType())) {
      return "redirect:/membership";
    }

    // 予約フォームの検証
    if (bindingResult.hasErrors()) {
      return "redirect:/stores/" + reservation.getStore().getId();
    }

    Store store = storeService.findById(reservation.getStore().getId());

    reservation.setStore(store);
    reservation.setUser(user);
    reservation.setStatus("予約受付");

    model.addAttribute("reservation", reservation);

    return "reservations/confirm";
  }

  // 予約登録処理
  @PostMapping("/create")
  public String create(
      @ModelAttribute Reservation reservation,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    User user = userDetails.getUser();

    // membership_typeを確認
    if (!"PREMIUM".equals(user.getMembershipType())) {
      return "redirect:/membership";
    }

    reservation.setUser(user);

    reservationService.save(reservation);

    return "redirect:/reservations";
  }

  // 予約一覧
  @GetMapping
  public String index(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      Model model) {

    User user = userDetails.getUser();

    List<Reservation> reservations = reservationService.findByUser(user);

    model.addAttribute("reservations", reservations);

    return "reservations/index";
  }
}