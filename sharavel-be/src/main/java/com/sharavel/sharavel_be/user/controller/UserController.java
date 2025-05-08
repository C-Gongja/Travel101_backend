package com.sharavel.sharavel_be.user.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sharavel.sharavel_be.user.dto.UserPersonalInfoDto;
import com.sharavel.sharavel_be.user.dto.UserProfileDto;
import com.sharavel.sharavel_be.user.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
	@Autowired
	UserService userService;

	@GetMapping("/verify")
	public ResponseEntity<?> verifyUser() {
		return userService.verify();
	}

	@GetMapping("/profile/{userUuid}")
	public ResponseEntity<?> userProfile(@PathVariable String userUuid) {
		try {
			UserProfileDto user = userService.getProfile(userUuid);
			return ResponseEntity.ok(user);
		} catch (Error e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Can't get a user profile");
		}
	}

	@GetMapping("/account/{userUuid}")
	public ResponseEntity<?> userPersonalInfo(@PathVariable String userUuid) {
		try {
			UserPersonalInfoDto user = userService.getPersonalInfo(userUuid);
			return ResponseEntity.ok(user);
		} catch (Error e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Can't get a user profile Info");
		}
	}

	@PatchMapping("/account/{userUuid}")
	public ResponseEntity<?> updateUserPersonalInfo(@PathVariable String userUuid,
			@RequestBody Map<String, Object> updates) {
		try {
			UserPersonalInfoDto updatedUserInfoDto = userService.updateUserPersonalInfo(userUuid, updates);
			return ResponseEntity.ok(updatedUserInfoDto);
		} catch (IllegalArgumentException e) {
			System.out.println("!!!!!!!!!!!!!!!!error: " + e.getMessage());
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", e.getMessage()));
		} catch (Error e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Can't update user personal information");
		}
	}

	@GetMapping("/admin/adminProfile")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String adminProfile() {
		return "Welcome to Admin Profile";
	}
}
