package com.example.nagoyameshi.controller;

import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.Store;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.security.CustomUserDetails;
import com.example.nagoyameshi.service.ReviewService;
import com.example.nagoyameshi.service.StoreService;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final StoreService storeService;

    public ReviewController(
            ReviewService reviewService,
            StoreService storeService) {

        this.reviewService = reviewService;
        this.storeService = storeService;
    }

    // 投稿画面表示
    @GetMapping("/create/{storeId}")
    public String create(
            @PathVariable Integer storeId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        User user = userDetails.getUser();

        // PREMIUM制御
        if (!"PREMIUM".equals(user.getMembershipType())) {
            return "redirect:/membership";
        }

        Store store = storeService.findById(storeId);

        Review review = new Review();
        review.setStore(store);
        review.setUser(user);

        model.addAttribute("review", review);
        model.addAttribute("store", store);

        return "reviews/create";
    }

    //投稿処理
    @PostMapping("/create")
    public String store(
            @ModelAttribute @Valid Review review,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        User user = userDetails.getUser();

        // PREMIUM制御
        if (!"PREMIUM".equals(user.getMembershipType())) {
            return "redirect:/membership";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("store", review.getStore());
            return "reviews/create";
        }

        review.setUser(user);

        reviewService.save(review);

        return "redirect:/stores/" + review.getStore().getId();
    }
}