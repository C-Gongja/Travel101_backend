package com.sharavel.sharavel_be.security;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sharavel.sharavel_be.entity.UserEntity;

public class CustomUserDetails implements UserDetails {
	final private String name;
	final private String email; // Changed from 'name' to 'username' for clarity
	@JsonIgnore
	final private String password;
	private final Collection<? extends GrantedAuthority> authorities;

	public CustomUserDetails(UserEntity userInfo) {
		this.name = userInfo.getName();
		this.email = userInfo.getEmail();
		this.password = userInfo.getPassword();
		this.authorities = userInfo.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
				.collect(Collectors.toList());
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public String getName() {
		return name;
	}

	// getUsername(): email을 반환 → Authentication의 getName()이 이를 반영.
	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true; // Implement your logic if you need this
	}

	@Override
	public boolean isAccountNonLocked() {
		return true; // Implement your logic if you need this
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true; // Implement your logic if you need this
	}

	@Override
	public boolean isEnabled() {
		return true; // Implement your logic if you need this
	}
}