package com.sharavel.sharavel_be.service.serviceImple;

import java.time.LocalDateTime;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sharavel.sharavel_be.dto.AuthDto;
import com.sharavel.sharavel_be.entity.Roles;
import com.sharavel.sharavel_be.entity.Users;
import com.sharavel.sharavel_be.repository.RoleRepo;
import com.sharavel.sharavel_be.repository.UserRepo;
import com.sharavel.sharavel_be.security.CustomUserDetails;
import com.sharavel.sharavel_be.security.SignupRequest;
import com.sharavel.sharavel_be.security.util.JwtUtil;
import com.sharavel.sharavel_be.service.AuthService;
import com.sharavel.sharavel_be.util.RoleConstants;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthServiceImpl implements AuthService {
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private UserRepo userRepository;
	@Autowired
	private RoleRepo roleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

	@Override
	public ResponseEntity<?> signup(SignupRequest request, HttpServletResponse response) {
		if (userRepository.existsByEmail(request.getEmail())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(("Email is already taken"));
		}

		Users user = new Users();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setCreatedAt(LocalDateTime.now());

		Roles userRole = roleRepository.findByName(RoleConstants.ROLE_USER)
				.orElseThrow(() -> new IllegalStateException("USER role Internal Error"));
		user.setRoles(Collections.singleton(userRole));

		Users savedUser = userRepository.save(user);

		CustomUserDetails userDetails = new CustomUserDetails(savedUser);

		String accessToken = jwtUtil.generateAccessToken(userDetails);
		String refreshToken = jwtUtil.generateRefreshToken(userDetails);

		Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
		refreshCookie.setHttpOnly(true);
		refreshCookie.setSecure(false); // 개발 환경
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge(86400000);
		response.addCookie(refreshCookie);

		return ResponseEntity.ok(new AuthDto(
				new AuthDto.UserInfo(userDetails.getName(), userDetails.getAuthorities()),
				accessToken));
	}

	@Override
	public ResponseEntity<?> login(Authentication authentication, HttpServletResponse response) {
		try {
			String email = authentication.getName();
			Users user = userRepository.findByEmail(email).orElse(null);

			if (user == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("User not found");
			}

			// 어떻게 유저 정보를 사용할것인지 Entity를 사용할것인지 CustomUserDetails을 사용할것인지
			CustomUserDetails userDetails = new CustomUserDetails(user);

			// make user token contains only name and roles
			String accessToken = jwtUtil.generateAccessToken(userDetails);
			String refreshToken = jwtUtil.generateRefreshToken(userDetails);

			Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
			refreshCookie.setHttpOnly(true); // JavaScript 접근 방지
			refreshCookie.setSecure(true); // HTTPS에서만 전송 (개발 환경 제외)
			refreshCookie.setPath("/"); // 전체 경로에서 사용
			refreshCookie.setMaxAge(86400000);
			response.addCookie(refreshCookie);

			return ResponseEntity.ok(new AuthDto(
					new AuthDto.UserInfo(userDetails.getName(), userDetails.getAuthorities()),
					accessToken));

		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Internal Error: " + ex.getMessage());
		}
	}

	@Override
	public ResponseEntity<?> validateRefreshToken(String refreshToken, HttpServletResponse response) {
		logger.debug("refreshToken: " + refreshToken);
		if (refreshToken == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Refresh token missing in cookies");
		}

		try {
			if (!jwtUtil.isRefreshToken(refreshToken)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Invalid refresh token type");
			}

			String email = jwtUtil.extractUsername(refreshToken);
			Users user = userRepository.findByEmail(email)
					.orElseThrow(() -> new IllegalStateException("User not found"));

			CustomUserDetails userDetails = new CustomUserDetails(user);
			if (!jwtUtil.isTokenValid(refreshToken, userDetails)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body("Refresh token expired or invalid");
			}

			String newAccessToken = jwtUtil.generateAccessToken(userDetails);
			// get new refreshToken
			String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

			Cookie refreshCookie = new Cookie("refreshToken", newRefreshToken);
			refreshCookie.setHttpOnly(true); // JavaScript 접근 방지
			refreshCookie.setSecure(false); // HTTPS에서만 전송 (개발 환경 제외)
			refreshCookie.setPath("/"); // 전체 경로에서 사용
			refreshCookie.setMaxAge(86400000);
			response.addCookie(refreshCookie);

			return ResponseEntity.ok(new AuthDto(
					new AuthDto.UserInfo(userDetails.getName(), userDetails.getAuthorities()),
					newAccessToken));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error: " + ex.getMessage());
		}
	}
}
