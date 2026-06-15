
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

	@Autowired
	private AiService aiService;

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
		
		// AI Resume Matcher Logic (Real LLM via AiService)
		double matchScore = 0.0;
		List<String> matchedSkills = applicationDTO.getSkillsMatch() != null ? applicationDTO.getSkillsMatch() : new java.util.ArrayList<>();
		String aiFeedbackText = "No AI feedback generated.";
		
		try {
		    String jobDesc = job.getDescription() + " Required Skills: " + String.join(", ", job.getSkills() != null ? job.getSkills() : new java.util.ArrayList<>());
		    String appSkills = String.join(", ", matchedSkills);
		    String appCoverLetter = applicationDTO.getCoverLetter() != null ? applicationDTO.getCoverLetter() : "No cover letter provided.";
		    
		    String prompt = "You are an expert technical recruiter analyzing a candidate's fit for a job. " +
		                    "Job Description: '" + jobDesc + "'. " +
		                    "Candidate Skills: '" + appSkills + "'. " +
		                    "Candidate Cover Letter: '" + appCoverLetter + "'. " +
		                    "Analyze this resume/profile against the job. Output ONLY valid JSON with three keys: " +
		                    "'matchScore' (integer 0-100), " +
		                    "'skillsMatch' (array of strings showing which skills align), " +
		                    "'aiFeedback' (string: detailed explanation of strengths and specific weaknesses or missing skills).";
		                    
		    String jsonResult = aiService.generateContent(prompt);
		    
		    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
		    com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(jsonResult);
		    
		    if (root.has("matchScore")) matchScore = root.get("matchScore").asDouble();
		    if (root.has("aiFeedback")) aiFeedbackText = root.get("aiFeedback").asText();
		    
		    if (root.has("skillsMatch") && root.get("skillsMatch").isArray()) {
		        matchedSkills.clear();
		        for (com.fasterxml.jackson.databind.JsonNode node : root.get("skillsMatch")) {
		            matchedSkills.add(node.asText());
		        }
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    aiFeedbackText = "Failed to process AI Match: " + e.getMessage();
		    matchScore = applicationDTO.getMatchScore() != null ? applicationDTO.getMatchScore() : 0.0;
		}
		
		application.setMatchScore(matchScore);
		application.setSkillsMatch(matchedSkills);
		application.setAiFeedback(aiFeedbackText);
		
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

	@Autowired
	private WebSocketService webSocketService;

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
		
		application = applicationRepository.save(application);
		
		// Send WebSocket Notification to Applicant
		String message = "Your application for " + application.getJobId().getTitle() + " at " + application.getJobId().getCompany() + " was " + status + ".";
		if ("rejected".equals(status) && rejectionReason != null && !rejectionReason.isEmpty()) {
		    message += " Reason: " + rejectionReason;
		}
		webSocketService.sendNotification(application.getApplicantId().getId(), message, "STATUS_UPDATE");
		
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
	public ApplicationDTO acceptOffer(String applicationId, User applicant) {
		Application application = applicationRepository.findById(applicationId)
				   .orElseThrow(() -> new JobPortalException("Application not found"));
		
		if (!application.getApplicantId().getId().equals(applicant.getId())) {
			throw new JobPortalException("You don't have permission to accept this offer");
		}
		if (!"hired".equals(application.getStatus()) && !"shortlisted".equals(application.getStatus())) {
			throw new JobPortalException("You can only accept if you have an offer (shortlisted or hired)");
		}
		
		application.setStatus("accepted");
		application.setUpdatedAt(LocalDateTime.now());
		application = applicationRepository.save(application);
		
		// Auto-withdraw other active applications
		List<Application> otherApps = applicationRepository.findByApplicantId(applicant, Pageable.unpaged()).getContent();
		for (Application app : otherApps) {
			if (!app.getId().equals(application.getId()) && ("pending".equals(app.getStatus()) || "shortlisted".equals(app.getStatus()) || "hired".equals(app.getStatus()))) {
				app.setStatus("withdrawn");
				app.setUpdatedAt(LocalDateTime.now());
				applicationRepository.save(app);
			}
		}
		
		// Send WebSocket Notification to Recruiter
		String message = applicant.getName() + " has accepted the offer for " + application.getJobId().getTitle() + "!";
		webSocketService.sendNotification(application.getJobId().getPostedBy().getId(), message, "OFFER_ACCEPTED");
		
		return convertToDTO(application);
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
		dto.setAiFeedback(application.getAiFeedback());
		return dto;
	}
}

