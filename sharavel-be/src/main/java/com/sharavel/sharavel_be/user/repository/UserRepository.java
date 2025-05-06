package com.sharavel.sharavel_be.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sharavel.sharavel_be.user.entity.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
	Optional<Users> findByUsername(String username);

	Optional<Users> findByUuid(String uuid);

	Boolean existsByUsername(String username);

	Optional<Users> findByEmail(String email);

	Boolean existsByEmail(String email);
}
