
package com.jobportal.service;
import com.jobportal.exception.JobPortalException;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jobportal.entity.Company;
import com.jobportal.entity.User;
import com.jobportal.repository.CompanyRepository;

@Service
public class CompanyServiceImpl implements CompanyService {
	
	@Autowired
	private CompanyRepository companyRepository;

	@Override
	public Company createCompanyProfile(Company company, User recruiter) {
		// Check if company profile already exists
		if (companyRepository.findByRecruiterId(recruiter).isPresent()) {
			   throw new JobPortalException("Company profile already exists");
		}
		
		company.setRecruiterId(recruiter);
		company.setCreatedAt(LocalDateTime.now());
		company.setUpdatedAt(LocalDateTime.now());
		
		return companyRepository.save(company);
	}

	@Override
	public Company updateCompanyProfile(Company company, User recruiter) {
		Company existing = companyRepository.findByRecruiterId(recruiter)
				   .orElseThrow(() -> new JobPortalException("Company profile not found"));
		
		// Update fields
		existing.setCompanyName(company.getCompanyName());
		existing.setCompanyLogo(company.getCompanyLogo());
		existing.setDescription(company.getDescription());
		existing.setWebsite(company.getWebsite());
		existing.setIndustry(company.getIndustry());
		existing.setCompanySize(company.getCompanySize());
		existing.setLocation(company.getLocation());
		existing.setAddress(company.getAddress());
		existing.setPhone(company.getPhone());
		existing.setEmail(company.getEmail());
		existing.setSocialMedia(company.getSocialMedia());
		existing.setFoundedYear(company.getFoundedYear());
		existing.setUpdatedAt(LocalDateTime.now());
		
		return companyRepository.save(existing);
	}

	@Override
	public Company getCompanyProfile(User recruiter) {
		return companyRepository.findByRecruiterId(recruiter)
				   .orElseThrow(() -> new JobPortalException("Company profile not found"));
	}

	@Override
	public Company getCompanyById(String companyId) {
		return companyRepository.findById(companyId)
				   .orElseThrow(() -> new JobPortalException("Company not found"));
	}
}

