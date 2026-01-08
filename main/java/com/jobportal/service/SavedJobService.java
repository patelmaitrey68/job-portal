package com.jobportal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jobportal.entity.Job;
import com.jobportal.entity.SavedJob;
import com.jobportal.entity.User;

public interface SavedJobService {
	SavedJob saveJob(String jobId, User user);
	void unsaveJob(String jobId, User user);
	boolean isJobSaved(String jobId, User user);
	Page<SavedJob> getSavedJobs(User user, Pageable pageable);
	List<SavedJob> getAllSavedJobs(User user);
}

