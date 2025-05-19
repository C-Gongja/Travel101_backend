package com.sharavel.sharavel_be.trip_likes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sharavel.sharavel_be.trip_likes.entity.TripLikes;
import com.sharavel.sharavel_be.user.entity.Users;
import com.sharavel.sharavel_be.trip.entity.Trip;

@Repository
public interface TripLikesRepository extends JpaRepository<TripLikes, Long> {
	boolean existsByUserAndTrip(Users user, Trip trip);

	Optional<TripLikes> findByUserAndTrip(Users user, Trip trip);

	int countByTrip(Trip trip);
}
