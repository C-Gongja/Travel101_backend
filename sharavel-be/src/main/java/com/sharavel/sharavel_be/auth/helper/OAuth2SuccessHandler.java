package com.sharavel.sharavel_be.auth.helper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

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

		OAuth2User user = (OAuth2User) authentication.getPrincipal();
		String email = user.getAttribute("email");

		Users authUser = userRepository.findByEmail(email).orElse(null);
		CustomUserDetails userDetails = new CustomUserDetails(authUser);

		String accessToken = jwtUtil.generateAccessToken(userDetails);
		String refreshToken = jwtUtil.generateRefreshToken(userDetails);

		// RefreshToken을 HttpOnly 쿠키로 설정
		Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
		refreshCookie.setHttpOnly(true);
		refreshCookie.setSecure(false); // 개발 환경에서는 false로 할 수도 있음
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge(86400000); // 7일
		response.addCookie(refreshCookie);

		String state = request.getParameter("state");
		String redirectUrl = "/";

		if (state != null && !state.isEmpty()) {
			try {
				byte[] decodedBytes = Base64.getUrlDecoder().decode(state);
				redirectUrl = new String(decodedBytes, StandardCharsets.UTF_8);

				// // redirectUrl에 토큰 추가 (프론트엔드에서 사용할 수 있도록)
				if (redirectUrl.contains("?")) {
					redirectUrl += "&token=" + accessToken;
				} else {
					redirectUrl += "?token=" + accessToken;
				}
			} catch (IllegalArgumentException e) {
				// 디코딩 실패 시 기본값 유지
			}
		}

		response.sendRedirect(redirectUrl);
	}
}
