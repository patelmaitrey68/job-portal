package com.jobportal.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jobportal.dto.JobDTO;
import com.jobportal.dto.UserDTO;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;

public interface AdminService {
	Map<String, Object> getPlatformStatistics();
	Page<UserDTO> getAllUsers(Pageable pageable);
	UserDTO updateUserStatus(String userId, Boolean isActive);
	void deleteUser(String userId);
	Page<JobDTO> getAllJobs(Pageable pageable);
	void deleteJob(String jobId);
}

