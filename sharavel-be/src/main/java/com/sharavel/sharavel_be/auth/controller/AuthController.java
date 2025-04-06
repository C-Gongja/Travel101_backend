package com.sharavel.sharavel_be.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sharavel.sharavel_be.auth.service.AuthService;
import com.sharavel.sharavel_be.security.SigninRequest;
import com.sharavel.sharavel_be.security.SignupRequest;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	AuthService authService;

	@Autowired
	AuthenticationManager authenticationManager;

	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody @Valid SignupRequest request, HttpServletResponse response) {
		return authService.signup(request, response);
	}

	@PostMapping("/signin")
	public ResponseEntity<?> login(@RequestBody @Valid SigninRequest request, HttpServletResponse response) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

			return authService.login(authentication, response);

		} catch (BadCredentialsException ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
		}
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken,
			HttpServletResponse response) {
		return authService.validateRefreshToken(refreshToken, response);
	}
}
