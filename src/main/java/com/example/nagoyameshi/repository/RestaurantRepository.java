package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
	public Page<Restaurant> findByNameLike(String keyword, Pageable pageable);
	
	//カテゴリテーブルを作成次第、追加予定（27章参考）
	//public Page<Restaurant> findByNameLikeOrAddressLike(String nameKeyword, String addressKeyword, Pageable pageable);
	//public Page<Restaurant> findByAddressLike(String area, Pageable pageable);
	//public Page<Restaurant> findByLowerPriceLessThanEqual(Integer price, Pageable pageable);
	
	//並べ替え機能追加（28章参考）
	public Page<Restaurant> findByNameLikeOrAddressLikeOrderByCreatedAtDesc(String nameKeyword, String addressKeyword, Pageable pageable);  
	public Page<Restaurant> findByNameLikeOrAddressLikeOrderByLowerPriceAsc(String nameKeyword, String addressKeyword, Pageable pageable);  
	public Page<Restaurant> findByCategory_NameLikeOrderByCreatedAtDesc(String category, Pageable pageable);
	public Page<Restaurant> findByCategory_NameLikeOrderByLowerPriceAsc(String category, Pageable pageable);
	public Page<Restaurant> findByLowerPriceLessThanEqualOrderByCreatedAtDesc(Integer price, Pageable pageable);
	public Page<Restaurant> findByLowerPriceLessThanEqualOrderByLowerPriceAsc(Integer price, Pageable pageable); 
	public Page<Restaurant> findAllByOrderByCreatedAtDesc(Pageable pageable);
	public Page<Restaurant> findAllByOrderByLowerPriceAsc(Pageable pageable);
    
	//新着順に店舗を10件取得するメソッド（29章参考)
	public List<Restaurant> findTop10ByOrderByCreatedAtDesc();
}

