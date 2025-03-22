package com.sharavel.sharavel_be.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sharavel.sharavel_be.entity.Roles;

public interface RoleRepo extends JpaRepository<Roles, Long> {
	Optional<Roles> findByName(String name);
}
