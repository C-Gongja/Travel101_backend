package com.sharavel.sharavel_be.trip.service.serviceImpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.sharavel.sharavel_be.countries.entity.Country;
import com.sharavel.sharavel_be.countries.repository.CountryRepository;
import com.sharavel.sharavel_be.follow.repository.UserFollowRepository;
import com.sharavel.sharavel_be.trip.dto.DaysDto;
import com.sharavel.sharavel_be.trip.dto.LocationDto;
import com.sharavel.sharavel_be.trip.dto.TripDto;
import com.sharavel.sharavel_be.trip.dto.TripListDto;
import com.sharavel.sharavel_be.trip.dto.response.TripRequest;
import com.sharavel.sharavel_be.trip.dto.response.TripResponse;
import com.sharavel.sharavel_be.trip.entity.Days;
import com.sharavel.sharavel_be.trip.entity.Locations;
import com.sharavel.sharavel_be.trip.entity.Trip;
import com.sharavel.sharavel_be.trip.mapper.TripMapper;
import com.sharavel.sharavel_be.trip.repository.TripRepository;
import com.sharavel.sharavel_be.trip.service.TripService;
import com.sharavel.sharavel_be.user.dto.UserSnippetDto;
import com.sharavel.sharavel_be.user.entity.Users;
import com.sharavel.sharavel_be.user.repository.UserRepository;

@Service
public class TripServiceImpl implements TripService {

	@Autowired
	private TripRepository tripRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserFollowRepository userFollowRepository;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private TripMapper tripMapper;

	@Override
	public TripDto createTrip(TripRequest tripDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("User is not authenticated");
		}

		String email = authentication.getName();
		Users user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalStateException("Create Trip User not found"));

		Trip trip = new Trip();
		trip.setTripUid(UUID.randomUUID().toString());
		trip.setUid(user);
		trip.setName(tripDto.getName());
		trip.setStartDate(tripDto.getStartDate());
		trip.setEndDate(tripDto.getEndDate());
		trip.setCompleted(tripDto.isCompleted());
		trip.setScripted(tripDto.getScripted()); // Default value

		List<Days> days = tripDto.getDays().stream().map(dayDto -> {
			Days day = new Days();
			day.setTripId(trip);
			day.setNumber(dayDto.getNumber());
			day.setLocations(new ArrayList<>()); // No locations at creation
			return day;
		}).collect(Collectors.toList());

		trip.setDays(days);
		Trip savedTrip = tripRepository.save(trip);

		return tripMapper.toDto(savedTrip);
	}

	@Override
	public void scriptTrip(String tripUuid) {
		Trip scriptedTrip = tripRepository.findByTripUid(tripUuid)
				.orElseThrow(() -> new RuntimeException("Script Trip Trip not found"));

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		Users user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalStateException("Script Trip User not found"));

		// add script to og trip
		scriptedTrip.setScripted(scriptedTrip.getScripted() + 1);
		tripRepository.save(scriptedTrip);

		Trip trip = new Trip();
		trip.setTripUid(UUID.randomUUID().toString());
		trip.setUid(user);
		trip.setName(scriptedTrip.getName());

		LocalDate today = LocalDate.now();
		int scriptedTripDays = (int) ChronoUnit.DAYS.between(scriptedTrip.getStartDate(), scriptedTrip.getEndDate());
		trip.setStartDate(today);
		trip.setEndDate(today.plusDays(scriptedTripDays));

		trip.setCompleted(false);
		trip.setCountries(scriptedTrip.getCountries());
		trip.setScripted(0L);

		List<Days> days = scriptedTrip.getDays().stream().map(originalDay -> {
			Days newDay = new Days();
			newDay.setTripId(trip);
			newDay.setNumber(originalDay.getNumber());

			List<Locations> newLocations = originalDay.getLocations().stream().map(originalLoc -> {
				Locations newLoc = new Locations();
				newLoc.setDayId(newDay);
				newLoc.setNumber(originalLoc.getNumber());
				newLoc.setName(originalLoc.getName());
				newLoc.setLongitude(originalLoc.getLongitude());
				newLoc.setLatitude(originalLoc.getLatitude());
				newLoc.setDescription(originalLoc.getDescription());
				return newLoc;
			}).collect(Collectors.toList());

			newDay.setLocations(newLocations);
			return newDay;
		}).collect(Collectors.toList());

		trip.setDays(days);

		tripRepository.save(trip);
	}

	@Override
	public TripResponse getTripByUuid(String tripUid) {
		Trip trip = tripRepository.findByTripUid(tripUid)
				.orElseThrow(() -> new RuntimeException("getTripByUuid Trip not found"));
		boolean isEditable = false;
		Users currentUser = null;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()
				&& !"anonymousUser".equals(authentication.getName())) {
			String email = authentication.getName();
			currentUser = userRepository.findByEmail(email)
					.orElse(null);
		}
		if (currentUser != null && trip.getUid().equals(currentUser)) {
			isEditable = true;
		}

		Users tripOwner = userRepository.findByUuid(trip.getUid().getUuid())
				.orElseThrow(() -> new IllegalStateException("getTripByUuid User not found"));

		boolean isFollowing = userFollowRepository.existsByFollowerIdAndFollowingId(currentUser.getId(), tripOwner.getId());

		UserSnippetDto userSnippet = new UserSnippetDto(tripOwner.getUuid(), tripOwner.getName(),
				tripOwner.getUsername(), isFollowing);

		TripResponse response = new TripResponse(tripMapper.toDto(trip), isEditable, userSnippet);

		return response;
	}

	@Override
	public List<TripListDto> getAllTrips() {
		List<Trip> allTrips = tripRepository.findAll(); // Trip 엔티티를 가져왔다고 가정

		List<TripListDto> filteredTrips = allTrips.stream()
				.map(trip -> (TripListDto) tripMapper.toListDto(trip))
				.collect(Collectors.toList());

		return filteredTrips;
	}

	@Override
	public List<TripListDto> getUserAllTrips(String userUuid) {
		Users user = userRepository.findByUuid(userUuid)
				.orElseThrow(() -> new IllegalStateException("getUserAllTrips User not found"));
		Long uid = user.getId();
		List<Trip> trips = tripRepository.findByUid_Id(uid);

		List<TripListDto> tripsListDto = trips.stream()
				.map(trip -> (TripListDto) tripMapper.toListDto(trip))
				.collect(Collectors.toList());

		return tripsListDto;
	}

	@Override
	public TripDto putUpdatedTrip(String tripUid, TripRequest updatedTrip) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("User is not authenticated");
		}

		String email = authentication.getName();
		Users user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalStateException("putUpdatedTrip User not found"));

		Trip trip = tripRepository.findByTripUid(tripUid)
				.orElseThrow(() -> new RuntimeException("putUpdatedTrip Trip not found"));

		Set<Country> countries = updatedTrip.getCountries().stream()
				.map(codeDto -> countryRepository.findByIso2(codeDto.getIso2())
						.orElseThrow(() -> new RuntimeException("Country not found: " + codeDto.getIso2())))
				.collect(Collectors.toSet());

		trip.setTripUid(trip.getTripUid());
		trip.setUid(user);
		trip.setName(updatedTrip.getName());

		// 기존 trip이 완료된 경우에만 totalTripDays와 totalTrips 감소
		if (trip.isCompleted()) {
			int currentTripDays = (int) ChronoUnit.DAYS.between(trip.getStartDate(), trip.getEndDate()) + 1;
			user.setTotalTripDays(user.getTotalTripDays() - currentTripDays);
			user.setTotalTrips(user.getTotalTrips() - 1);
		}

		LocalDate today = LocalDate.now();
		if (updatedTrip.getEndDate() != null && updatedTrip.getEndDate().isBefore(today.plusDays(1))) {
			int updatedTripDays = (int) ChronoUnit.DAYS.between(updatedTrip.getStartDate(), updatedTrip.getEndDate()) + 1;
			user.setTotalTripDays(user.getTotalTripDays() + updatedTripDays);
			user.setTotalTrips(user.getTotalTrips() + 1);
			trip.setCompleted(true);
		} else {
			trip.setCompleted(false);
		}

		trip.setStartDate(updatedTrip.getStartDate());
		trip.setEndDate(updatedTrip.getEndDate());
		trip.setCountries(countries);
		trip.setScripted(0L);

		Map<Integer, Days> existingDaysMap = trip.getDays().stream()
				.collect(Collectors.toMap(Days::getNumber, day -> day));
		List<Days> updatedDays = new ArrayList<>();

		for (DaysDto dayDto : updatedTrip.getDays()) {
			Days day = existingDaysMap.getOrDefault(dayDto.getNumber(), new Days());
			day.setTripId(trip);
			day.setNumber(dayDto.getNumber());

			Map<Integer, Locations> existingLocationsMap = day.getLocations().stream()
					.collect(Collectors.toMap(Locations::getNumber, loc -> loc));
			List<Locations> updatedLocations = new ArrayList<>();

			for (LocationDto locDto : dayDto.getLocations()) {
				Locations location = existingLocationsMap.getOrDefault(locDto.getNumber(), new Locations());
				location.setDayId(day);
				location.setNumber(locDto.getNumber());
				location.setName(locDto.getName());
				location.setLongitude(locDto.getLongitude());
				location.setLatitude(locDto.getLatitude());
				location.setDescription(locDto.getDescription());
				updatedLocations.add(location);
				existingLocationsMap.remove(locDto.getNumber()); // 처리된 항목 제거
			}

			// 삭제된 Location 제거
			// day.getLocations().removeAll(existingLocationsMap.values());
			day.getLocations().clear();
			day.getLocations().addAll(updatedLocations);
			updatedDays.add(day);
			existingDaysMap.remove(dayDto.getNumber()); // 처리된 항목 제거
		}

		// 삭제된 Day 제거
		// trip.getDays().removeAll(existingDaysMap.values());
		trip.getDays().clear();
		trip.getDays().addAll(updatedDays);

		if (trip.isCompleted()) {
			userRepository.save(user);
		}
		Trip savedTrip = tripRepository.save(trip);

		return tripMapper.toDto(savedTrip);
	}

	@Override
	public TripDto updateTripField(String tripUuid, Map<String, Object> updates) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'updateTripField'");
	}

	@Override
	public ResponseEntity<?> deleteTrip(String tripUuid) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("User is not authenticated");
		}

		String email = authentication.getName();
		Users user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalStateException("putUpdatedTrip User not found"));

		Trip trip = tripRepository.findByTripUid(tripUuid)
				.orElseThrow(() -> new RuntimeException("deleteTrip Trip not found"));

		int tripDays = (int) ChronoUnit.DAYS.between(trip.getStartDate(), trip.getEndDate()) + 1;
		user.setTotalTripDays(user.getTotalTripDays() - tripDays);
		user.setTotalTrips(user.getTotalTrips() - 1);

		userRepository.save(user);
		tripRepository.delete(trip);
		return ResponseEntity.ok("Deleted Trip");
		// throw new UnsupportedOperationException("Unimplemented method 'deleteTrip'");
	}

}