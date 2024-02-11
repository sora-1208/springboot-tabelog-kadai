package com.example.nagoyameshi.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReservationRegisterForm;
import com.example.nagoyameshi.repository.ReservationRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.UserRepository;

@Service
public class ReservationService {
	
	private final ReservationRepository reservationRepository;
	private final RestaurantRepository restaurantRepository;
	private final UserRepository userRepository;
	
	public ReservationService(ReservationRepository reservationRepository, RestaurantRepository restaurantRepository, UserRepository userRepository) {
		this.reservationRepository = reservationRepository;
		this.restaurantRepository = restaurantRepository;
		this.userRepository = userRepository;
	}
	
	
	@Transactional
	public void create(ReservationRegisterForm reservationRegisterForm) {
		Reservation reservation = new Reservation();
		Restaurant restaurant = restaurantRepository.getReferenceById(reservationRegisterForm.getRestaurantId());
		User user = userRepository.getReferenceById(reservationRegisterForm.getUserId());
		LocalDate reservationDate = LocalDate.parse(reservationRegisterForm.getReservationDate());
		LocalTime reservationTime = LocalTime.parse(reservationRegisterForm.getReservationTime());

		reservation.setRestaurant(restaurant);
		reservation.setUser(user);
		reservation.setReservationDate(reservationDate);
		reservation.setReservationTime(reservationTime);
		reservation.setNumberOfPeople(reservationRegisterForm.getNumberOfPeople());
		
		reservationRepository.save(reservation);
	}
	
	
	//予約人数が定員以下かどうかチェックする
	public boolean isWithinCapacity(Integer numberOfPeople, Integer capacity) {
		return numberOfPeople <= capacity;
	}
	
	
	//営業時間内かどうかチェックするメソッドの追加
	public boolean isWithinTime(LocalTime reservationTime, Restaurant restaurant) {
		LocalTime openingTime = restaurant.getOpeningTime();
		LocalTime closingTime = restaurant.getClosingTime();
		LocalTime lastTime = closingTime.minusHours(1);
		
		return !reservationTime.isBefore(openingTime) && !reservationTime.isAfter(lastTime);
	}
	
	//定休日ではないかどうかチェックするメソッドの追加
	public boolean isRegularHoliday(LocalDate reservationDate, Restaurant restaurant) {
		DayOfWeek dayOfWeek = reservationDate.getDayOfWeek();
		String nameOfWeek = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.JAPANESE);
		
		String regularHoliday = restaurant.getRegularHoliday();
		
		return regularHoliday.contains(nameOfWeek);
	}
	

}
