package com.example.nagoyameshi.controller;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReservationInputForm;
import com.example.nagoyameshi.repository.FavoriteRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.ReviewService;

@Controller
@RequestMapping("/restaurants")
public class RestaurantController {
	
	private final RestaurantRepository restaurantRepository;
	private final FavoriteRepository favoriteRepository;
	private final ReviewService reviewService;
    
	public RestaurantController(RestaurantRepository restaurantRepository, FavoriteRepository favoriteRepository, ReviewService reviewService) {
		this.restaurantRepository = restaurantRepository; 
		this.favoriteRepository = favoriteRepository;
		this.reviewService = reviewService;
	}     
  
	@GetMapping
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
						@RequestParam(name = "category", required = false) String category,
						@RequestParam(name = "price", required = false) Integer price,
						@RequestParam(name = "order", required = false) String order,
						@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
						Model model) 
	{
		Page<Restaurant> restaurantPage;
                
		if (keyword != null && !keyword.isEmpty()) {
			//restaurantPage = restaurantRepository.findByNameLikeOrAddressLike("%" + keyword + "%", "%" + keyword + "%", pageable);
			if (order != null && order.equals("lowerPriceAsc")) {
				restaurantPage = restaurantRepository.findByNameLikeOrAddressLikeOrderByLowerPriceAsc("%" + keyword + "%", "%" + keyword + "%", pageable);
			} else {
				restaurantPage = restaurantRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%", "%" + keyword + "%", pageable);
			}
		} else if (category != null && !category.isEmpty()) {
			//restaurantPage = restaurantRepository.findByAddressLike("%" + area + "%", pageable);
			if (order != null && order.equals("lowerPriceAsc")) {
				restaurantPage = restaurantRepository.findByCategory_NameLikeOrderByLowerPriceAsc("%" + category + "%", pageable);
			} else {
				restaurantPage = restaurantRepository.findByCategory_NameLikeOrderByCreatedAtDesc("%" + category + "%", pageable);
			}
		} else if (price != null) {
			//restaurantPage = restaurantRepository.findByLowerPriceLessThanEqual(price, pageable);
			if (order != null && order.equals("lowerPriceAsc")) {
				restaurantPage = restaurantRepository.findByLowerPriceLessThanEqualOrderByLowerPriceAsc(price, pageable);
			} else {
				restaurantPage = restaurantRepository.findByLowerPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
			}
		} else {
			//restaurantPage = restaurantRepository.findAll(pageable);
			if (order != null && order.equals("lowerPriceAsc")) {
				restaurantPage = restaurantRepository.findAllByOrderByLowerPriceAsc(pageable);
			} else {
				restaurantPage = restaurantRepository.findAllByOrderByCreatedAtDesc(pageable);
			}
		} 
		
		model.addAttribute("restaurantPage", restaurantPage);
		model.addAttribute("keyword", keyword);
		model.addAttribute("category", category);
		model.addAttribute("price", price);
		model.addAttribute("order", order);
		model.addAttribute("eachReview", reviewService.eachReview());
        
		return "restaurants/index";
    }
	
	
	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		Restaurant restaurant = restaurantRepository.getReferenceById(id);
		User user;
		BigDecimal average;
		
		if (userDetailsImpl != null) { 
		user = userDetailsImpl.getUser();
		Favorite favorite = favoriteRepository.findByUserAndRestaurant(user, restaurant);
		model.addAttribute("favorite", favorite);
		}
		
		average = reviewService.aveReview(restaurant);
		
		model.addAttribute("restaurant", restaurant);
		model.addAttribute("reservationInputForm", new ReservationInputForm());
		model.addAttribute("reviewAverage", average);
		
		//出力確認用
		//System.out.println(favorite);
		//reviewService.aveReview(restaurant);
		
		return "restaurants/show";
	}

}
