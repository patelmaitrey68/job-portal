
package com.jobportal.service;
import com.jobportal.exception.JobPortalException;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jobportal.entity.Resume;
import com.jobportal.entity.User;
import com.jobportal.repository.ResumeRepository;

@Service
public class ResumeServiceImpl implements ResumeService {
	
	@Autowired
	private ResumeRepository resumeRepository;

	@Override
	public Resume uploadResume(Resume resume, User user) {
		resume.setUserId(user);
		resume.setUploadedAt(LocalDateTime.now());
		
		// If this is the first resume, set as default
		if (resumeRepository.countByUserId(user) == 0) {
			resume.setIsDefault(true);
		} else if (resume.getIsDefault() == null || resume.getIsDefault()) {
			// If setting as default, unset others
			resumeRepository.findByUserId(user).forEach(r -> {
				if (r.getIsDefault()) {
					r.setIsDefault(false);
					resumeRepository.save(r);
				}
			});
			resume.setIsDefault(true);
		}
		
		return resumeRepository.save(resume);
	}

	@Override
	public Resume getResumeById(String resumeId, User user) {
		Resume resume = resumeRepository.findByIdAndUserId(resumeId, user)
				   .orElseThrow(() -> new JobPortalException("Resume not found"));
		return resume;
	}

	@Override
	public List<Resume> getResumesByUser(User user) {
		return resumeRepository.findByUserId(user);
	}

	@Override
	public Resume setAsDefault(String resumeId, User user) {
		Resume resume = resumeRepository.findByIdAndUserId(resumeId, user)
				   .orElseThrow(() -> new JobPortalException("Resume not found"));
		
		// Unset all other defaults
		resumeRepository.findByUserId(user).forEach(r -> {
			if (r.getIsDefault() && !r.getId().equals(resumeId)) {
				r.setIsDefault(false);
				resumeRepository.save(r);
			}
		});
		
		resume.setIsDefault(true);
		return resumeRepository.save(resume);
	}

	@Override
	public Resume getDefaultResume(User user) {
		return resumeRepository.findByUserIdAndIsDefaultTrue(user)
				   .orElseThrow(() -> new JobPortalException("No default resume found"));
	}

	@Override
	public void deleteResume(String resumeId, User user) {
		Resume resume = resumeRepository.findByIdAndUserId(resumeId, user)
				   .orElseThrow(() -> new JobPortalException("Resume not found"));
		
		// If deleting default resume, set another as default
		if (resume.getIsDefault()) {
			List<Resume> otherResumes = resumeRepository.findByUserId(user);
			otherResumes.remove(resume);
			if (!otherResumes.isEmpty()) {
				otherResumes.get(0).setIsDefault(true);
				resumeRepository.save(otherResumes.get(0));
			}
		}
		
		resumeRepository.delete(resume);
	}
}

