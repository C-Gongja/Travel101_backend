package com.sharavel.sharavel_be.user.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.sharavel.sharavel_be.auth.dto.AuthDto;
import com.sharavel.sharavel_be.security.CustomUserDetails;
import com.sharavel.sharavel_be.security.util.JwtUtil;
import com.sharavel.sharavel_be.user.dto.UserProfileDto;
import com.sharavel.sharavel_be.user.entity.Users;
import com.sharavel.sharavel_be.user.repository.UserRepository;
import com.sharavel.sharavel_be.user.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private UserRepository userRepository;

	@Override
	public ResponseEntity<?> verify() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(("User not authenticated"));
		}
		// 인증된 유저 정보
		String email = authentication.getName();
		Users user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalStateException("User not found"));
		CustomUserDetails userDetails = new CustomUserDetails(user);

		// 새 accessToken 발급
		String newAccessToken = jwtUtil.generateAccessToken(userDetails);

		return ResponseEntity.ok(new AuthDto(
				new AuthDto.UserInfo(userDetails.getUuid(), userDetails.getName(), userDetails.getAuthorities()),
				newAccessToken));
	}

	@Override
	public UserProfileDto getProfile(String userUuid) {
		Users user = userRepository.findByUuid(userUuid)
				.orElseThrow(() -> new RuntimeException("User not found"));

		return new UserProfileDto(user);
	}
}
