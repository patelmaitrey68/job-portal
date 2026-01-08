
package com.jobportal.service;
import com.jobportal.exception.JobPortalException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jobportal.dto.ApplicationDTO;
import com.jobportal.entity.Application;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.JobRepository;

@Service
public class ApplicationServiceImpl implements ApplicationService {
	
	@Autowired
	private ApplicationRepository applicationRepository;
	
	@Autowired
	private JobRepository jobRepository;

	@Override
	public ApplicationDTO applyToJob(String jobId, ApplicationDTO applicationDTO, User applicant) {
		// Check if job exists
		Job job = jobRepository.findById(jobId)
				   .orElseThrow(() -> new JobPortalException("Job not found"));
		
		// Check if job is active
		if (!"active".equals(job.getStatus())) {
			   throw new JobPortalException("Cannot apply to inactive job");
		}
		
		// Check if already applied
		if (applicationRepository.findByJobIdAndApplicantId(job, applicant).isPresent()) {
			   throw new JobPortalException("You have already applied to this job");
		}
		
		// Create application
		Application application = new Application();
		application.setJobId(job);
		application.setApplicantId(applicant);
		application.setResumeUrl(applicationDTO.getResumeUrl());
		application.setResumeFileName(applicationDTO.getResumeFileName());
		application.setCoverLetter(applicationDTO.getCoverLetter());
		application.setStatus("pending");
		application.setAppliedAt(LocalDateTime.now());
		application.setUpdatedAt(LocalDateTime.now());
		application.setMatchScore(applicationDTO.getMatchScore());
		application.setSkillsMatch(applicationDTO.getSkillsMatch());
		
		application = applicationRepository.save(application);
		
		// Increment job application count
		job.setApplicationCount(job.getApplicationCount() + 1);
		jobRepository.save(job);
		
		return convertToDTO(application);
	}

	@Override
	public ApplicationDTO getApplicationById(String applicationId) {
		Application application = applicationRepository.findById(applicationId)
				   .orElseThrow(() -> new JobPortalException("Application not found"));
		return convertToDTO(application);
	}

	@Override
	public Page<ApplicationDTO> getApplicationsByApplicant(User applicant, Pageable pageable) {
		Page<Application> applications = applicationRepository.findByApplicantId(applicant, pageable);
		return applications.map(this::convertToDTO);
	}

	@Override
	public Page<ApplicationDTO> getApplicationsByJob(Job job, Pageable pageable) {
		Page<Application> applications = applicationRepository.findByJobId(job, pageable);
		return applications.map(this::convertToDTO);
	}

	@Override
	public ApplicationDTO updateApplicationStatus(String applicationId, String status, User recruiter, String notes, String rejectionReason) {
		Application application = applicationRepository.findById(applicationId)
				   .orElseThrow(() -> new JobPortalException("Application not found"));
		
		// Check if recruiter owns the job
		if (!application.getJobId().getPostedBy().getId().equals(recruiter.getId())) {
			   throw new JobPortalException("You don't have permission to update this application");
		}
		
		application.setStatus(status);
		application.setReviewedBy(recruiter);
		application.setReviewedAt(LocalDateTime.now());
		application.setUpdatedAt(LocalDateTime.now());
		
		if (notes != null) {
			application.setNotes(notes);
		}
		
		if (rejectionReason != null && "rejected".equals(status)) {
			application.setRejectionReason(rejectionReason);
		}
		
		if ("shortlisted".equals(status)) {
			// Can set interview date later
		}
		
		application = applicationRepository.save(application);
		return convertToDTO(application);
	}

	@Override
	public void withdrawApplication(String applicationId, User applicant) {
		Application application = applicationRepository.findById(applicationId)
				   .orElseThrow(() -> new JobPortalException("Application not found"));
		
		// Check if applicant owns the application
		if (!application.getApplicantId().getId().equals(applicant.getId())) {
			throw new RuntimeException("You don't have permission to withdraw this application");
		}
		
		application.setStatus("withdrawn");
		application.setUpdatedAt(LocalDateTime.now());
		applicationRepository.save(application);
	}

	@Override
	public List<ApplicationDTO> getApplicationsByStatus(User applicant, String status) {
		List<Application> applications = applicationRepository.findByApplicantIdAndStatus(applicant, status);
		return applications.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	@Override
	public long countApplicationsByJob(Job job) {
		return applicationRepository.countByJobId(job);
	}

	@Override
	public long countApplicationsByApplicant(User applicant) {
		return applicationRepository.countByApplicantId(applicant);
	}
	
	private ApplicationDTO convertToDTO(Application application) {
		ApplicationDTO dto = new ApplicationDTO();
		dto.setId(application.getId());
		dto.setJobId(application.getJobId().getId());
		dto.setJobTitle(application.getJobId().getTitle());
		dto.setCompanyName(application.getJobId().getCompany());
		dto.setApplicantId(application.getApplicantId().getId());
		dto.setApplicantName(application.getApplicantId().getName());
		dto.setResumeUrl(application.getResumeUrl());
		dto.setResumeFileName(application.getResumeFileName());
		dto.setCoverLetter(application.getCoverLetter());
		dto.setStatus(application.getStatus());
		dto.setAppliedAt(application.getAppliedAt());
		dto.setUpdatedAt(application.getUpdatedAt());
		dto.setReviewedAt(application.getReviewedAt());
		if (application.getReviewedBy() != null) {
			dto.setReviewedBy(application.getReviewedBy().getName());
		}
		dto.setNotes(application.getNotes());
		dto.setRejectionReason(application.getRejectionReason());
		dto.setInterviewDate(application.getInterviewDate());
		dto.setMatchScore(application.getMatchScore());
		dto.setSkillsMatch(application.getSkillsMatch());
		return dto;
	}
}

