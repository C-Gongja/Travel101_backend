package com.sharavel.sharavel_be.trip_script.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sharavel.sharavel_be.trip.entity.Trip;
import com.sharavel.sharavel_be.trip_script.entity.TripScript;
import com.sharavel.sharavel_be.user.entity.Users;

@Repository
public interface TripScriptRepository extends JpaRepository<TripScript, Long> {
	Long countByTrip(Trip trip);

	Long countByScriptUser(Users scriptUser);
}
