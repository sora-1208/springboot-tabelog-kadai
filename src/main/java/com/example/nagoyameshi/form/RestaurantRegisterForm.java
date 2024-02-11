package com.example.nagoyameshi.form;

import java.time.LocalTime;

import org.springframework.web.multipart.MultipartFile;

import com.example.nagoyameshi.entity.Category;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RestaurantRegisterForm {
	
	@NotBlank(message = "店舗名を入力してください。")
	private String name;
	
	private MultipartFile imageFile;
	
	@NotBlank(message = "説明を入力してください。")
	private String description;
	
	@NotNull(message = "価格帯（下限）を入力してください。")
	@Min(value = 1, message = "1円以上に設定してください。")
	private Integer lowerPrice;
	
	@NotNull(message = "価格帯（上限）を入力してください。")
	@Min(value = 1, message = "1円以上に設定してください。")
	private Integer upperPrice;
	
	@NotBlank(message = "住所を入力してください。")
	private String address;
	
	@NotBlank(message = "電話番号を入力してください。")
	private String phoneNumber;
	
	@NotNull(message = "開店時間を入力してください。")
	private LocalTime openingTime;
	
	@NotNull(message = "閉店時間を入力してください。")
	private LocalTime closingTime;
	
	@NotBlank(message = "定休日を入力してください。")
	private String regularHoliday;
	
	@NotNull(message = "席数を入力してください。")
	@Min(value = 1, message = "1席以上に設定してください。")
	private Integer capacity;
	
	@NotNull(message = "ジャンルを選択してください")
	private Category categoryId;

}
