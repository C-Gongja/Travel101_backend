package com.sharavel.sharavel_be.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sharavel.sharavel_be.user.dto.UserProfileDto;
import com.sharavel.sharavel_be.user.service.UserService;

@RestController
@RequestMapping("/public/user")
public class PublicUserController {
	@Autowired
	UserService userService;

	@GetMapping("/profile/{userUuid}")
	public ResponseEntity<?> userProfile(@PathVariable String userUuid) {
		try {
			UserProfileDto user = userService.getProfile(userUuid);
			return ResponseEntity.ok(user);
		} catch (Error e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Can't get a user profile");
		}
	}
}
