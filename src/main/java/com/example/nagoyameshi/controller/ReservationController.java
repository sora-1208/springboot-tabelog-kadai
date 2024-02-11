package com.example.nagoyameshi.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReservationInputForm;
import com.example.nagoyameshi.form.ReservationRegisterForm;
import com.example.nagoyameshi.repository.ReservationRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.ReservationService;
import com.example.nagoyameshi.service.ReviewService;

@Controller
public class ReservationController {
	
	private final ReservationRepository reservationRepository;
	private final RestaurantRepository restaurantRepository;
	private final ReservationService reservationService;
	private final ReviewService reviewService;
	
	public ReservationController(ReservationRepository reservationRepository, RestaurantRepository restaurantRepository, ReservationService reservationService, ReviewService reviewService) {
		this.reservationRepository = reservationRepository;
		this.restaurantRepository = restaurantRepository; 
		this.reservationService = reservationService;
		this.reviewService = reviewService;
	}
	
	@GetMapping("/reservations")
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, Model model) {
		User user = userDetailsImpl.getUser();
		Page<Reservation> reservationPage = reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
		
		model.addAttribute("reservationPage", reservationPage);
		
		return "reservations/index";
	}
	
	
	//予約内容をチェックし問題なければ、確認画面へ遷移
	@GetMapping("/restaurants/{id}/reservations/input")
	public String input(@PathVariable(name = "id") Integer id,
                        @ModelAttribute @Validated ReservationInputForm reservationInputForm,
                        BindingResult bindingResult,
                        RedirectAttributes redirectAttributes,
                        Model model)
    {   
		Restaurant restaurant = restaurantRepository.getReferenceById(id);
		Integer numberOfPeople = reservationInputForm.getNumberOfPeople();   
		Integer capacity = restaurant.getCapacity();
			
		//予約人数のチェック
		if (numberOfPeople != null) {
			if (!reservationService.isWithinCapacity(numberOfPeople, capacity)) {
				FieldError fieldError = new FieldError(bindingResult.getObjectName(), "numberOfPeople", "予約がいっぱいです。");
				bindingResult.addError(fieldError);                
			}            
		} 
		
		//予約時間は営業時間内かのチェック
		if (reservationInputForm.getFromReservationTime() != null && !reservationInputForm.getFromReservationTime().isBlank()) {
			LocalTime reservationTime = reservationInputForm.getReservationTime();
			if (!reservationService.isWithinTime(reservationTime, restaurant)) {
				FieldError fieldError = new FieldError(bindingResult.getObjectName(), "fromReservationTime", "営業時間外です。");
				bindingResult.addError(fieldError);
			}
		}
		
		//予約日が定休日かをチェック
		if (reservationInputForm.getFromReservationDate() != null && !reservationInputForm.getFromReservationDate().isBlank()) {
			LocalDate reservationDate = reservationInputForm.getReservationDate();
			if (reservationService.isRegularHoliday(reservationDate, restaurant)) {
				FieldError fieldError = new FieldError(bindingResult.getObjectName(), "fromReservationDate", "定休日です。");
				bindingResult.addError(fieldError);
			}
		}
		
        
		if (bindingResult.hasErrors()) {
			BigDecimal average = reviewService.aveReview(restaurant);
			model.addAttribute("reviewAverage", average);
			model.addAttribute("restaurant", restaurant);            
			model.addAttribute("errorMessage", "予約内容に不備があります。"); 
			return "restaurants/show";
		}
        
		redirectAttributes.addFlashAttribute("reservationInputForm", reservationInputForm);           
        
		return "redirect:/restaurants/{id}/reservations/confirm";
    }
	
	
	
	@GetMapping("/restaurants/{id}/reservations/confirm")
	public String confirm(@PathVariable(name = "id") Integer id,
                          @ModelAttribute ReservationInputForm reservationInputForm,
                          @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,                          
                          Model model) 
	{        
		Restaurant restaurant = restaurantRepository.getReferenceById(id);
		User user = userDetailsImpl.getUser(); 
                
		//予約日と予約時間を取得する
		LocalDate reservationDate = reservationInputForm.getReservationDate();
		LocalTime reservationTime = reservationInputForm.getReservationTime();
        
		ReservationRegisterForm reservationRegisterForm = new ReservationRegisterForm(restaurant.getId(), user.getId(), reservationDate.toString(), reservationTime.toString(), reservationInputForm.getNumberOfPeople());
        
		model.addAttribute("restaurant", restaurant);  
		model.addAttribute("reservationRegisterForm", reservationRegisterForm);       
        
		return "reservations/confirm";
	}
	
	
	//データベースに予約内容を保存
	@PostMapping("/restaurants/{id}/reservations/create")
	public String create(@ModelAttribute ReservationRegisterForm reservationRegisterForm) {
		reservationService.create(reservationRegisterForm);
		
		return "redirect:/reservations?reserved";
	}
}
