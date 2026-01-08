package com.jobportal.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jobportal.dto.JobDTO;
import com.jobportal.dto.UserDTO;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;

@Service
public class AdminServiceImpl implements AdminService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private ApplicationRepository applicationRepository;

	@Override
	public Map<String, Object> getPlatformStatistics() {
		Map<String, Object> stats = new HashMap<>();
		
		// User statistics
		stats.put("totalUsers", userRepository.count());
		stats.put("activeUsers", userRepository.count());
		stats.put("jobSeekers", userRepository.count());
		stats.put("recruiters", userRepository.count());
		stats.put("admins", userRepository.count());
		
		// Job statistics
		stats.put("totalJobs", jobRepository.count());
		stats.put("activeJobs", jobRepository.countByStatus("active"));
		stats.put("draftJobs", jobRepository.countByStatus("draft"));
		stats.put("closedJobs", jobRepository.countByStatus("closed"));
		
		// Application statistics
		stats.put("totalApplications", applicationRepository.count());
		stats.put("pendingApplications", applicationRepository.countByStatus("pending"));
		stats.put("shortlistedApplications", applicationRepository.countByStatus("shortlisted"));
		stats.put("hiredApplications", applicationRepository.countByStatus("hired"));
		
		return stats;
	}

	@Override
	public Page<UserDTO> getAllUsers(Pageable pageable) {
		Page<User> users = userRepository.findAll(pageable);
		return users.map(User::toDTO);
	}

	@Override
	public UserDTO updateUserStatus(String userId, Boolean isActive) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		
		user.setIsActive(isActive);
		user = userRepository.save(user);
		return user.toDTO();
	}

	@Override
	public void deleteUser(String userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		
		userRepository.delete(user);
	}

	@Override
	public Page<JobDTO> getAllJobs(Pageable pageable) {
		Page<Job> jobs = jobRepository.findAll(pageable);
		return jobs.map(job -> {
			JobDTO dto = new JobDTO();
			dto.setId(job.getId());
			dto.setTitle(job.getTitle());
			dto.setCompany(job.getCompany());
			dto.setStatus(job.getStatus());
			dto.setCreatedAt(job.getCreatedAt());
			return dto;
		});
	}

	@Override
	public void deleteJob(String jobId) {
		Job job = jobRepository.findById(jobId)
				.orElseThrow(() -> new RuntimeException("Job not found"));
		
		jobRepository.delete(job);
	}
}

