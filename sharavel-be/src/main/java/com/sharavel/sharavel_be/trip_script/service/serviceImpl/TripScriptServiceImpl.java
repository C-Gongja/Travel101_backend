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
import com.sharavel.sharavel_be.trip_script.dto.request.ScriptDayRequestDto;
import com.sharavel.sharavel_be.trip_script.dto.request.ScriptLocationRequestDto;
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
				newLoc.setCountryIso2(originalLoc.getCountryIso2());
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
	public void scriptDay(ScriptDayRequestDto request) {
		Users user = getCurrentUser();

		Days originalDay = daysRepository.findByTrip_TripUidAndNumber(request.getTripUid(), request.getDayNum());
		Trip targetTrip = tripRepository.findByTripUid(request.getTargetTripUid())
				.orElseThrow(() -> new RuntimeException("Trip not found"));

		List<Days> targetDays = targetTrip.getDays();

		int nextDayNumber = 1;

		if (!targetDays.isEmpty()) {
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
			newLoc.setCountryIso2(loc.getCountryIso2());
			return newLoc;
		}).collect(Collectors.toList());

		copiedDay.setLocations(copiedLocations);
		// 기존 days에 추가
		targetTrip.getDays().add(copiedDay);

		// TripScript 생성
		TripScript script = new TripScript();
		script.setType("DAY");
		script.setScriptUser(user);
		script.setDay(originalDay);
		script.setScriptedAt(LocalDateTime.now());
		tripScriptRepository.save(script);

		// endDate를 하루 늘림
		targetTrip.setEndDate(targetTrip.getEndDate().plusDays(1));

		tripRepository.save(targetTrip);
		daysRepository.save(copiedDay);
		locationRepository.saveAll(copiedLocations);
	}

	@Override
	public void scriptLocation(ScriptLocationRequestDto request) {
		System.out.println(request.toString());
		Users user = getCurrentUser();

		Days originalDay = daysRepository.findByTrip_TripUidAndNumber(request.getTripUid(), request.getDayNum());
		if (originalDay == null) {
			throw new RuntimeException("Original Day not found");
		}

		Locations ogLoc = locationRepository.findByDayAndNumber(originalDay, request.getLocNum())
				.orElseThrow(() -> new RuntimeException("Original Location not found"));

		Trip targetTrip = tripRepository.findByTripUid(request.getTargetTripUid())
				.orElseThrow(() -> new RuntimeException("Trip not found"));

		Days targetDay = daysRepository.findByTrip_TripUidAndNumber(request.getTargetTripUid(), request.getTargetDayNum());
		if (targetDay == null) {
			throw new RuntimeException("Target Day not found");
		}

		// 3. 붙여넣을 위치 번호 계산
		int nextLocNumber = targetDay.getLocations().stream()
				.mapToInt(Locations::getNumber)
				.max()
				.orElse(0) + 1;

		Locations newLoc = new Locations();
		newLoc.setDay(targetDay);
		newLoc.setNumber(nextLocNumber);
		newLoc.setName(ogLoc.getName());
		newLoc.setLongitude(ogLoc.getLongitude());
		newLoc.setLatitude(ogLoc.getLatitude());
		newLoc.setDescription(ogLoc.getDescription());
		newLoc.setCountryIso2(ogLoc.getCountryIso2());

		locationRepository.save(newLoc);
		targetDay.getLocations().add(newLoc);

		// TripScript 생성
		TripScript script = new TripScript();
		script.setType("LOCATION");
		script.setScriptUser(user);
		script.setLocation(newLoc);
		script.setScriptedAt(LocalDateTime.now());

		tripRepository.save(targetTrip);
	}
}
