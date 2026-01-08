package com.jobportal.service;

import com.jobportal.entity.Company;
import com.jobportal.entity.User;

public interface CompanyService {
	Company createCompanyProfile(Company company, User recruiter);
	Company updateCompanyProfile(Company company, User recruiter);
	Company getCompanyProfile(User recruiter);
	Company getCompanyById(String companyId);
}

