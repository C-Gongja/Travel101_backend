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
		private final String username;
		private final String picture;
		private final Collection<? extends GrantedAuthority> roles;

		public UserInfo(String uuid, String name, String username, String picture,
				Collection<? extends GrantedAuthority> roles) {
			this.uuid = uuid;
			this.name = name;
			this.username = username;
			this.picture = picture;
			this.roles = roles;
		}

		// Getters
		public String getUuid() {
			return uuid;
		}

		public String getName() {
			return name;
		}

		public String getUsername() {
			return username;
		}

		public Collection<? extends GrantedAuthority> getRoles() {
			return roles;
		}

		public String getPicture() {
			return picture;
		}
	}

	public UserInfo getUser() {
		return user;
	}

	public String getAccessToken() {
		return accessToken;
	}
}
