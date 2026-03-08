package com.example.nagoyameshi.controller;

import com.example.nagoyameshi.entity.Store;
import com.example.nagoyameshi.service.CategoryService;
import com.example.nagoyameshi.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/stores")
public class AdminStoreController {

    private final StoreService storeService;
    private final CategoryService categoryService;

    public AdminStoreController(
            StoreService storeService,
            CategoryService categoryService) {

        this.storeService = storeService;
        this.categoryService = categoryService;
    }

    // 店舗一覧（検索＋ページネーション＋並び替え）
    @GetMapping
    public String index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword, // ★追加
            @RequestParam(defaultValue = "id") String sort, // ★追加
            Model model) {

        // 並び替えをする
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sort));

        // 検索してページングされた店舗リストを取得
        Page<Store> storePage = storeService.search(keyword, pageable);

        model.addAttribute("stores", storePage.getContent());
        model.addAttribute("storePage", storePage);

        // 検索キーワードとソート順をビューに渡す
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", page);

        return "admin/stores/index";
    }

    // 店舗詳細ページ
    @GetMapping("/{id}")
    public String show(@PathVariable Integer id, Model model) {

        Store store = storeService.findById(id);

        model.addAttribute("store", store);

        return "admin/stores/show";
    }

    // 店舗登録画面
    @GetMapping("/create")
    public String create(Model model) {

        model.addAttribute("store", new Store());
        model.addAttribute("categories", categoryService.findAll());

        return "admin/stores/create";
    }

    // 店舗登録処理
    @PostMapping
    public String store(
            @Valid @ModelAttribute Store store,
            BindingResult result) {

        if (result.hasErrors()) {
            return "admin/stores/create";
        }

        storeService.save(store);

        return "redirect:/admin/stores";
    }

    // 編集画面
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, Model model) {

        Store store = storeService.findById(id);

        model.addAttribute("store", store);
        model.addAttribute("categories", categoryService.findAll());

        return "admin/stores/edit";
    }

    // 更新処理
    @PostMapping("/{id}/update")
    public String update(
            @PathVariable Integer id,
            @Valid @ModelAttribute Store store,
            BindingResult result) {

        if (result.hasErrors()) {
            return "admin/stores/edit";
        }

        store.setId(id);

        storeService.save(store);

        return "redirect:/admin/stores";
    }

    // 削除処理
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {

        storeService.delete(id);

        return "redirect:/admin/stores";
    }

}