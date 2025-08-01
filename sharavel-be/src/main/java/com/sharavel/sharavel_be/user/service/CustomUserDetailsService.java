package com.sharavel.sharavel_be.user.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sharavel.sharavel_be.security.CustomUserDetails;
import com.sharavel.sharavel_be.user.entity.Users;
import com.sharavel.sharavel_be.user.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<Users> userDetail = repository.findByEmail(email); // Assuming 'email' is used as username

		// Converting UserInfo to UserDetails
		return userDetail.map(CustomUserDetails::new)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
	}
}
