package com.sharavel.sharavel_be.trip.utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sharavel.sharavel_be.countries.dto.CountryCodeDto;
import com.sharavel.sharavel_be.countries.entity.Country;
import com.sharavel.sharavel_be.countries.repository.CountryRepository;
import com.sharavel.sharavel_be.s3bucket.dto.response.S3TripMediaResponse;
import com.sharavel.sharavel_be.s3bucket.entity.S3TripMedia;
import com.sharavel.sharavel_be.s3bucket.repository.S3TripMediaRepository;
import com.sharavel.sharavel_be.trip.dto.DaysDto;
import com.sharavel.sharavel_be.trip.dto.LocationDto;
import com.sharavel.sharavel_be.trip.dto.response.TripRequest;
import com.sharavel.sharavel_be.trip.entity.Days;
import com.sharavel.sharavel_be.trip.entity.Locations;
import com.sharavel.sharavel_be.trip.entity.Trip;
import com.sharavel.sharavel_be.user.entity.Users;

@Component
public class TripHelper {

	private static final Logger logger = LoggerFactory.getLogger(TripHelper.class);

	private final CountryRepository countryRepository;
	private final S3TripMediaRepository s3TripMediaRepository;
	// 필요한 경우 DaysRepository, LocationsRepository 추가

	public TripHelper(CountryRepository countryRepository, S3TripMediaRepository s3TripMediaRepository) {
		this.countryRepository = countryRepository;
		this.s3TripMediaRepository = s3TripMediaRepository;
	}

	/**
	 * Trip 엔티티의 핵심 필드를 업데이트합니다.
	 * 이 메서드는 트랜잭션의 일부로 호출될 것이므로 별도의 @Transactional은 필요 없습니다.
	 * (혹은 Propagation.REQUIRES_NEW 등으로 별도 트랜잭션 필요 시 설정)
	 * 
	 * @param trip    업데이트될 Trip 엔티티
	 * @param request TripRequest DTO
	 */
	public void updateTripCoreFields(Trip trip, TripRequest request) {
		trip.setName(request.getName());
		trip.setStartDate(request.getStartDate());
		trip.setEndDate(request.getEndDate());
		// isCompleted는 사용자 통계 로직에 따라 결정되므로 여기서 직접 설정하지 않습니다.
	}

	/**
	 * 사용자의 총 여행 일수와 총 여행 횟수를 업데이트 전 처리합니다.
	 * (기존 여행이 완료 상태였다면 통계에서 차감)
	 * 
	 * @param trip 현재 Trip 엔티티
	 * @param user 현재 Users 엔티티
	 */
	public void handleUserStatsBeforeUpdate(Trip trip, Users user) {
		if (trip.isCompleted() && trip.getStartDate() != null && trip.getEndDate() != null) {
			// Calculate days between startDate and endDate (inclusive)
			long daysBetween = ChronoUnit.DAYS.between(trip.getStartDate(), trip.getEndDate()) + 1;

			// Check for Integer overflow
			if (daysBetween > Integer.MAX_VALUE) {
				throw new IllegalStateException("Trip duration exceeds maximum Integer value: " + daysBetween);
			}

			// Explicitly cast to Integer
			Integer currentTripDays = Math.toIntExact(daysBetween);

			// Handle null for getTotalTripDays and getTotalTrips
			Integer currentTotalTripDays = user.getTotalTripDays();
			Integer currentTotalTrips = user.getTotalTrips();

			// Subtract days and trips, ensuring non-negative results
			user.updateTotalTripDays(Math.max(0, currentTotalTripDays - currentTripDays), user.getUuid());
			user.updateTotalTrips(Math.max(0, currentTotalTrips - 1), user.getUuid());
		}
	}

	/**
	 * 사용자의 총 여행 일수와 총 여행 횟수를 업데이트 후 처리합니다.
	 * (새로운 업데이트 상태가 완료라면 통계에 추가)
	 * 
	 * @param trip 현재 Trip 엔티티
	 * @param user 현재 Users 엔티티
	 */
	public void handleUserStatsAfterUpdate(Trip trip, Users user) {
		LocalDate today = LocalDate.now();
		// Null check for endDate and startDate
		if (trip.getEndDate() != null && trip.getStartDate() != null && !trip.getEndDate().isAfter(today)) {
			// Calculate days between startDate and endDate (inclusive)
			long daysBetween = ChronoUnit.DAYS.between(trip.getStartDate(), trip.getEndDate()) + 1;

			// Check for Integer overflow
			if (daysBetween > Integer.MAX_VALUE) {
				throw new IllegalStateException("Trip duration exceeds maximum Integer value: " + daysBetween);
			}

			// Explicitly cast to Integer
			Integer updatedTripDays = Math.toIntExact(daysBetween);

			// Update user stats (handle null for getTotalTripDays and getTotalTrips)
			Integer currentTotalTripDays = user.getTotalTripDays();
			Integer currentTotalTrips = user.getTotalTrips();

			user.updateTotalTripDays((currentTotalTripDays + updatedTripDays), user.getUuid());
			user.updateTotalTrips((currentTotalTrips + 1), user.getUuid());
			trip.setCompleted(true);
		} else {
			trip.setCompleted(false);
		}
	}

	/**
	 * Trip의 Countries를 업데이트합니다.
	 * 
	 * @param trip            업데이트될 Trip 엔티티
	 * @param countryCodeDtos DTO에서 받은 국가 코드 리스트
	 */
	public void updateCountries(Trip trip, List<CountryCodeDto> countryCodeDtos) {
		if (countryCodeDtos == null) {
			trip.setCountries(new HashSet<>());
			return;
		}
		Set<Country> countries = countryCodeDtos.stream()
				.map(codeDto -> countryRepository.findByIso2(codeDto.getIso2())
						.orElseThrow(() -> new NoSuchElementException("Country not found: " + codeDto.getIso2())))
				.collect(Collectors.toSet());
		trip.setCountries(countries);
	}

	/**
	 * Trip의 Days 및 Locations를 업데이트하고 변경 사항을 반영합니다.
	 *
	 * @param trip                     업데이트될 Trip 엔티티
	 * @param dayDtos                  DTO에서 받은 Day 리스트
	 * @param mediaIdsToKeepAcrossTrip 전체 트립에서 유지될 미디어 ID 세트 (반환 값 또는
	 *                                 out-parameter처럼 사용)
	 */
	public void updateDaysAndLocations(Trip trip, List<DaysDto> dayDtos, Set<Long> mediaIdsToKeepAcrossTrip) {
		Map<Integer, Days> existingDaysMap = trip.getDays().stream()
				.collect(Collectors.toMap(Days::getNumber, day -> day));
		List<Days> newOrUpdatedDays = new ArrayList<>();

		for (DaysDto dayDto : dayDtos) {
			Days day = existingDaysMap.getOrDefault(dayDto.getNumber(), new Days());
			day.setTrip(trip);
			day.setNumber(dayDto.getNumber());

			updateLocationsForDay(day, dayDto.getLocations(), trip.getTripUid(), dayDto.getNumber(),
					mediaIdsToKeepAcrossTrip);

			newOrUpdatedDays.add(day);
			existingDaysMap.remove(dayDto.getNumber());
		}

		// DTO에 없는 기존 Day는 삭제 처리 (JPA CascadeType.ALL이 설정되어 있다면 자식들도 삭제됨)
		trip.getDays().clear();
		trip.getDays().addAll(newOrUpdatedDays);
	}

	/**
	 * 단일 Day 내의 Locations를 업데이트하고 변경 사항을 반영합니다.
	 *
	 * @param day                      업데이트될 Days 엔티티
	 * @param locationDtos             DTO에서 받은 Location 리스트
	 * @param tripUid                  현재 Trip UID (미디어 관리용)
	 * @param dayNum                   현재 Day Number (미디어 관리용)
	 * @param mediaIdsToKeepAcrossTrip 전체 트립에서 유지될 미디어 ID 세트
	 */
	private void updateLocationsForDay(Days day, List<LocationDto> locationDtos, String tripUid, Integer dayNum,
			Set<Long> mediaIdsToKeepAcrossTrip) {
		Map<Integer, Locations> existingLocationsMap = day.getLocations().stream()
				.collect(Collectors.toMap(Locations::getNumber, loc -> loc));
		List<Locations> newOrUpdatedLocations = new ArrayList<>();

		for (LocationDto locDto : locationDtos) {
			Locations location = existingLocationsMap.getOrDefault(locDto.getNumber(), new Locations());
			location.setDay(day);
			location.setNumber(locDto.getNumber());
			location.setName(locDto.getName());
			location.setLongitude(locDto.getLongitude());
			location.setLatitude(locDto.getLatitude());
			location.setDescription(locDto.getDescription());
			location.setCountryIso2(locDto.getCountryIso2());

			updateS3TripMediaForLocation(tripUid, dayNum, locDto.getNumber(), locDto.getMedia(), mediaIdsToKeepAcrossTrip);

			newOrUpdatedLocations.add(location);
			existingLocationsMap.remove(locDto.getNumber());
		}

		day.getLocations().clear();
		day.getLocations().addAll(newOrUpdatedLocations);
	}

	/**
	 * Location에 대한 S3TripMedia를 업데이트합니다.
	 *
	 * @param tripUid                  현재 Trip UID
	 * @param dayNum                   현재 Day Number
	 * @param locationNum              현재 Location Number
	 * @param mediaDtos                Location DTO에서 받은 미디어 리스트
	 * @param mediaIdsToKeepAcrossTrip 전체 트립에서 유지될 미디어 ID 세트
	 */
	private void updateS3TripMediaForLocation(String tripUid, Integer dayNum, Integer locationNum,
			List<S3TripMediaResponse> mediaDtos, Set<Long> mediaIdsToKeepAcrossTrip) {

		List<S3TripMedia> existingMediaEntities = s3TripMediaRepository
				.findByTripUidAndDayNumAndLocationNum(tripUid, dayNum, locationNum);
		Map<String, S3TripMedia> existingS3KeyToMediaMap = existingMediaEntities.stream()
				.collect(Collectors.toMap(S3TripMedia::getS3Key, media -> media));

		Set<String> clientS3Keys = (mediaDtos != null)
				? mediaDtos.stream().map(S3TripMediaResponse::getS3Key).collect(Collectors.toSet())
				: Collections.emptySet();

		Set<String> s3KeysToDelete = new HashSet<>(existingS3KeyToMediaMap.keySet());
		s3KeysToDelete.removeAll(clientS3Keys);

		Set<String> s3KeysToConnectOrUpdate = new HashSet<>(clientS3Keys);
		s3KeysToConnectOrUpdate.removeAll(existingS3KeyToMediaMap.keySet());

		if (!s3KeysToDelete.isEmpty()) {
			List<S3TripMedia> mediaToDisconnect = s3KeysToDelete.stream()
					.map(existingS3KeyToMediaMap::get)
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
			mediaToDisconnect.forEach(media -> {
				media.setDayNum(null);
				media.setLocationNum(null);
			});
			s3TripMediaRepository.saveAll(mediaToDisconnect);
		}

		for (String s3Key : s3KeysToConnectOrUpdate) {
			S3TripMedia mediaEntry = s3TripMediaRepository.findByS3Key(s3Key)
					.orElseThrow(() -> new NoSuchElementException("Media entry not found for S3 key: " + s3Key));
			mediaEntry.setTripUid(tripUid);
			mediaEntry.setDayNum(dayNum);
			mediaEntry.setLocationNum(locationNum);
			s3TripMediaRepository.save(mediaEntry);
		}

		clientS3Keys.forEach(s3Key -> {
			S3TripMedia mediaEntry = existingS3KeyToMediaMap.get(s3Key);
			if (mediaEntry != null) {
				mediaIdsToKeepAcrossTrip.add(mediaEntry.getId());
			} else {
				s3TripMediaRepository.findByS3Key(s3Key).ifPresent(m -> mediaIdsToKeepAcrossTrip.add(m.getId()));
			}
		});
	}

	/**
	 * 주어진 TripUid에 대해 더 이상 어떤 Location에도 참조되지 않는 S3TripMedia 엔티티를 삭제합니다.
	 * S3 버킷에서 실제 파일 삭제도 포함될 수 있습니다.
	 *
	 * @param tripUid                  현재 Trip UID
	 * @param mediaIdsToKeepAcrossTrip 업데이트 후 유지되어야 할 모든 미디어 ID 목록
	 */
	// @Transactional(propagation = Propagation.REQUIRES_NEW) // 별도 트랜잭션으로 실행될 수 있도록
	// (옵션)
	// public void deleteUnreferencedMedia(String tripUid, Set<Long>
	// mediaIdsToKeepAcrossTrip) {
	// List<S3TripMedia> allTripMedia =
	// s3TripMediaRepository.findByTripUid(tripUid);

	// List<S3TripMedia> mediaToDeletePermanently = allTripMedia.stream()
	// .filter(media -> !mediaIdsToKeepAcrossTrip.contains(media.getId()))
	// .filter(media -> media.getLocationNum() == null && media.getDayNum() == null)
	// .collect(Collectors.toList());

	// logger.debug("Deleting unreferenced media entities: " +
	// mediaToDeletePermanently.size());

	// for (S3TripMedia media : mediaToDeletePermanently) {
	// // S3에서 실제 파일 삭제 로직 추가 (s3BucketService.deleteFile(media.getS3Key());)
	// s3TripMediaRepository.delete(media);
	// }
	// }
}