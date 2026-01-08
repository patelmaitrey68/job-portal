
package com.jobportal.service;
import com.jobportal.exception.JobPortalException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jobportal.dto.JobDTO;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.repository.JobRepository;

@Service
public class JobServiceImpl implements JobService {
	
	@Autowired
	private JobRepository jobRepository;

	@Override
	public JobDTO createJob(JobDTO jobDTO, User recruiter) {
		Job job = new Job();
		job.setTitle(jobDTO.getTitle());
		job.setCompany(jobDTO.getCompany());
		job.setCompanyLogo(jobDTO.getCompanyLogo());
		job.setDescription(jobDTO.getDescription());
		job.setRequirements(jobDTO.getRequirements());
		job.setResponsibilities(jobDTO.getResponsibilities());
		job.setLocation(jobDTO.getLocation());
		job.setJobType(jobDTO.getJobType());
		job.setExperienceLevel(jobDTO.getExperienceLevel());
		job.setSalaryMin(jobDTO.getSalaryMin());
		job.setSalaryMax(jobDTO.getSalaryMax());
		job.setSalaryCurrency(jobDTO.getSalaryCurrency());
		job.setCategory(jobDTO.getCategory());
		job.setIndustry(jobDTO.getIndustry());
		job.setSkills(jobDTO.getSkills());
		job.setPostedBy(recruiter);
		job.setStatus(jobDTO.getStatus() != null ? jobDTO.getStatus() : "active");
		job.setApplicationDeadline(jobDTO.getApplicationDeadline());
		job.setIsFeatured(jobDTO.getIsFeatured() != null ? jobDTO.getIsFeatured() : false);
		job.setApplicationCount(0);
		job.setViews(0);
		job.setCreatedAt(LocalDateTime.now());
		job.setUpdatedAt(LocalDateTime.now());
		
		job = jobRepository.save(job);
		return convertToDTO(job);
	}

	@Override
	public JobDTO updateJob(String jobId, JobDTO jobDTO, User recruiter) {
		Job job = jobRepository.findById(jobId)
				   .orElseThrow(() -> new JobPortalException("Job not found"));
		
		// Check if recruiter owns this job
		if (!job.getPostedBy().getId().equals(recruiter.getId())) {
			   throw new JobPortalException("You don't have permission to update this job");
		}
		
		job.setTitle(jobDTO.getTitle());
		job.setCompany(jobDTO.getCompany());
		job.setCompanyLogo(jobDTO.getCompanyLogo());
		job.setDescription(jobDTO.getDescription());
		job.setRequirements(jobDTO.getRequirements());
		job.setResponsibilities(jobDTO.getResponsibilities());
		job.setLocation(jobDTO.getLocation());
		job.setJobType(jobDTO.getJobType());
		job.setExperienceLevel(jobDTO.getExperienceLevel());
		job.setSalaryMin(jobDTO.getSalaryMin());
		job.setSalaryMax(jobDTO.getSalaryMax());
		job.setSalaryCurrency(jobDTO.getSalaryCurrency());
		job.setCategory(jobDTO.getCategory());
		job.setIndustry(jobDTO.getIndustry());
		job.setSkills(jobDTO.getSkills());
		job.setStatus(jobDTO.getStatus());
		job.setApplicationDeadline(jobDTO.getApplicationDeadline());
		job.setIsFeatured(jobDTO.getIsFeatured());
		job.setUpdatedAt(LocalDateTime.now());
		
		job = jobRepository.save(job);
		return convertToDTO(job);
	}

	@Override
	public void deleteJob(String jobId, User recruiter) {
		Job job = jobRepository.findById(jobId)
				   .orElseThrow(() -> new JobPortalException("Job not found"));
		
		// Check if recruiter owns this job
		if (!job.getPostedBy().getId().equals(recruiter.getId())) {
			   throw new JobPortalException("You don't have permission to delete this job");
		}
		
		jobRepository.delete(job);
	}

	@Override
	public JobDTO getJobById(String jobId) {
		Job job = jobRepository.findById(jobId)
				   .orElseThrow(() -> new JobPortalException("Job not found"));
		
		// Increment views
		job.setViews(job.getViews() + 1);
		jobRepository.save(job);
		
		return convertToDTO(job);
	}

	@Override
	public Page<JobDTO> getAllJobs(Pageable pageable) {
		Page<Job> jobs = jobRepository.findByStatusOrderByCreatedAtDesc("active", pageable);
		return jobs.map(this::convertToDTO);
	}

	@Override
	public Page<JobDTO> getJobsByRecruiter(User recruiter, Pageable pageable) {
		Page<Job> jobs = jobRepository.findByPostedBy(recruiter, pageable);
		return jobs.map(this::convertToDTO);
	}

	@Override
	public Page<JobDTO> searchJobs(String searchText, Pageable pageable) {
		List<Job> jobs = jobRepository.searchJobs(searchText);
		// Convert to page manually
		int start = (int) pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), jobs.size());
		List<Job> pagedJobs = start < jobs.size() ? jobs.subList(start, end) : List.of();
		
		List<JobDTO> jobDTOs = pagedJobs.stream().map(this::convertToDTO).collect(Collectors.toList());
		
		return new PageImpl<>(jobDTOs, pageable, jobs.size());
	}

	@Override
	public Page<JobDTO> filterJobs(String location, String jobType, String category, Pageable pageable) {
		List<Job> allJobs = jobRepository.findByStatus("active");
		
		// Apply filters
		List<Job> filteredJobs = allJobs.stream()
				.filter(job -> {
					// Location filter
					if (location != null && !location.isEmpty()) {
						if (job.getLocation() == null || !job.getLocation().toLowerCase().contains(location.toLowerCase())) {
							return false;
						}
					}
					// JobType filter
					if (jobType != null && !jobType.isEmpty()) {
						if (job.getJobType() == null || !job.getJobType().equalsIgnoreCase(jobType)) {
							return false;
						}
					}
					// Category filter
					if (category != null && !category.isEmpty()) {
						if (job.getCategory() == null || !job.getCategory().toLowerCase().contains(category.toLowerCase())) {
							return false;
						}
					}
					return true;
				})
				.sorted((a, b) -> {
					if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
					return b.getCreatedAt().compareTo(a.getCreatedAt());
				})
				.collect(Collectors.toList());
		
		// Manual pagination
		int start = (int) pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), filteredJobs.size());
		List<Job> pagedJobs = start < filteredJobs.size() ? filteredJobs.subList(start, end) : List.of();
		
		List<JobDTO> jobDTOs = pagedJobs.stream().map(this::convertToDTO).collect(Collectors.toList());
		
		return new PageImpl<>(jobDTOs, pageable, filteredJobs.size());
	}

	@Override
	public List<JobDTO> getFeaturedJobs() {
		List<Job> jobs = jobRepository.findByIsFeaturedTrueAndStatusOrderByCreatedAtDesc("active");
		return jobs.stream().map(this::convertToDTO).collect(Collectors.toList());
	}
	
	private JobDTO convertToDTO(Job job) {
		JobDTO dto = new JobDTO();
		dto.setId(job.getId());
		dto.setTitle(job.getTitle());
		dto.setCompany(job.getCompany());
		dto.setCompanyLogo(job.getCompanyLogo());
		dto.setDescription(job.getDescription());
		dto.setRequirements(job.getRequirements());
		dto.setResponsibilities(job.getResponsibilities());
		dto.setLocation(job.getLocation());
		dto.setJobType(job.getJobType());
		dto.setExperienceLevel(job.getExperienceLevel());
		dto.setSalaryMin(job.getSalaryMin());
		dto.setSalaryMax(job.getSalaryMax());
		dto.setSalaryCurrency(job.getSalaryCurrency());
		dto.setCategory(job.getCategory());
		dto.setIndustry(job.getIndustry());
		dto.setSkills(job.getSkills());
		dto.setPostedBy(job.getPostedBy().getId());
		dto.setPostedByName(job.getPostedBy().getName());
		dto.setStatus(job.getStatus());
		dto.setApplicationDeadline(job.getApplicationDeadline());
		dto.setApplicationCount(job.getApplicationCount());
		dto.setViews(job.getViews());
		dto.setIsFeatured(job.getIsFeatured());
		dto.setCreatedAt(job.getCreatedAt());
		dto.setUpdatedAt(job.getUpdatedAt());
		return dto;
	}
}

