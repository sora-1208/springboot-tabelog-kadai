package com.example.nagoyameshi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer>{
	public Page<Favorite> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
	
	//ユーザーと店舗からお気に入り登録済みか確認する
	public Favorite findByUserAndRestaurant(User user, Restaurant restaurant);
}
