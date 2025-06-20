package com.sharavel.sharavel_be.trip.service.serviceImpl;

import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sharavel.sharavel_be.follow.repository.UserFollowRepository;
import com.sharavel.sharavel_be.trip.dto.TripDto;
import com.sharavel.sharavel_be.trip.dto.TripListDto;
import com.sharavel.sharavel_be.trip.dto.response.TripRequest;
import com.sharavel.sharavel_be.trip.dto.response.TripResponse;
import com.sharavel.sharavel_be.trip.entity.Trip;
import com.sharavel.sharavel_be.trip.mapper.TripMapper;
import com.sharavel.sharavel_be.trip.repository.TripRepository;
import com.sharavel.sharavel_be.trip.service.TripService;
import com.sharavel.sharavel_be.trip.utils.TripHelper;
import com.sharavel.sharavel_be.user.dto.UserSnippetDto;
import com.sharavel.sharavel_be.user.entity.Users;
import com.sharavel.sharavel_be.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Slf4j
public class TripServiceImpl implements TripService {

	@Autowired
	private TripRepository tripRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserFollowRepository userFollowRepository;

	@Autowired
	private TripMapper tripMapper;

	@Autowired
	private TripHelper tripHelper;

	final static Logger logger = LoggerFactory.getLogger(TripServiceImpl.class);

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
	public TripResponse createTrip(TripRequest tripRequest) {
		Users user = getCurrentUser();

		Trip trip = new Trip();
		trip.setTripUid(UUID.randomUUID().toString());
		trip.setUid(user);
		trip.setScripted(0L);

		tripHelper.updateTripCoreFields(trip, tripRequest); // 핵심 필드 업데이트
		tripHelper.updateCountries(trip, tripRequest.getCountries()); // 국가 정보 설정
		// 미디어 ID 추적을 위한 Set (createTrip에서는 보통 비어있음)
		Set<Long> mediaIdsToKeep = new HashSet<>();
		tripHelper.updateDaysAndLocations(trip, tripRequest.getDays(), mediaIdsToKeep); // Days 및 Locations 업데이트

		tripHelper.handleUserStatsAfterUpdate(trip, user); // 업데이트 후 사용자 통계 처리 (여기서 user save)

		userRepository.save(user);
		Trip savedTrip = tripRepository.save(trip);
		// 생성 후에는 unreferenced media가 없을 것이므로 deleteUnreferencedMedia 호출은 보통 생략
		UserSnippetDto userSnippet = new UserSnippetDto(user.getUuid(), user.getName(),
				user.getUsername(), false);

		TripResponse response = new TripResponse(tripMapper.toDto(savedTrip, user), true, userSnippet);

		return response;
	}

	@Override
	public TripResponse getTripByUuid(String tripUid) {
		boolean isEditable = false;
		boolean isFollowing = false;
		Users currentUser = null;

		Trip trip = tripRepository.findByTripUid(tripUid)
				.orElseThrow(() -> new RuntimeException("getTripByUuid Trip not found"));

		Users tripOwner = userRepository.findByUuid(trip.getUid().getUuid())
				.orElseThrow(() -> new IllegalStateException("getTripByUuid User not found"));

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()
				&& !"anonymousUser".equals(authentication.getName())) {
			String email = authentication.getName();
			currentUser = userRepository.findByEmail(email)
					.orElse(null);

			isFollowing = userFollowRepository.existsByFollowerIdAndFollowingId(currentUser.getId(), tripOwner.getId());
		}
		if (currentUser != null && trip.getUid().equals(currentUser)) {
			isEditable = true;
		}

		UserSnippetDto userSnippet = new UserSnippetDto(tripOwner.getUuid(), tripOwner.getName(),
				tripOwner.getUsername(), isFollowing);

		TripResponse response = new TripResponse(tripMapper.toDto(trip, currentUser), isEditable, userSnippet);

		return response;
	}

	@Override
	@Transactional
	public TripResponse putUpdatedTrip(String tripUid, TripRequest updatedTripRequest) {
		Users user = getCurrentUser();

		Trip trip = tripRepository.findByTripUid(tripUid)
				.orElseThrow(() -> new NoSuchElementException("Trip not found with uid: " + tripUid));

		tripHelper.handleUserStatsBeforeUpdate(trip, user); // 업데이트 전 사용자 통계 처리

		tripHelper.updateTripCoreFields(trip, updatedTripRequest); // 핵심 필드 업데이트
		tripHelper.updateCountries(trip, updatedTripRequest.getCountries()); // 국가 정보 업데이트

		Set<Long> mediaIdsToKeep = new HashSet<>(); // 유지될 미디어 ID 추적
		tripHelper.updateDaysAndLocations(trip, updatedTripRequest.getDays(), mediaIdsToKeep); // Days 및 Locations 업데이트

		tripHelper.handleUserStatsAfterUpdate(trip, user); // 업데이트 후 사용자 통계 처리 (여기서 user save)

		userRepository.save(user);
		Trip savedTrip = tripRepository.save(trip);

		// 생성 후에는 unreferenced media가 없을 것이므로 deleteUnreferencedMedia 호출은 보통 생략
		UserSnippetDto userSnippet = new UserSnippetDto(user.getUuid(), user.getName(),
				user.getUsername(), false);

		TripResponse response = new TripResponse(tripMapper.toDto(savedTrip, user), true, userSnippet);

		// 업데이트 후 더 이상 참조되지 않는 미디어 삭제
		// 별도 트랜잭션으로 미디어 삭제를 원하면 tripHelper의 deleteUnreferencedMedia에
		// @Transactional(propagation = Propagation.REQUIRES_NEW) 추가
		// tripHelper.deleteUnreferencedMedia(tripUid, mediaIdsToKeep);

		return response;
	}

	// scripted 된 trip은 삭제에 문제가있음
	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11
	@Override
	public ResponseEntity<?> deleteTrip(String tripUuid) {
		Users user = getCurrentUser();
		Trip trip = tripRepository.findByTripUid(tripUuid)
				.orElseThrow(() -> new RuntimeException("deleteTrip Trip not found"));

		// 0 이하로 가지않게
		// int tripDays = (int) ChronoUnit.DAYS.between(trip.getStartDate(),
		// trip.getEndDate()) + 1;
		// user.setTotalTripDays(user.getTotalTripDays() - tripDays);
		// user.setTotalTrips(user.getTotalTrips() - 1);
		tripHelper.handleUserStatsBeforeUpdate(trip, user); // 업데이트 전 사용자 통계 처리

		userRepository.save(user);
		tripRepository.delete(trip);

		return ResponseEntity.ok("Deleted Trip");
	}

	@Override
	public TripDto updateTripField(String tripUuid, Map<String, Object> updates) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'updateTripField'");
	}

	@Override
	public List<TripDto> getCloneTripsList(String userUid) {
		Users user = userRepository.findByUuid(userUid)
				.orElseThrow(() -> new IllegalStateException("getUserAllTrips User not found"));
		Long uid = user.getId();
		List<Trip> userTrips = tripRepository.findByUid_Id(uid);

		List<TripDto> userTripsDto = userTrips.stream()
				.map(trip -> (TripDto) tripMapper.toCloneTripDto(trip))
				.collect(Collectors.toList());

		return userTripsDto;
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
}