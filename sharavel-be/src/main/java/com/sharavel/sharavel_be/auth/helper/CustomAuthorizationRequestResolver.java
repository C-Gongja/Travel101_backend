package com.sharavel.sharavel_be.auth.helper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import jakarta.servlet.http.HttpServletRequest;

public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

	private final OAuth2AuthorizationRequestResolver defaultResolver;

	public CustomAuthorizationRequestResolver(ClientRegistrationRepository repo, String authorizationRequestBaseUri) {
		this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repo, authorizationRequestBaseUri);
	}

	@Override
	public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
		OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
		return customizeAuthorizationRequest(request, req);
	}

	@Override
	public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
		OAuth2AuthorizationRequest req = defaultResolver.resolve(request, clientRegistrationId);
		return customizeAuthorizationRequest(request, req);
	}

	private OAuth2AuthorizationRequest customizeAuthorizationRequest(HttpServletRequest request,
			OAuth2AuthorizationRequest req) {
		if (req == null) {
			return null;
		}

		String redirectUri = request.getParameter("redirect_uri");
		if (redirectUri == null || redirectUri.isEmpty()) {
			redirectUri = "/";
		}

		// Base64 URL-safe 인코딩해서 state로 저장
		String encodedState = Base64.getUrlEncoder().encodeToString(redirectUri.getBytes(StandardCharsets.UTF_8));

		return OAuth2AuthorizationRequest.from(req)
				.state(encodedState)
				.build();
	}
}