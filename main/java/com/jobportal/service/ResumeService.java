package com.jobportal.service;

import java.util.List;

import com.jobportal.entity.Resume;
import com.jobportal.entity.User;

public interface ResumeService {
	Resume uploadResume(Resume resume, User user);
	Resume getResumeById(String resumeId, User user);
	List<Resume> getResumesByUser(User user);
	Resume setAsDefault(String resumeId, User user);
	Resume getDefaultResume(User user);
	void deleteResume(String resumeId, User user);
}

