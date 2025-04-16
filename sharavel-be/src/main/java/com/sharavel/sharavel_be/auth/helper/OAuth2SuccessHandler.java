package com.sharavel.sharavel_be.auth.helper;

import java.io.IOException;

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

	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;

	public OAuth2SuccessHandler(JwtUtil jwtUtil, UserRepository userRepository) {
		this.jwtUtil = jwtUtil;
		this.userRepository = userRepository;
	}

	// send accessToken and refreshToken as custom auth
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		System.out.println("!!!! OAUTH Success");
		OAuth2User user = (OAuth2User) authentication.getPrincipal();
		String email = user.getAttribute("email");

		Users authUser = userRepository.findByEmail(email).orElse(null);
		CustomUserDetails userDetails = new CustomUserDetails(authUser);

		String refreshToken = jwtUtil.generateRefreshToken(userDetails);

		// RefreshToken을 HttpOnly 쿠키로 설정
		Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
		refreshCookie.setHttpOnly(true);
		refreshCookie.setSecure(false); // 개발 환경에서는 false로 할 수도 있음
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge(86400000); // 7일
		response.addCookie(refreshCookie);

		response.sendRedirect("http://localhost:3000/oauth2/redirect");
	}
}
