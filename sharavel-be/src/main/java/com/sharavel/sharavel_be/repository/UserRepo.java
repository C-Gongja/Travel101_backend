package com.sharavel.sharavel_be.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sharavel.sharavel_be.entity.Users;

@Repository
public interface UserRepo extends JpaRepository<Users, Integer> {
	Optional<Users> findByUsername(String username);

	Boolean existsByUsername(String username);

	Optional<Users> findByEmail(String email);

	Boolean existsByEmail(String email);
}
