package com.example.nagoyameshi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReviewRegisterForm;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.ReviewService;

@Controller
public class ReviewController {
	
	private final ReviewRepository reviewRepository;
	private final RestaurantRepository restaurantRepository;
	private final ReviewService reviewService;
	
	public ReviewController(ReviewRepository reviewRepository, RestaurantRepository restaurantRepository, ReviewService reviewService) {
		this.reviewRepository =reviewRepository;
		this.restaurantRepository = restaurantRepository;
		this.reviewService = reviewService;
	}
	
	@GetMapping("/restaurants/{id}/reviews")
	public String index(@PageableDefault(page = 0, size = 5, sort = "id", direction = Direction.ASC) Pageable pageable, @PathVariable(name = "id") Integer id, Model model) {
		Restaurant restaurant = restaurantRepository.getReferenceById(id);
		
		Page<Review> reviewPage = reviewRepository.findByRestaurantOrderByCreatedAtDesc(restaurant, pageable);
		
		model.addAttribute("restaurant", restaurant);
		model.addAttribute("reviewPage", reviewPage);
		
		return "reviews/index";
	}
	
	@GetMapping("/restaurants/{id}/reviews/register")
	public String register(@PathVariable(name = "id") Integer id, Model model) {
		Restaurant restaurant = restaurantRepository.getReferenceById(id);
		
		model.addAttribute("restaurant", restaurant);
		model.addAttribute("reviewRegisterForm", new ReviewRegisterForm());
		return "reviews/register";
	}
	
	@PostMapping("/restaurants/{id}/reviews/create")
	public String create(@PathVariable(name = "id") Integer id,
                         @ModelAttribute @Validated ReviewRegisterForm reviewRegisterForm,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                         RedirectAttributes redirectAttributes,
                         Model model) {
		
		Restaurant restaurant = restaurantRepository.getReferenceById(id);
		User user = userDetailsImpl.getUser();
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("restaurant", restaurant);
			return "reviews/register";
		}
		
		reviewService.create(restaurant, user, reviewRegisterForm);
		redirectAttributes.addFlashAttribute("successMessage", "レビューを投稿しました。");
		
		return "redirect:/restaurants/{id}/reviews";
	}
	

}
