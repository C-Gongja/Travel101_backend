package com.sharavel.sharavel_be.service;

import org.springframework.stereotype.Service;

import com.sharavel.sharavel_be.entity.RoleEntity;
import com.sharavel.sharavel_be.repository.RoleRepo;
import com.sharavel.sharavel_be.util.RoleConstants;

import jakarta.annotation.PostConstruct;

@Service
public class DataInitializer {
	private final RoleRepo roleRepository;

	public DataInitializer(RoleRepo roleRepository) {
		this.roleRepository = roleRepository;
	}

	@PostConstruct
	public void init() {
		// USER 역할 초기화
		if (!roleRepository.findByName(RoleConstants.ROLE_USER).isPresent()) {
			RoleEntity userRole = new RoleEntity();
			userRole.setName(RoleConstants.ROLE_USER);
			roleRepository.save(userRole);
		}
		// ADMIN 역할 초기화
		if (!roleRepository.findByName(RoleConstants.ROLE_ADMIN).isPresent()) {
			RoleEntity adminRole = new RoleEntity();
			adminRole.setName(RoleConstants.ROLE_ADMIN);
			roleRepository.save(adminRole);
		}
	}
}

// can be use as resource/data.sql
// INSERT INTO role_entity (name) VALUES ('USER') ON DUPLICATE KEY UPDATE name = 'USER';
// INSERT INTO role_entity (name) VALUES ('ADMIN') ON DUPLICATE KEY UPDATE name = 'ADMIN';