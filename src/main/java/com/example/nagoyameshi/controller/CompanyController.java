package com.example.nagoyameshi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Company;
import com.example.nagoyameshi.form.CompanyEditForm;
import com.example.nagoyameshi.repository.CompanyRepository;
import com.example.nagoyameshi.service.CompanyService;

@Controller
public class CompanyController {
	
	private final CompanyRepository companyRepository;
	private final CompanyService companyService;
	
	public CompanyController(CompanyRepository companyRepository, CompanyService companyService) {
		this.companyRepository = companyRepository;
		this.companyService = companyService;
	}
	
	@GetMapping("/company")
	public String index(Model model) {
		Company company = companyRepository.getReferenceById(1);
		
		model.addAttribute("company",company);
		
		return "company/index";
	}
	
	
	@GetMapping("/company/edit")
	public String edit(Model model) {
		Company company = companyRepository.getReferenceById(1);
		CompanyEditForm companyEditForm = new CompanyEditForm(company.getId(), company.getName(), company.getEstablish(), company.getPostalCode(), company.getAddress(), company.getPhoneNumber(), company.getPresident(), company.getEmployee(), company.getCapital());
		
		model.addAttribute("company",company);
		model.addAttribute("companyEditForm", companyEditForm);
		
		return "company/edit";
	}
	
	
	@PostMapping("company/update")
	public String update(@ModelAttribute @Validated CompanyEditForm companyEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "company/edit";
		}
		
		companyService.update(companyEditForm);
		redirectAttributes.addFlashAttribute("successMessage", "会社情報を更新しました。");
		
		return "redirect:/company";
	}
	

}
