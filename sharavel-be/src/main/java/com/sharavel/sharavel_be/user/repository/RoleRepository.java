package com.sharavel.sharavel_be.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sharavel.sharavel_be.user.entity.Roles;

public interface RoleRepository extends JpaRepository<Roles, Long> {
	Optional<Roles> findByName(String name);
}
