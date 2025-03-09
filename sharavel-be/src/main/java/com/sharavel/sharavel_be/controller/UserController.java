package com.sharavel.sharavel_be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sharavel.sharavel_be.dto.UserProfileDto;
import com.sharavel.sharavel_be.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
	@Autowired
	UserService userService;

	@GetMapping("/verify")
	public ResponseEntity<?> verifyUser() {
		return userService.verify();
	}

	@GetMapping("/profile")
	public ResponseEntity<?> userProfile() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String email = authentication.getName();
			System.out.println("email: "+ email);
			UserProfileDto user = userService.getProfile(email);

			return ResponseEntity.ok(user);
		} catch (Error e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Can't get a user profile");
		}
	}

	@GetMapping("/admin/adminProfile")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String adminProfile() {
		return "Welcome to Admin Profile";
	}

}
