package com.sharavel.sharavel_be.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.sharavel.sharavel_be.security.SignupRequest;

import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
	public ResponseEntity<?> signup(SignupRequest request, HttpServletResponse response);

	public ResponseEntity<?> login(Authentication authentication, HttpServletResponse response);

	public ResponseEntity<?> validateRefreshToken(String refreshToken, HttpServletResponse response);
}
