package com.sharavel.sharavel_be.trip.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sharavel.sharavel_be.trip.entity.Days;
import com.sharavel.sharavel_be.trip.entity.Trip;

@Repository
public interface DaysRepository extends JpaRepository<Days, Long> {
	@Override
	Optional<Days> findById(Long id);

	// Trip 객체로 Day 목록 조회
	List<Days> findByTrip(Trip trip);

	Days findByTrip_UidAndNumber(String tripUid, Integer number);
}
