package com.example.nagoyameshi.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.SubscriptionRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.SubscriptionCancelParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class StripeService {
	@Value("${stripe.api-key}")
	private String stripeApiKey;
	
	private String priceId = "price_1OZDShK73e8cOAU5E4HUznLb";
	
	private final UserService userService;
	private final SubscriptionService subscriptionService;
	private final SubscriptionRepository subscriptionRepository;
	
	public StripeService(UserService userService, SubscriptionService subscriptionService, SubscriptionRepository subscriptionRepository) {
		this.userService = userService;
		this.subscriptionService = subscriptionService;
		this.subscriptionRepository = subscriptionRepository;
	}
	
	// セッションを作成し、Stripeに必要な情報を返す
	public String createStripeSession(User user, HttpServletRequest httpServletRequest) {
		Stripe.apiKey = stripeApiKey;
		String requestUrl = new String(httpServletRequest.getRequestURL());
		
		//出力確認
		//System.out.println(user.getId().toString());
		//System.out.println(requestUrl);
		
		SessionCreateParams params =
			SessionCreateParams.builder()
				.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
				.addLineItem(
					SessionCreateParams.LineItem.builder()
						.setQuantity(1L)
						.setPrice(priceId)
						.build())
				.setMode(SessionCreateParams.Mode.SUBSCRIPTION)
				.setSuccessUrl(requestUrl.replaceAll("/user/subscription", "/login") + "?reserved")
				.setCancelUrl(requestUrl)
				.setSubscriptionData(
					SessionCreateParams.SubscriptionData.builder()
						.putMetadata("userId", user.getId().toString())
						.putMetadata("name", user.getName())
						.putMetadata("email", user.getEmail())
						.putMetadata("role", user.getRole().toString())
						.build())
			.build();
		try {
			Session session = Session.create(params);
			//System.out.println(session.getSubscription());
			return session.getId();
		} catch (StripeException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	
	// セッションから予約情報を取得し、UserServiceクラスを介してデータベースの会員情報(Role)を更新する  
	public void processSessionCompleted(Event event) {
		Optional<StripeObject> optionalStripeObject = event.getDataObjectDeserializer().getObject();
		
		optionalStripeObject.ifPresent(stripeObject -> {
			Session session = (Session)stripeObject;
			SessionRetrieveParams params = SessionRetrieveParams.builder().addExpand("subscription").build();

			try {
				session = Session.retrieve(session.getId(), params, null);
				Map<String, String> subscriptionObject = session.getSubscriptionObject().getMetadata();
				
				//出力確認
				//System.out.println("StripeServiceの89行目です。");
				//System.out.println(session.getSubscription());
				
				String subscriptionId = session.getSubscription();
				subscriptionService.create(subscriptionObject, subscriptionId);
				userService.roleUpdate(subscriptionObject);
			} catch (StripeException e) {
				e.printStackTrace();
			}
		});
	}
	
	
	//サブスクリプションをキャンセルする。
	public void cancelSubscription(User user) {
		Stripe.apiKey = stripeApiKey;
		
		String subscriptionId = subscriptionRepository.findByUser(user).getSubscriptionId();
		
		SubscriptionCancelParams params = SubscriptionCancelParams.builder().build();
		try {
			Subscription resource = Subscription.retrieve(subscriptionId);
			resource.cancel(params);
			userService.roleUpdate(user);
			subscriptionService.delete(user);
			} catch (StripeException e) {
			e.printStackTrace();
		}
	}
	
	
//	public void processSubscriptionDeleted(Event event) {
//		Optional<StripeObject> optionalStripeObject = event.getDataObjectDeserializer().getObject();
//		
//		optionalStripeObject.ifPresent(stripeObject -> {
//			Subscription subscription = (Subscription)stripeObject;
//
//			try {
//				subscription = Subscription.retrieve(subscription.getId());
//				Map<String, String> subscriptionObject = subscription.getMetadata();
//				
//				//出力確認
//				System.out.println("StripeServiceの133行目です。");
//				System.out.println(subscriptionObject);
//				
//				subscriptionService.delete(subscriptionObject);
//				userService.roleUpdate(subscriptionObject);
//			} catch (StripeException e) {
//				e.printStackTrace();
//			}
//		});
//	}

}
