package com.sharavel.sharavel_be.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sharavel.sharavel_be.entity.Trip;

@Repository
public interface TripRepo extends JpaRepository<Trip, Integer> {
	Optional<Trip> findById(Long id);

	Trip findByUuid(String uuid);

	// findByUid만 하면 UserEntity 객체를 직접 받아야 해서 보통 findByUid_Id를 사용함.
	List<Trip> findByUid_Id(Integer userId);

	List<Trip> findByCountries(List<String> countries);
}
