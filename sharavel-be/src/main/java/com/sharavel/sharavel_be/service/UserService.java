package com.sharavel.sharavel_be.service;

import org.springframework.http.ResponseEntity;

import com.sharavel.sharavel_be.dto.UserProfileDto;

public interface UserService {
	public ResponseEntity<?> verify();

	public UserProfileDto getProfile(String email);
}
