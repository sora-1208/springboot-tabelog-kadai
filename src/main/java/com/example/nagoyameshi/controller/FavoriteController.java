package com.example.nagoyameshi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.FavoriteRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.FavoriteService;

@Controller
public class FavoriteController {
	private final FavoriteRepository favoriteRepository;
	private final RestaurantRepository restaurantRepository;
	private final FavoriteService favoriteService;
	
	public FavoriteController(FavoriteRepository favoriteRepository, RestaurantRepository restaurantRepository, FavoriteService favoriteService) {
		this.favoriteRepository = favoriteRepository;
		this.restaurantRepository = restaurantRepository;
		this.favoriteService = favoriteService;
	}
	
	@GetMapping("/favorites")
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, Model model) {
		User user = userDetailsImpl.getUser();
		Page<Favorite> favoritePage = favoriteRepository.findByUserOrderByCreatedAtDesc(user, pageable);
		
		model.addAttribute("favoritePage", favoritePage);
		
		return "favorites/index";
	}
	
	//お気に入り登録
	@PostMapping("/restaurants/{id}/favorites/create")
	public String create(@PathVariable(name = "id") Integer id,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, RedirectAttributes redirectAttributes)
	{
		Restaurant restaurant = restaurantRepository.getReferenceById(id);
		User user = userDetailsImpl.getUser();
		
		favoriteService.create(restaurant.getId(), user.getId());
		redirectAttributes.addFlashAttribute("successMessage", "お気に入り登録しました。");
		
		return "redirect:/restaurants/{id}";
	}
	
	//お気に入り削除
	@PostMapping("/restaurants/{id}/favorites/delete")
	public String delete(@PathVariable(name = "id") Integer id,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, RedirectAttributes redirectAttributes)
	{
		Restaurant restaurant = restaurantRepository.getReferenceById(id);
		User user = userDetailsImpl.getUser();
		
		Favorite favorite = favoriteRepository.findByUserAndRestaurant(user, restaurant);
		favoriteRepository.delete(favorite);
		
		redirectAttributes.addFlashAttribute("successMessage", "お気に入り解除しました。");
		
		//出力確認用コード
		//System.out.println(favorite);
		
		return "redirect:/restaurants/{id}";
	}

}
