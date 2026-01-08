package com.jobportal.service;

import com.jobportal.dto.UserDTO;
import com.jobportal.entity.User;

public interface UserService {
	UserDTO getUserProfile(User user);
	UserDTO updateUserProfile(User user, UserDTO userDTO);
	void changePassword(User user, String oldPassword, String newPassword);
	UserDTO getUserById(String userId);
}

