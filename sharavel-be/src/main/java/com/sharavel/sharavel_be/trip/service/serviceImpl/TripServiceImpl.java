package com.sharavel.sharavel_be.trip.service.serviceImpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
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
import com.sharavel.sharavel_be.s3bucket.dto.response.S3TripMediaResponse;
import com.sharavel.sharavel_be.s3bucket.entity.S3TripMedia;
import com.sharavel.sharavel_be.s3bucket.repository.S3TripMediaRepository;
import com.sharavel.sharavel_be.s3bucket.service.S3BucketService;
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
	private S3BucketService s3BucketService;

	@Autowired
	private S3TripMediaRepository s3TripMediaRepository;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private TripMapper tripMapper;

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
	public TripDto createTrip(TripRequest tripDto) {
		Users user = getCurrentUser();

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
			day.setTrip(trip);
			day.setNumber(dayDto.getNumber());
			day.setLocations(new ArrayList<>()); // No locations at creation
			return day;
		}).collect(Collectors.toList());

		trip.setDays(days);
		Trip savedTrip = tripRepository.save(trip);

		return tripMapper.toDto(savedTrip, user);
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
		Users user = getCurrentUser();

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

			if (user.getTotalTripDays() >= currentTripDays) {
				user.setTotalTripDays(user.getTotalTripDays() - currentTripDays);
			} else {
				user.setTotalTripDays(0);
			}

			if (user.getTotalTrips() > 0) {
				user.setTotalTrips(user.getTotalTrips() - 1);
			}
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

		Set<Long> mediaIdsToKeep = new HashSet<>(); // 이번 업데이트에서 유지될 미디어 ID (S3TripMedia.id)

		for (DaysDto dayDto : updatedTrip.getDays()) {
			Days day = existingDaysMap.getOrDefault(dayDto.getNumber(), new Days());
			day.setTrip(trip);
			day.setNumber(dayDto.getNumber());

			Map<Integer, Locations> existingLocationsMap = day.getLocations().stream()
					.collect(Collectors.toMap(Locations::getNumber, loc -> loc));
			List<Locations> updatedLocations = new ArrayList<>();

			for (LocationDto locDto : dayDto.getLocations()) {
				Locations location = existingLocationsMap.getOrDefault(locDto.getNumber(), new Locations());
				location.setDay(day);
				location.setNumber(locDto.getNumber());
				location.setName(locDto.getName());
				location.setLongitude(locDto.getLongitude());
				location.setLatitude(locDto.getLatitude());
				location.setDescription(locDto.getDescription());
				location.setCountryIso2(locDto.getCountryIso2());
				updatedLocations.add(location);
				existingLocationsMap.remove(locDto.getNumber());

				// --- Media (S3 Key) 관리 로직 ---
				// 현재 Location에 대한 모든 기존 미디어 엔티티를 가져옵니다.
				// 이는 이후에 ID를 사용하기 위함입니다.
				List<S3TripMedia> existingMediaEntities = s3TripMediaRepository
						.findByTripUidAndDayNumAndLocationNum(tripUid, dayDto.getNumber(), locDto.getNumber());

				// 1. 기존 미디어의 S3 키 맵 (빠른 조회를 위해)
				// Map<S3Key, S3TripMedia> 형태로 저장하여 ID를 쉽게 가져올 수 있도록 합니다.
				Map<String, S3TripMedia> existingS3KeyToMediaMap = existingMediaEntities.stream()
						.collect(Collectors.toMap(S3TripMedia::getS3Key, media -> media));

				// 2. 클라이언트에서 받은 DTO 미디어의 S3 키 세트
				Set<String> newMediaS3KeysFromClient = locDto.getMedia().stream()
						.map(S3TripMediaResponse::getS3Key)
						.collect(Collectors.toSet());

				// --- 미디어 변경 사항 감지 및 처리 ---

				// DB에서 삭제해야 할 미디어 (existingS3Keys에는 있지만 newMediaS3Keys에는 없는 것)
				// existingS3KeyToMediaMap의 keySet()을 사용하는 것이 좋습니다.
				Set<String> s3KeysToDelete = new HashSet<>(existingS3KeyToMediaMap.keySet());
				s3KeysToDelete.removeAll(newMediaS3KeysFromClient); // 클라이언트에 없는 키는 삭제 대상

				// DB에 새로 추가하거나 업데이트해야 할 미디어 (newMediaS3Keys에는 있지만 existingS3Keys에는 없는 것)
				Set<String> s3KeysToAddNewOrUpdate = new HashSet<>(newMediaS3KeysFromClient);
				s3KeysToAddNewOrUpdate.removeAll(existingS3KeyToMediaMap.keySet()); // 기존에 없는 키는 추가/업데이트 대상

				// 1. 삭제 대상 미디어 처리
				if (!s3KeysToDelete.isEmpty()) {
					List<S3TripMedia> mediaToDelete = s3KeysToDelete.stream()
							.map(existingS3KeyToMediaMap::get)
							.collect(Collectors.toList());
					// 예를 들어, soft delete를 하거나 관계를 끊는 로직
					s3TripMediaRepository.deleteAll(mediaToDelete); // 실제 삭제 시 사용
					// 또는 mediaToDelete.forEach(media -> media.setDeleted(true));
					// s3TripMediaRepository.saveAll(mediaToDelete);
					// 또는 mediaToDelete.forEach(media -> media.setLocationNum(null)); // 관계만 끊는 경우
				}

				// 2. 새로 추가되거나 업데이트될 미디어 처리
				for (String s3Key : s3KeysToAddNewOrUpdate) {
					// 이 s3Key는 클라이언트에서 이미 S3에 업로드하고 DB에 저장된 상태라고 가정
					// (별도의 /api/media/upload 엔드포인트를 통해 이미 처리됨)
					// 여기서는 해당 S3TripMedia 엔티티를 찾아서 Location과의 연관 관계를 맺어줍니다.
					S3TripMedia mediaEntry = s3TripMediaRepository.findByS3Key(s3Key)
							.orElseThrow(() -> new RuntimeException("Media entry not found for S3 key: " + s3Key));

					// mediaEntry가 이미 올바른 tripUid, dayNum, locNum을 가지고 있는지 확인 (옵션)
					// 만약 S3 업로드 시점에 이 정보들이 이미 설정된다면 이 부분은 필요 없을 수 있습니다.
					// 현재 로직상 미디어가 다른 Location에 속했다가 이 Location으로 옮겨지는 경우를 처리합니다.
					if (!mediaEntry.getTripUid().equals(tripUid) ||
							!mediaEntry.getDayNum().equals(dayDto.getNumber()) ||
							!mediaEntry.getLocationNum().equals(locDto.getNumber())) {
						mediaEntry.setTripUid(tripUid);
						mediaEntry.setDayNum(dayDto.getNumber());
						mediaEntry.setLocationNum(locDto.getNumber());
						s3TripMediaRepository.save(mediaEntry); // 변경 사항 저장
					}
					mediaIdsToKeep.add(mediaEntry.getId()); // 유지될 미디어 목록에 추가
				}

				// 3. 기존에 존재하며 유지될 미디어 식별
				// 클라이언트에서 보낸 S3 키 중 기존 DB에도 있는 키들을 찾습니다.
				Set<String> s3KeysToKeep = new HashSet<>(newMediaS3KeysFromClient);
				s3KeysToKeep.retainAll(existingS3KeyToMediaMap.keySet()); // 교집합 연산

				for (String s3Key : s3KeysToKeep) {
					S3TripMedia mediaEntry = existingS3KeyToMediaMap.get(s3Key);
					if (mediaEntry != null) {
						mediaIdsToKeep.add(mediaEntry.getId()); // 유지될 미디어 목록에 추가
					}
				}

				// 이후 mediaIdsToKeep을 활용하여 Location과 S3TripMedia 간의 관계를 업데이트하거나
				// 다른 필요한 로직을 수행할 수 있습니다.
				// 예를 들어, 특정 Location의 모든 S3TripMedia를 이 mediaIdsToKeep 목록으로 동기화할 수 있습니다.
			}

			// 삭제된 Location 제거
			// day.getLocations().removeAll(existingLocationsMap.values());
			day.getLocations().clear();
			day.getLocations().addAll(updatedLocations);
			updatedDays.add(day);
			existingDaysMap.remove(dayDto.getNumber());
		}

		// 삭제된 Day 제거
		// trip.getDays().removeAll(existingDaysMap.values());
		trip.getDays().clear();
		trip.getDays().addAll(updatedDays);

		// --- S3TripMedia 테이블에서 더 이상 참조되지 않는 미디어 삭제 ---
		// 해당 Trip에 속하면서도 이번 업데이트에서 유지 목록에 없는 미디어들을 찾습니다.
		// List<S3TripMedia> allTripMedia =
		// s3TripMediaRepository.findByTripUid(tripUid);
		// List<S3TripMedia> mediaToDelete = allTripMedia.stream()
		// .filter(media -> !mediaIdsToKeep.contains(media.getId()))
		// .collect(Collectors.toList());

		// logger.debug("mediaToDelete: " + mediaToDelete);
		// for (S3TripMedia media : mediaToDelete) {
		// // S3에서 실제 파일 삭제 (S3UploadService에 deleteFileByS3Key 메서드 추가)
		// s3BucketService.deleteFile(media.getS3Key());
		// }
		// -------------------------------------------------------------
		if (trip.isCompleted()) {
			userRepository.save(user);
		}
		Trip savedTrip = tripRepository.save(trip);
		return tripMapper.toDto(savedTrip, user);
	}

	@Override
	public TripDto updateTripField(String tripUuid, Map<String, Object> updates) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'updateTripField'");
	}

	@Override
	public ResponseEntity<?> deleteTrip(String tripUuid) {
		Users user = getCurrentUser();

		Trip trip = tripRepository.findByTripUid(tripUuid)
				.orElseThrow(() -> new RuntimeException("deleteTrip Trip not found"));

		int tripDays = (int) ChronoUnit.DAYS.between(trip.getStartDate(), trip.getEndDate()) + 1;
		user.setTotalTripDays(user.getTotalTripDays() - tripDays);
		user.setTotalTrips(user.getTotalTrips() - 1);

		userRepository.save(user);
		tripRepository.delete(trip);
		return ResponseEntity.ok("Deleted Trip");
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
}