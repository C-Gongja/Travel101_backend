package com.sharavel.sharavel_be.auth.service.serviceImpl;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sharavel.sharavel_be.auth.dto.AuthDto;
import com.sharavel.sharavel_be.auth.dto.SigninRequest;
import com.sharavel.sharavel_be.auth.dto.SignupRequest;
import com.sharavel.sharavel_be.auth.service.AuthService;
import com.sharavel.sharavel_be.security.CustomUserDetails;
import com.sharavel.sharavel_be.security.util.JwtUtil;
import com.sharavel.sharavel_be.user.entity.Roles;
import com.sharavel.sharavel_be.user.entity.Users;
import com.sharavel.sharavel_be.user.repository.RoleRepository;
import com.sharavel.sharavel_be.user.repository.UserRepository;
import com.sharavel.sharavel_be.user.service.UserService;
import com.sharavel.sharavel_be.util.RoleConstants;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthServiceImpl implements AuthService {
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	private final AuthenticationConfiguration authenticationConfiguration;

	private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

	public AuthServiceImpl(AuthenticationConfiguration authenticationConfiguration) {
		this.authenticationConfiguration = authenticationConfiguration;
	}

	private Cookie generateCookie(String refreshToken) {
		Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
		refreshCookie.setHttpOnly(true);
		refreshCookie.setSecure(false); // dev env
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge(86400000); // 7days
		return refreshCookie;
	}

	@Override
	public ResponseEntity<?> signup(SignupRequest request, HttpServletResponse response) {
		if (userRepository.existsByEmail(request.getEmail())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(("Email is already taken"));
		}

		String randomUsername = userService.generateRandomUsername(request.getName());
		Roles userRole = roleRepository.findByName(RoleConstants.ROLE_USER)
				.orElseThrow(() -> new IllegalStateException("USER role Internal Error"));

		Users newUser = new Users.Builder(request.getEmail(), request.getName(), randomUsername, 0, 0)
				.password(passwordEncoder.encode(request.getPassword()))
				.roles(Collections.singleton(userRole))
				.build();

		Users savedUser = userRepository.save(newUser);

		CustomUserDetails userDetails = new CustomUserDetails(savedUser);

		String accessToken = jwtUtil.generateAccessToken(userDetails);
		String refreshToken = jwtUtil.generateRefreshToken(userDetails);

		Cookie refreshCookie = generateCookie(refreshToken);
		response.addCookie(refreshCookie);

		return ResponseEntity.ok(new AuthDto(
				new AuthDto.UserInfo(userDetails.getUuid(), userDetails.getName(), savedUser.getUsername(),
						userDetails.getPicture(), userDetails.getAuthorities()),
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

			CustomUserDetails userDetails = new CustomUserDetails(user);

			// token contains only name and roles
			String accessToken = jwtUtil.generateAccessToken(userDetails);
			String refreshToken = jwtUtil.generateRefreshToken(userDetails);

			Cookie refreshCookie = generateCookie(refreshToken);
			response.addCookie(refreshCookie);

			return ResponseEntity.ok(new AuthDto(
					new AuthDto.UserInfo(userDetails.getUuid(), userDetails.getName(), user.getUsername(),
							userDetails.getPicture(), userDetails.getAuthorities()),
					accessToken));

		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Internal Error: " + ex.getMessage());
		}
	}

	@Override
	public ResponseEntity<?> validateRefreshToken(String refreshToken, HttpServletResponse response) {
		logger.info("refreshToken: " + refreshToken);
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

			Cookie refreshCookie = generateCookie(newRefreshToken);
			response.addCookie(refreshCookie);

			return ResponseEntity.ok(new AuthDto(
					new AuthDto.UserInfo(userDetails.getUuid(), userDetails.getName(), user.getUsername(),
							userDetails.getPicture(), userDetails.getAuthorities()),
					newAccessToken));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error: " + ex.getMessage());
		}
	}

	@Override
	public Authentication authenticate(SigninRequest request) {
		try {
			AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
			return authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		} catch (BadCredentialsException e) {
			// throw e;
			throw new IllegalArgumentException("Invalid email or password.", e); // 400
		} catch (AuthenticationException e) {
			// throw e;
			throw new IllegalStateException("Authentication failed", e); // 401 Unauthorized
		} catch (Exception e) { // 그 외 예상치 못한 모든 예외
			throw new RuntimeException("An unexpected error occurred during authentication.", e); // 500 Internal Server Error
		}
	}
}
