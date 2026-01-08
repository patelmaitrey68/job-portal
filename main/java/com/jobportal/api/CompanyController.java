package com.jobportal.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.entity.Company;
import com.jobportal.entity.User;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.CompanyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/company")
@CrossOrigin(origins = "*")
public class CompanyController {
	
	@Autowired
	private CompanyService companyService;
	
	@Autowired
	private UserRepository userRepository;
	
	@PreAuthorize("hasRole('EMPLOYER')")
	@PostMapping("/profile")
	public ResponseEntity<Company> createCompanyProfile(
			@Valid @RequestBody Company company,
			Authentication authentication) {
		User recruiter = getCurrentUser(authentication);
		Company created = companyService.createCompanyProfile(company, recruiter);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PreAuthorize("hasRole('EMPLOYER')")
	@PutMapping("/profile")
	public ResponseEntity<Company> updateCompanyProfile(
			@Valid @RequestBody Company company,
			Authentication authentication) {
		User recruiter = getCurrentUser(authentication);
		Company updated = companyService.updateCompanyProfile(company, recruiter);
		return ResponseEntity.ok(updated);
	}

	@PreAuthorize("hasRole('EMPLOYER')")
	@GetMapping("/profile")
	public ResponseEntity<Company> getCompanyProfile(Authentication authentication) {
		User recruiter = getCurrentUser(authentication);
		Company company = companyService.getCompanyProfile(recruiter);
		return ResponseEntity.ok(company);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Company> getCompanyById(@PathVariable String id) {
		Company company = companyService.getCompanyById(id);
		return ResponseEntity.ok(company);
	}
	
	private User getCurrentUser(Authentication authentication) {
		String email = authentication.getName();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found"));
	}
}

