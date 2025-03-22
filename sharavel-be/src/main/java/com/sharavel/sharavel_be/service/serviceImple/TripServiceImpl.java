package com.sharavel.sharavel_be.service.serviceImple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.sharavel.sharavel_be.dto.DaysDto;
import com.sharavel.sharavel_be.dto.LocationDto;
import com.sharavel.sharavel_be.dto.TripDto;
import com.sharavel.sharavel_be.entity.Days;
import com.sharavel.sharavel_be.entity.Trip;
import com.sharavel.sharavel_be.entity.Users;
import com.sharavel.sharavel_be.repository.TripRepo;
import com.sharavel.sharavel_be.repository.UserRepo;
import com.sharavel.sharavel_be.service.TripService;

@Service
public class TripServiceImpl implements TripService {

	@Autowired
	private TripRepo tripRepository;

	@Autowired
	private UserRepo userRepository;

	@Override
	public TripDto createTrip(TripDto tripDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("User is not authenticated");
		}
		// 인증된 유저 정보
		String email = authentication.getName();
		Users user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalStateException("User not found"));

		Trip trip = new Trip();
		trip.setUuid(UUID.randomUUID().toString());
		trip.setUid(user);
		trip.setName(tripDto.getName());
		trip.setStartDate(tripDto.getStartDate());
		trip.setEndDate(tripDto.getEndDate());
		trip.setCompleted(tripDto.isCompleted());
		trip.setCountries(tripDto.getCountries());
		trip.setScripted(tripDto.getScripted()); // Default value

		// Initialize days based on date range or TripDto input
		List<Days> days = tripDto.getDays().stream().map(dayDto -> {
			Days day = new Days();
			day.setTripId(trip);
			day.setNumber(dayDto.getNumber());
			day.setLocations(new ArrayList<>()); // No locations at creation
			return day;
		}).collect(Collectors.toList());

		trip.setDays(days);
		Trip savedTrip = tripRepository.save(trip);

		return mapToTripDto(savedTrip);
	}

	@Override
	public TripDto getTripByUuid(String tripUuid) {
		Trip trip = tripRepository.findByUuid(tripUuid);
		return mapToTripDto(trip);
	}

	@Override
	public TripDto getTrip(Long tripId) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getTrip'");
	}

	@Override
	public List<TripDto> getUserAllTrip(Long uid) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getUserAllTrip'");
	}

	@Override
	public ResponseEntity<?> putTrip(String tripId, TripDto updatedTripDto) {
		return ResponseEntity.ok("Successfully put trip");
	}

	@Override
	public ResponseEntity<?> patchTrip(Long tripId, TripDto trip) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'patchTrip'");
	}

	@Override
	public Map<String, Object> updateTripField(String tripUuid, Map<String, Object> updates) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'updateTripField'");
	}

	private TripDto mapToTripDto(Trip trip) {
		TripDto tripDto = new TripDto();
		tripDto.setId(trip.getUuid()); // Use UUID as the identifier instead of internal ID
		tripDto.setName(trip.getName());
		tripDto.setStartDate(trip.getStartDate());
		tripDto.setEndDate(trip.getEndDate());
		tripDto.setCompleted(trip.isCompleted());
		tripDto.setCountries(trip.getCountries());
		tripDto.setScripted(trip.getScripted());

		List<DaysDto> dayDtos = trip.getDays().stream().map(day -> {
			DaysDto dayDto = new DaysDto();
			dayDto.setNumber(day.getNumber());
			dayDto.setLocations(day.getLocations().stream().map(loc -> {
				LocationDto locDto = new LocationDto();
				locDto.setNumber(loc.getNumber());
				locDto.setName(loc.getName());
				locDto.setAddress(loc.getAddress());
				locDto.setDescription(loc.getDescription());
				return locDto;
			}).collect(Collectors.toList()));
			return dayDto;
		}).collect(Collectors.toList());

		tripDto.setDays(dayDtos);
		return tripDto;
	}
}
