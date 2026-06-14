package com.jobportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.jobportal.entity.Company;
import com.jobportal.entity.User;

public interface CompanyRepository extends JpaRepository<Company, String> {
	
	// Find company by recruiter
	Optional<Company> findByRecruiterId(User recruiterId);
	
	// Find company by name
	Optional<Company> findByCompanyName(String companyName);
	
	// Search companies by text
	@org.springframework.data.jpa.repository.Query("SELECT c FROM Company c WHERE LOWER(c.companyName) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', ?1, '%'))")
	java.util.List<Company> searchCompanies(String searchText);
}

