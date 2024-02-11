package com.example.nagoyameshi.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRegisterForm {
	
	@NotNull(message = "評価をつけてください。")
	private Integer reviewStar;
	
	@NotBlank(message = "コメントを入力してください。")
	private String reviewComment;
	
}
