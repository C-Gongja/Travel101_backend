package com.sharavel.sharavel_be.user.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.sharavel.sharavel_be.user.dto.UserPersonalInfoDto;
import com.sharavel.sharavel_be.user.dto.UserProfileDto;

public interface UserService {
	public ResponseEntity<?> verify();

	public UserProfileDto getProfile(String email);

	public UserPersonalInfoDto getPersonalInfo(String email);

	public UserProfileDto updateUserProfile(String userUuid);

	public UserPersonalInfoDto updateUserPersonalInfo(String uuid, Map<String, Object> updates);
}
