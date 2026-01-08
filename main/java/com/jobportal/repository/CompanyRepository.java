package com.jobportal.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.jobportal.entity.Company;
import com.jobportal.entity.User;

public interface CompanyRepository extends MongoRepository<Company, String> {
	
	// Find company by recruiter
	Optional<Company> findByRecruiterId(User recruiterId);
	
	// Find company by name
	Optional<Company> findByCompanyName(String companyName);
	
	// Search companies by text
	@Query("{ $text: { $search: ?0 } }")
	java.util.List<Company> searchCompanies(String searchText);
}

