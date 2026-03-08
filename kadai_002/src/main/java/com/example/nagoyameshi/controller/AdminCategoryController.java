package com.example.nagoyameshi.controller;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.service.CategoryService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categories") // URLのベースを指定
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // カテゴリー一覧（検索＋ページネーション＋並び替え）
    @GetMapping
    public String index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "id") String sort,
            Model model) {

        Pageable pageable = PageRequest.of(page, 5, Sort.by(sort));

        Page<Category> categoryPage =
                categoryService.search(keyword, pageable);

        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("categoryPage", categoryPage);

        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", page);

        return "admin/categories/index";
    }

    // カテゴリー登録ページ
    @GetMapping("/create")
    public String create(Model model) {

        model.addAttribute("category", new Category());

        return "admin/categories/create";
    }

    // カテゴリー登録処理
    @PostMapping
    public String store(
            @Valid @ModelAttribute Category category,
            BindingResult result) {

        if (result.hasErrors()) {
            return "admin/categories/create";
        }

        categoryService.save(category);

        return "redirect:/admin/categories";
    }

    // カテゴリー編集画面
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, Model model) {

        Category category = categoryService.findById(id);

        model.addAttribute("category", category);

        return "admin/categories/edit";
    }

    // カテゴリー更新処理
    @PostMapping("/{id}/update")
    public String update(
            @PathVariable Integer id,
            @Valid @ModelAttribute Category category,
            BindingResult result) {

        if (result.hasErrors()) {
            return "admin/categories/edit";
        }

        category.setId(id);

        categoryService.save(category);

        return "redirect:/admin/categories";
    }

    //カテゴリー削除処理
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {

        categoryService.delete(id);

        return "redirect:/admin/categories";
    }

}