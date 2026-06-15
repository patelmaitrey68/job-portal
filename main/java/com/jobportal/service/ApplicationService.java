package com.jobportal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jobportal.dto.ApplicationDTO;
import com.jobportal.entity.Application;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;

public interface ApplicationService {
	ApplicationDTO applyToJob(String jobId, ApplicationDTO applicationDTO, User applicant);
	ApplicationDTO getApplicationById(String applicationId);
	Page<ApplicationDTO> getApplicationsByApplicant(User applicant, Pageable pageable);
	Page<ApplicationDTO> getApplicationsByJob(Job job, Pageable pageable);
	ApplicationDTO updateApplicationStatus(String applicationId, String status, User recruiter, String notes, String rejectionReason);
	void withdrawApplication(String applicationId, User applicant);
	List<ApplicationDTO> getApplicationsByStatus(User applicant, String status);
	long countApplicationsByJob(Job job);
	long countApplicationsByApplicant(User applicant);
	ApplicationDTO acceptOffer(String applicationId, User applicant);
}

