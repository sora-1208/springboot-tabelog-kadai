package com.example.nagoyameshi.service;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Role;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.SignupForm;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.repository.RoleRepository;
import com.example.nagoyameshi.repository.UserRepository;

@Service
public class UserService {
	
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
    
	public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;        
		this.passwordEncoder = passwordEncoder;
	}    
    
	@Transactional
	public User create(SignupForm signupForm) {
		User user = new User();
		Role role = roleRepository.findByName("ROLE_GENERAL");
        
		user.setName(signupForm.getName());
		user.setPostalCode(signupForm.getPostalCode());
		user.setAddress(signupForm.getAddress());
		user.setPhoneNumber(signupForm.getPhoneNumber());
		user.setEmail(signupForm.getEmail());
		user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
		user.setRole(role);
		user.setEnabled(false);        
        
		return userRepository.save(user);
	}
	
	@Transactional
	public void update(UserEditForm userEditForm) {
		User user = userRepository.getReferenceById(userEditForm.getId());
        
		user.setName(userEditForm.getName());
		user.setPostalCode(userEditForm.getPostalCode());
		user.setAddress(userEditForm.getAddress());
		user.setPhoneNumber(userEditForm.getPhoneNumber());
		user.setEmail(userEditForm.getEmail());      
        
		userRepository.save(user);
	} 
	
	// メールアドレスが登録済みかどうかをチェックする
	public boolean isEmailRegistered(String email) {
		User user = userRepository.findByEmail(email);  
		return user != null;
	}
	
	// パスワードとパスワード（確認用）の入力値が一致するかどうかをチェックする
	public boolean isSamePassword(String password, String passwordConfirmation) {
		return password.equals(passwordConfirmation);
	}
	
	//ユーザーを有効にする
	@Transactional
	public void enableUser(User user) {
		user.setEnabled(true);
		userRepository.save(user);
	}
	
	// メールアドレスが変更されたかどうかをチェックする
	public boolean isEmailChanged(UserEditForm userEditForm) {
		User currentUser = userRepository.getReferenceById(userEditForm.getId());
		return !userEditForm.getEmail().equals(currentUser.getEmail());      
	}
	
	//ユーザーを会員ランクを変更する（無料 → 有料、有料 → 無料）
	@Transactional
	public void roleUpdate(Map<String, String> subscriptionObject) {
		Integer userId = Integer.valueOf(subscriptionObject.get("userId"));
		User user = userRepository.getReferenceById(userId);
		Integer roleId = user.getRole().getId();
		Role role;
		if(roleId == 1) {
			role = roleRepository.findByName("ROLE_SUBSCRIBERS");
		} else {
			role = roleRepository.findByName("ROLE_GENERAL");
		}
		user.setRole(role);
		userRepository.save(user);
	}
	
	//ユーザーを会員ランクを変更する（無料 → 有料、有料 → 無料）
		@Transactional
		public void roleUpdate(User user) {
			Integer roleId = user.getRole().getId();
			Role role;
			if(roleId == 1) {
				role = roleRepository.findByName("ROLE_SUBSCRIBERS");
			} else {
				role = roleRepository.findByName("ROLE_GENERAL");
			}
			user.setRole(role);
			userRepository.save(user);
		}

}