package com.example.nagoyameshi.controller;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.service.UserService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users") // ★追加
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    // 会員一覧
    @GetMapping
    public String index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            Model model) {

        Pageable pageable = PageRequest.of(page, 10);

        Page<User> userPage =
                userService.search(keyword, pageable);

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("userPage", userPage);

        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);

        return "admin/users/index";
    }

    //会員詳細
    @GetMapping("/{id}")
    public String show(@PathVariable Integer id, Model model) {

        User user = userService.findById(id);

        model.addAttribute("user", user);

        return "admin/users/show";
    }

    //編集画面
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, Model model) {

        User user = userService.findById(id);

        model.addAttribute("user", user);

        return "admin/users/edit";
    }

    //更新処理
    @PostMapping("/{id}/update")
    public String update(
            @PathVariable Integer id,
            @Valid @ModelAttribute User user,
            BindingResult result) {

        if (result.hasErrors()) {
            return "admin/users/edit";
        }

        user.setId(id);

        userService.save(user);

        return "redirect:/admin/users";
    }

}