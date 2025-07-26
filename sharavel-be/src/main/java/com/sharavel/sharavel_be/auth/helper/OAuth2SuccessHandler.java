package com.sharavel.sharavel_be.auth.helper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.sharavel.sharavel_be.security.CustomUserDetails;
import com.sharavel.sharavel_be.security.util.JwtUtil;
import com.sharavel.sharavel_be.user.entity.Users;
import com.sharavel.sharavel_be.user.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
	private static final Logger logger = LoggerFactory.getLogger(OAuth2SuccessHandler.class);

	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;

	public OAuth2SuccessHandler(JwtUtil jwtUtil, UserRepository userRepository) {
		this.jwtUtil = jwtUtil;
		this.userRepository = userRepository;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		OAuth2User user = (OAuth2User) authentication.getPrincipal();
		String email = user.getAttribute("email");

		Users authUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("User not found for email: " + email));
		CustomUserDetails userDetails = new CustomUserDetails(authUser);

		String refreshToken = jwtUtil.generateRefreshToken(userDetails);

		Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
		refreshCookie.setHttpOnly(true);
		refreshCookie.setSecure(false); // false for dev env
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge(86400000); // 7days
		response.addCookie(refreshCookie);

		String state = request.getParameter("state");
		String redirectUrl = "/";

		if (state != null && !state.isEmpty()) {
			try {
				byte[] decodedBytes = Base64.getUrlDecoder().decode(state);
				redirectUrl = new String(decodedBytes, StandardCharsets.UTF_8);
			} catch (IllegalArgumentException e) {
				logger.warn("Invalid state parameter: {}. Falling back to default redirect URL.", state, e);
			}
		}

		response.sendRedirect(redirectUrl);
	}
}
