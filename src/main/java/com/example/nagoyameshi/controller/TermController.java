package com.example.nagoyameshi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TermController {
	
	@GetMapping("/term")
	public String index() {
		return "term/index";
	}

}