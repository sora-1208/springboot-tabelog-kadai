package com.example.nagoyameshi.service;

import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.Company;
import com.example.nagoyameshi.form.CompanyEditForm;
import com.example.nagoyameshi.repository.CompanyRepository;

import jakarta.transaction.Transactional;

@Service
public class CompanyService {
	
	private final CompanyRepository companyRepository;
	
	public CompanyService(CompanyRepository companyRepository) {
		this.companyRepository = companyRepository;
	}
	
	@Transactional
	public void update(CompanyEditForm companyEditForm) {
		Company company = companyRepository.getReferenceById(1);
		
		company.setName(companyEditForm.getName());
		company.setEstablish(companyEditForm.getEstablish());
		company.setPostalCode(companyEditForm.getPostalCode());
		company.setAddress(companyEditForm.getAddress());
		company.setPhoneNumber(companyEditForm.getPhoneNumber());
		company.setPresident(companyEditForm.getPresident());
		company.setEmployee(companyEditForm.getEmployee());
		company.setCapital(companyEditForm.getCapital());
		
		companyRepository.save(company);
	}

}
