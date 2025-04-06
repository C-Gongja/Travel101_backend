package com.sharavel.sharavel_be.auth.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class AuthDto {
	private final UserInfo user;
	private final String accessToken;

	public AuthDto(UserInfo user, String accessToken) {
		this.user = user;
		this.accessToken = accessToken;
	}

	public static class UserInfo {
		private final String uuid;
		private final String name;
		private final Collection<? extends GrantedAuthority> roles;

		public UserInfo(String uuid, String name, Collection<? extends GrantedAuthority> roles) {
			this.uuid = uuid;
			this.name = name;
			this.roles = roles;
		}

		// Getters
		public String getUuid() {
			return uuid;
		}

		public String getName() {
			return name;
		}

		public Collection<? extends GrantedAuthority> getRoles() {
			return roles;
		}
	}

	public UserInfo getUser() {
		return user;
	}

	public String getAccessToken() {
		return accessToken;
	}
}
