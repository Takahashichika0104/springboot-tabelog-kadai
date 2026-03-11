package com.example.nagoyameshi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import com.example.nagoyameshi.service.StoreService;

@Controller
public class HomeController {
	private final StoreService storeService;
	
	public HomeController(StoreService storeService) {
		this.storeService = storeService;
	}
	// トップページ
	@GetMapping("/")
	public String index(Model model) {

		model.addAttribute(
				"stores",
				storeService.findLatestStores());

		return "home/index";
	}

}
