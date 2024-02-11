package com.example.nagoyameshi.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReviewRegisterForm;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.ReviewRepository;


@Service
public class ReviewService {
	
	private final ReviewRepository reviewRepository;
	private final RestaurantRepository restaurantRepository;
	
	public ReviewService(ReviewRepository reviewRepository, RestaurantRepository restaurantRepository) {
		this.reviewRepository = reviewRepository;
		this.restaurantRepository = restaurantRepository;
	}
	
	@Transactional
	public void create(Restaurant restaurant, User user, ReviewRegisterForm reviewRegisterForm) {
		Review review = new Review();
		
		review.setRestaurant(restaurant);
		review.setUser(user);
		review.setReviewStar(reviewRegisterForm.getReviewStar());
		review.setReviewComment(reviewRegisterForm.getReviewComment());
		
		reviewRepository.save(review);
	}
	
	//レビューの平均点を算出
	public BigDecimal aveReview(Restaurant restaurant) {
		List<Review> reviews = reviewRepository.findByRestaurant(restaurant);
		
		int total = 0;
		int denominator = reviews.size();
		double ave = 0;
		BigDecimal bd;
		
		if(denominator > 0) {
			for(Review review : reviews) {
				total += review.getReviewStar();	
			}
			ave = (double)total/denominator;
		}
			
		bd = new BigDecimal(ave);
		bd = bd.setScale(1, RoundingMode.HALF_UP);
			
		return bd;
	}
	
	public Map<Integer, BigDecimal> eachReview() {
		List<Restaurant> restaurants = restaurantRepository.findAll();
		Map<Integer, BigDecimal> eachReviewMap = new HashMap<>();
		for(Restaurant restaurant : restaurants) {
			aveReview(restaurant);
			eachReviewMap.put(restaurant.getId(), aveReview(restaurant));
		}
		return eachReviewMap;
	}
	
	
	//レストランのIDを評価の高い順に並べ替えます。
	public List<Optional<Restaurant>> sortedReview() {
		Map<Integer, BigDecimal> sortedReviewMap = new HashMap<>();
		sortedReviewMap = eachReview().entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k1, k2) -> k1, LinkedHashMap::new));
		List<Optional<Restaurant>> topReviewRestaurants = new ArrayList<Optional<Restaurant>>();
		
		for(Integer key : sortedReviewMap.keySet()) {
			topReviewRestaurants.add(restaurantRepository.findById(key));
		}
		List<Optional<Restaurant>> sbList = topReviewRestaurants.subList(0, 10);
		
		return sbList;
	}
	
}
