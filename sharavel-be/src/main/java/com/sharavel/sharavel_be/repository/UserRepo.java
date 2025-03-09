package com.sharavel.sharavel_be.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sharavel.sharavel_be.entity.UserEntity;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Integer> {
	Optional<UserEntity> findByUsername(String username);

	Boolean existsByUsername(String username);

	Optional<UserEntity> findByEmail(String email);

	Boolean existsByEmail(String email);
}
