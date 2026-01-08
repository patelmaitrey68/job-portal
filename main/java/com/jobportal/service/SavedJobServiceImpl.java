
package com.jobportal.service;
import com.jobportal.exception.JobPortalException;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jobportal.entity.Job;
import com.jobportal.entity.SavedJob;
import com.jobportal.entity.User;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.SavedJobRepository;

@Service
public class SavedJobServiceImpl implements SavedJobService {
	
	@Autowired
	private SavedJobRepository savedJobRepository;
	
	@Autowired
	private JobRepository jobRepository;

	@Override
	public SavedJob saveJob(String jobId, User user) {
		Job job = jobRepository.findById(jobId)
				   .orElseThrow(() -> new JobPortalException("Job not found"));
		
		// Check if already saved
		if (savedJobRepository.findByUserIdAndJobId(user, job).isPresent()) {
			   throw new JobPortalException("Job is already saved");
		}
		
		SavedJob savedJob = new SavedJob();
		savedJob.setUserId(user);
		savedJob.setJobId(job);
		savedJob.setSavedAt(LocalDateTime.now());
		
		return savedJobRepository.save(savedJob);
	}

	@Override
	public void unsaveJob(String jobId, User user) {
		Job job = jobRepository.findById(jobId)
				   .orElseThrow(() -> new JobPortalException("Job not found"));
		
		savedJobRepository.deleteByUserIdAndJobId(user, job);
	}

	@Override
	public boolean isJobSaved(String jobId, User user) {
		Job job = jobRepository.findById(jobId)
				   .orElseThrow(() -> new JobPortalException("Job not found"));
		
		return savedJobRepository.findByUserIdAndJobId(user, job).isPresent();
	}

	@Override
	public Page<SavedJob> getSavedJobs(User user, Pageable pageable) {
		return savedJobRepository.findByUserIdOrderBySavedAtDesc(user, pageable);
	}

	@Override
	public List<SavedJob> getAllSavedJobs(User user) {
		return savedJobRepository.findByUserIdOrderBySavedAtDesc(user);
	}
}

