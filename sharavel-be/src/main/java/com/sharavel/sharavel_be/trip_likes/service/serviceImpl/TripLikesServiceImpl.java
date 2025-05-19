package com.sharavel.sharavel_be.trip_likes.service.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.sharavel.sharavel_be.trip.entity.Trip;
import com.sharavel.sharavel_be.trip.repository.TripRepository;
import com.sharavel.sharavel_be.trip_likes.entity.TripLikes;
import com.sharavel.sharavel_be.trip_likes.repository.TripLikesRepository;
import com.sharavel.sharavel_be.trip_likes.service.TripLikesService;
import com.sharavel.sharavel_be.user.entity.Users;
import com.sharavel.sharavel_be.user.repository.UserRepository;

@Service
public class TripLikesServiceImpl implements TripLikesService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TripRepository tripRepository;
	@Autowired
	private TripLikesRepository tripLikesRepository;

	private Users getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("User not authenticated");
		}
		String email = authentication.getName();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalStateException("Current user not found"));
	}

	@Override
	public String addLike(String tripUid) {
		Users user = getCurrentUser();
		Trip trip = tripRepository.findByTripUid(tripUid)
				.orElseThrow(() -> new IllegalStateException("Current trip not found"));
		TripLikes tripLike = new TripLikes();
		tripLike.setUser(user);
		tripLike.setTrip(trip);
		tripLike.setLikedAt(LocalDateTime.now());
		tripLikesRepository.save(tripLike);

		return "successfully add like";
	}

	@Override
	public String removeLike(String tripUid) {
		Users user = getCurrentUser();
		Trip trip = tripRepository.findByTripUid(tripUid)
				.orElseThrow(() -> new IllegalArgumentException("Trip not found"));

		TripLikes like = tripLikesRepository.findByUserAndTrip(user, trip)
				.orElseThrow(() -> new IllegalStateException("Like not found"));

		tripLikesRepository.delete(like);
		return "successfully remove like";
	}

	@Override
	public int getTripLikes(String tripUid) {
		Trip trip = tripRepository.findByTripUid(tripUid)
				.orElseThrow(() -> new IllegalArgumentException("Trip not found"));
		return tripLikesRepository.countByTrip(trip);
	}

}
