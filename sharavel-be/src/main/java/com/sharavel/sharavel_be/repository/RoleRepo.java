package com.sharavel.sharavel_be.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sharavel.sharavel_be.entity.RoleEntity;

public interface RoleRepo extends JpaRepository<RoleEntity, Long> {
	Optional<RoleEntity> findByName(String name);
}
