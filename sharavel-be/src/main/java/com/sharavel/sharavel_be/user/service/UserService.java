package com.sharavel.sharavel_be.user.service;

import org.springframework.http.ResponseEntity;

import com.sharavel.sharavel_be.user.dto.UserProfileDto;

public interface UserService {
	public ResponseEntity<?> verify();

	public UserProfileDto getProfile(String email);
}
