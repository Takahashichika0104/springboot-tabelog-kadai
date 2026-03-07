package com.example.nagoyameshi.controller;

import com.example.nagoyameshi.service.StoreService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminStoreController {

    private final StoreService storeService;

    public AdminStoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/admin/stores")
    public String index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword, // ★追加
            @RequestParam(defaultValue = "id") String sort,  // ★追加
            Model model
    ) {

        // 並び替えをする
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sort));

        // 検索してページングされた店舗リストを取得
        Page storePage = storeService.search(keyword, pageable);

        model.addAttribute("stores", storePage.getContent());
        model.addAttribute("storePage", storePage);

        // 検索キーワードとソート順をビューに渡す
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", page);

        return "admin/stores/index";
    }
}