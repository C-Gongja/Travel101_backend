package com.sharavel.sharavel_be.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
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

	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody @Valid SignupRequest request, HttpServletResponse response) {
		return authService.signup(request, response);
	}

	@PostMapping("/signin")
	public ResponseEntity<?> login(@RequestBody @Valid SigninRequest request, HttpServletResponse response) {
		Authentication authentication = authService.authenticate(request);
		return authService.login(authentication, response);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletResponse response) {
		// 쿠키 삭제 (만료 시간 과거로 설정)
		ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
				.httpOnly(true)
				.secure(true)
				.path("/")
				.maxAge(0)
				.build();

		response.setHeader("Set-Cookie", deleteCookie.toString());

		return ResponseEntity.ok("Logged out successfully");
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken,
			HttpServletResponse response) {
		return authService.validateRefreshToken(refreshToken, response);
	}
}
