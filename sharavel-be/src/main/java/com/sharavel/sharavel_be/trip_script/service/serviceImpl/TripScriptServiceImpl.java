package com.sharavel.sharavel_be.trip_script.service.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.sharavel.sharavel_be.countries.entity.Country;
import com.sharavel.sharavel_be.countries.repository.CountryRepository;
import com.sharavel.sharavel_be.trip.entity.Days;
import com.sharavel.sharavel_be.trip.entity.Locations;
import com.sharavel.sharavel_be.trip.entity.Trip;
import com.sharavel.sharavel_be.trip.repository.DaysRepository;
import com.sharavel.sharavel_be.trip.repository.LocationsRepository;
import com.sharavel.sharavel_be.trip.repository.TripRepository;
import com.sharavel.sharavel_be.trip_script.entity.TripScript;
import com.sharavel.sharavel_be.trip_script.repository.TripScriptRepository;
import com.sharavel.sharavel_be.trip_script.service.TripScriptService;
import com.sharavel.sharavel_be.user.entity.Users;
import com.sharavel.sharavel_be.user.repository.UserRepository;

@Service
public class TripScriptServiceImpl implements TripScriptService {

	@Autowired
	private TripRepository tripRepository;

	@Autowired
	private DaysRepository daysRepository;

	@Autowired
	private LocationsRepository locationRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private TripScriptRepository tripScriptRepository;

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
	public void scriptTrip(String tripUuid) {
		Trip originalTrip = tripRepository.findByTripUid(tripUuid)
				.orElseThrow(() -> new RuntimeException("Script Trip Trip not found"));

		Users user = getCurrentUser();

		// add script to og trip
		originalTrip.setScripted(originalTrip.getScripted() + 1);
		tripRepository.save(originalTrip);

		Trip trip = new Trip();
		trip.setTripUid(UUID.randomUUID().toString());
		trip.setUid(user);
		trip.setName(originalTrip.getName());

		Set<Country> countries = originalTrip.getCountries().stream()
				.map(codeDto -> countryRepository.findByIso2(codeDto.getIso2())
						.orElseThrow(() -> new RuntimeException("Country not found: " + codeDto.getIso2())))
				.collect(Collectors.toSet());

		LocalDate today = LocalDate.now();
		int originalTripDays = (int) ChronoUnit.DAYS.between(originalTrip.getStartDate(), originalTrip.getEndDate());
		trip.setStartDate(today);
		trip.setEndDate(today.plusDays(originalTripDays));

		trip.setCompleted(false);
		trip.setCountries(countries);
		trip.setScripted(0L);

		List<Days> days = originalTrip.getDays().stream().map(originalDay -> {
			Days newDay = new Days();
			newDay.setTrip(trip);
			newDay.setNumber(originalDay.getNumber());

			List<Locations> newLocations = originalDay.getLocations().stream().map(originalLoc -> {
				Locations newLoc = new Locations();
				newLoc.setDay(newDay);
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

		// TripScript 생성
		TripScript script = new TripScript();
		script.setType("TRIP");
		script.setTrip(originalTrip);
		script.setScriptUser(user);
		script.setScriptedAt(LocalDateTime.now());

		tripRepository.save(trip);
		tripScriptRepository.save(script);
	}

	@Override
	public Long getScriptedCount(String tripUid) {
		Trip originalTrip = tripRepository.findByTripUid(tripUid)
				.orElseThrow(() -> new RuntimeException("Trip not found"));
		Long count = tripScriptRepository.countByTrip(originalTrip);
		return count;
	}

	@Override
	public void scriptDay(String tripUid, Integer dayNum, String targetTripUid) {
		Users user = getCurrentUser();

		Days originalDay = daysRepository.findByTrip_UidAndNumber(tripUid, dayNum);
		Trip targetTrip = tripRepository.findByTripUid(targetTripUid)
				.orElseThrow(() -> new RuntimeException("Trip not found"));

		List<Days> targetDays = targetTrip.getDays();

		int nextDayNumber = 1; // 기본값: 아무 day도 없을 경우

		if (!targetDays.isEmpty()) {
			// day 번호들 중 가장 큰 값을 찾음
			nextDayNumber = targetDays.stream()
					.mapToInt(Days::getNumber)
					.max()
					.orElse(0) + 1;
		}

		// 새 Days 객체 생성
		Days copiedDay = new Days();
		copiedDay.setNumber(nextDayNumber); // 원래 dayNum 그대로 쓰거나 변경 가능
		copiedDay.setTrip(targetTrip); // 대상 trip 설정

		// Location들도 복사
		List<Locations> copiedLocations = originalDay.getLocations().stream().map(loc -> {
			Locations newLoc = new Locations();
			newLoc.setDay(copiedDay);
			newLoc.setNumber(loc.getNumber());
			newLoc.setName(loc.getName());
			newLoc.setLongitude(loc.getLongitude());
			newLoc.setLatitude(loc.getLatitude());
			newLoc.setDescription(loc.getDescription());
			return newLoc;
		}).collect(Collectors.toList());

		copiedDay.setLocations(copiedLocations);

		// TripScript 생성
		TripScript script = new TripScript();
		script.setType("DAY");
		script.setScriptUser(user);
		script.setDay(originalDay);
		script.setScriptedAt(LocalDateTime.now());

		// 기존 days에 추가
		targetTrip.getDays().add(copiedDay);
		tripRepository.save(targetTrip);
	}

	@Override
	public void scriptLocation(String tripUid, Integer dayNum, Integer locNum, String targetTripUid,
			Integer targetDayNum) {
		Users user = getCurrentUser();

		Days originalDay = daysRepository.findByTrip_UidAndNumber(tripUid, dayNum);
		Locations ogLoc = locationRepository.findByDay(originalDay);

		Trip targetTrip = tripRepository.findByTripUid(targetTripUid)
				.orElseThrow(() -> new RuntimeException("Trip not found"));
		Days targetDay = daysRepository.findByTrip_UidAndNumber(targetTrip.getTripUid(), targetDayNum);

		List<Locations> targetLoc = targetDay.getLocations();

		int nextDLocNumber = 1; // 기본값: 아무 day도 없을 경우

		if (!targetLoc.isEmpty()) {
			// day 번호들 중 가장 큰 값을 찾음
			nextDLocNumber = targetLoc.stream()
					.mapToInt(Locations::getNumber)
					.max()
					.orElse(0) + 1;
		}

		Locations newLoc = new Locations();
		newLoc.setDay(targetDay);
		newLoc.setNumber(nextDLocNumber);
		newLoc.setName(ogLoc.getName());
		newLoc.setLongitude(ogLoc.getLongitude());
		newLoc.setLatitude(ogLoc.getLatitude());
		newLoc.setDescription(ogLoc.getDescription());

		// TripScript 생성
		TripScript script = new TripScript();
		script.setType("LOCATION");
		script.setScriptUser(user);
		script.setLocation(newLoc);
		script.setScriptedAt(LocalDateTime.now());
	}
}
