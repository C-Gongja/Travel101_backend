package com.sharavel.sharavel_be.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sharavel.sharavel_be.trip.entity.Days;
import com.sharavel.sharavel_be.trip.entity.Locations;

@Repository
public interface LocationsRepository extends JpaRepository<Locations, Long> {
	Locations findByDay(Days day);
}
