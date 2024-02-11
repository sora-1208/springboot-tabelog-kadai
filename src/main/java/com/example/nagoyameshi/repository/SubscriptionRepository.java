package com.example.nagoyameshi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Subscription;
import com.example.nagoyameshi.entity.User;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
	
	//ログイン中のユーザのsubscriptionIdを取得
	public Subscription findByUser(User user);

}
