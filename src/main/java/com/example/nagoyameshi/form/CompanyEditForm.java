package com.example.nagoyameshi.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompanyEditForm {

	@NotNull
	private Integer id;

	@NotBlank(message = "会社名を入力してください。")
	private String name;
	
	@NotBlank(message = "設立日を入力してください。")
	private String establish;
	
	@NotBlank(message = "郵便番号を入力してください。")
	private String postalCode;
	
	@NotBlank(message = "住所を入力してください。")
	private String address;
	
	@NotBlank(message = "電話番号を入力してください。")
	private String phoneNumber;
	
	@NotBlank(message = "代表取締役を入力してください。")
	private String president;
	
	@NotNull(message = "従業員数を入力してください。")
	@Min(value = 1, message = "1人以上に設定してください。")
	private Integer employee;
	
	@NotNull(message = "資本金を入力してください。")
	@Min(value = 1, message = "1万円以上に設定してください。")
	private Integer capital;

}
