package com.example.nagoyameshi.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.Subscription;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.SubscriptionRepository;
import com.example.nagoyameshi.repository.UserRepository;

import jakarta.transaction.Transactional;


@Service
public class SubscriptionService {
	
	private final SubscriptionRepository subscriptionRepository;
	private final UserRepository userRepository;
	
	public SubscriptionService(SubscriptionRepository subscriptionRepository, UserRepository userRepository) {
		this.subscriptionRepository = subscriptionRepository;
		this.userRepository = userRepository;
	}
	
	
	@Transactional
	public void create(Map<String, String> subscriptionObject, String subscriptionId) {
		Subscription subscription = new Subscription();
		
		Integer userId = Integer.valueOf(subscriptionObject.get("userId"));
		User user = userRepository.getReferenceById(userId);
		
		subscription.setUser(user);
		subscription.setSubscriptionId(subscriptionId);
		
		subscriptionRepository.save(subscription);
	}
	
	
	@Transactional
	public void delete(Map<String, String> subscriptionObject) {
		Integer userId = Integer.valueOf(subscriptionObject.get("userId"));
		User user = userRepository.getReferenceById(userId);
		
		Subscription subscription = subscriptionRepository.findByUser(user);
		
		subscriptionRepository.delete(subscription);
	}
	
	@Transactional
	public void delete(User user) {
		Subscription subscription = subscriptionRepository.findByUser(user);
		
		subscriptionRepository.delete(subscription);
	}

}
