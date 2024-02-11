package com.example.nagoyameshi.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.StripeService;
import com.example.nagoyameshi.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/user")
public class UserController {
	
	private final UserRepository userRepository; 
	private final UserService userService;
	private final StripeService stripeService;
    
	public UserController(UserRepository userRepository, UserService userService, StripeService stripeService) {
		this.userRepository = userRepository; 
		this.userService = userService;
		this.stripeService = stripeService;
	}    
    
	@GetMapping
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {         
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());  
		
		//出力確認
		//System.out.println("UserControllerの43行目です。");
		//System.out.print(user.getRole().getId());
		//System.out.println(user.getRole().getName());
        
		model.addAttribute("user", user);
        
		return "user/index";
    }
	
	@GetMapping("/edit")
	public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
		UserEditForm userEditForm = new UserEditForm(user.getId(), user.getName(), user.getPostalCode(), user.getAddress(), user.getPhoneNumber(), user.getEmail());
		
		model.addAttribute("userEditForm", userEditForm);
		
		return "user/edit";
	}
	
	@PostMapping("/update")
	public String update(@ModelAttribute @Validated UserEditForm userEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		// メールアドレスが変更されており、かつ登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
		if (userService.isEmailChanged(userEditForm) && userService.isEmailRegistered(userEditForm.getEmail())) {
			FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
			bindingResult.addError(fieldError);                       
		}
        
		if (bindingResult.hasErrors()) {
			return "user/edit";
		}
        
		userService.update(userEditForm);
		redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");
        
		return "redirect:/user";
	}
	
	@GetMapping("/subscription")
	public String register(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, HttpServletRequest httpServletRequest, Model model) {         
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());  
		
		String sessionId = stripeService.createStripeSession(user, httpServletRequest);
        
		model.addAttribute("user", user);
		model.addAttribute("sessionId", sessionId);
        
		return "user/subscription";
    }
	
	@GetMapping("/delete")
	public String delete(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, HttpServletRequest httpServletRequest, Model model) {         
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());  
		
		String sessionId = stripeService.createStripeSession(user, httpServletRequest);
        
		model.addAttribute("user", user);
		model.addAttribute("sessionId", sessionId);
        
		return "user/delete";
    }
	
	//
	@GetMapping("/delete/execute")
	public String execute(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, RedirectAttributes redirectAttributes, Model model) {
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
		
		stripeService.cancelSubscription(user);
		redirectAttributes.addFlashAttribute("deleteMessage", "有料会員登録を解約しました。");
		//user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
		//model.addAttribute("user", user);
		
		return "redirect:/login?reserved";
	}

}
