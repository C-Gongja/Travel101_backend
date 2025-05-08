package com.sharavel.sharavel_be.auth.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.sharavel.sharavel_be.user.entity.Roles;
import com.sharavel.sharavel_be.user.entity.Users;
import com.sharavel.sharavel_be.user.repository.RoleRepository;
import com.sharavel.sharavel_be.user.repository.UserRepository;
import com.sharavel.sharavel_be.user.service.UserService;
import com.sharavel.sharavel_be.util.RoleConstants;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final UserRepository userRepository;
	private final UserService userService;
	private final RoleRepository roleRepository;

	public CustomOAuth2UserService(UserRepository userRepository, UserService userService,
			RoleRepository roleRepository) {
		this.userRepository = userRepository;
		this.userService = userService;
		this.roleRepository = roleRepository;
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		String email = oAuth2User.getAttribute("email");
		String name = oAuth2User.getAttribute("name");
		String picture = oAuth2User.getAttribute("picture");
		String providerId = oAuth2User.getAttribute("sub");
		String randomUsername = userService.generateRandomUsername(name);

		// 저장 또는 업데이트
		Users user = userRepository.findByEmail(email)
				.orElseGet(() -> {
					Users newUser = new Users();
					newUser.setEmail(email);
					newUser.setName(name);
					newUser.setUsername(randomUsername);
					newUser.setPicture(picture);
					newUser.setProvider("google");
					newUser.setProviderId(providerId);
					newUser.setTotalTripDays(0);
					newUser.setTotalTrips(0);
					newUser.setCreatedAt(LocalDateTime.now());

					Roles userRole = roleRepository.findByName(RoleConstants.ROLE_USER)
							.orElseThrow(() -> new IllegalStateException("USER role Internal Error"));
					newUser.setRoles(Collections.singleton(userRole));
					return userRepository.save(newUser);
				});

		Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
		attributes.put("id", user.getId());

		return new DefaultOAuth2User(
				List.of(new SimpleGrantedAuthority("ROLE_USER")),
				attributes,
				"email");
	}
}
