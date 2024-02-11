package com.example.nagoyameshi.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Role;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.RoleRepository;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.service.ReviewService;

@Controller
public class HomeController {
	
	private final RestaurantRepository restaurantRepository;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final ReviewService reviewService;
	
	public HomeController(RestaurantRepository restaurantRepository, UserRepository userRepository, RoleRepository roleRepository, ReviewService reviewService) {
		this.restaurantRepository = restaurantRepository;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.reviewService = reviewService;
	}
	
     @GetMapping("/")
     public String index(Model model) {
    	 List<Restaurant> popularRestaurants = restaurantRepository.findTop10ByOrderByCreatedAtDesc();
    	 model.addAttribute("popularRestaurants", popularRestaurants);
    	 
    	 Role admin = roleRepository.findByName("ROLE_ADMIN");
    	 Role general = roleRepository.findByName("ROLE_GENERAL");
    	 Role subscriber = roleRepository.findByName("ROLE_SUBSCRIBERS");
    	 List<User> userTotal = userRepository.findAll();
    	 List<User> admins = userRepository.findByRole(admin);
    	 List<User> generals = userRepository.findByRole(general);
    	 List<User> subscribers = userRepository.findByRole(subscriber);
    	 model.addAttribute("userTotal", userTotal.size() - admins.size());
     	 model.addAttribute("generalCount", generals.size());
     	 model.addAttribute("subscriberCount", subscribers.size());
     	 
     	 List<Optional<Restaurant>> topReviewRestaurants = reviewService.sortedReview();
     	 model.addAttribute("topReviewRestaurants", topReviewRestaurants);
     	 
     	model.addAttribute("eachReview", reviewService.eachReview());
    	 
         return "index";
     }   
}

