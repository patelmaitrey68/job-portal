package com.jobportal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jobportal.dto.JobDTO;
import com.jobportal.entity.User;

public interface JobService {
	JobDTO createJob(JobDTO jobDTO, User recruiter);
	JobDTO updateJob(String jobId, JobDTO jobDTO, User recruiter);
	void deleteJob(String jobId, User recruiter);
	JobDTO getJobById(String jobId);
	Page<JobDTO> getAllJobs(Pageable pageable);
	Page<JobDTO> getJobsByRecruiter(User recruiter, Pageable pageable);
	Page<JobDTO> searchJobs(String searchText, Pageable pageable);
	Page<JobDTO> filterJobs(String location, String jobType, String category, Pageable pageable);
	List<JobDTO> getFeaturedJobs();
}

