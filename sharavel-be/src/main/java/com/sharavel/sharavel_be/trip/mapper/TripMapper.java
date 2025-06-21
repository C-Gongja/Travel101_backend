package com.sharavel.sharavel_be.trip.mapper;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sharavel.sharavel_be.comments.repository.CommentRepository;
import com.sharavel.sharavel_be.countries.dto.CountryDto;
import com.sharavel.sharavel_be.likes.repository.LikesRepository;
import com.sharavel.sharavel_be.s3bucket.dto.response.S3TripMediaResponse;
import com.sharavel.sharavel_be.s3bucket.entity.S3TripMedia;
import com.sharavel.sharavel_be.s3bucket.repository.S3TripMediaRepository;
import com.sharavel.sharavel_be.s3bucket.service.S3TripService;
import com.sharavel.sharavel_be.trip.dto.DaysDto;
import com.sharavel.sharavel_be.trip.dto.LocationDto;
import com.sharavel.sharavel_be.trip.dto.TripDto;
import com.sharavel.sharavel_be.trip.dto.TripListDto;
import com.sharavel.sharavel_be.trip.entity.Trip;
import com.sharavel.sharavel_be.trip_script.repository.TripScriptRepository;
import com.sharavel.sharavel_be.user.entity.Users;

@Component
public class TripMapper {
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private LikesRepository likesRepository;
	@Autowired
	private TripScriptRepository tripScriptRepository;
	@Autowired
	private S3TripMediaRepository s3TripMediaRepository;
	@Autowired
	private S3TripService s3BucketService;

	public TripDto toDto(Trip trip, Users user) {
		boolean isLiked = likesRepository.existsByTargetTypeAndTargetUidAndUser("TRIP", trip.getTripUid(), user);
		Long tripLikesCount = likesRepository.countByTargetTypeAndTargetUid("TRIP", trip.getTripUid());
		Long tripCommentCount = commentRepository.countByTargetTypeAndTargetUidAndDeletedFalse("TRIP", trip.getTripUid());
		Long tripScriptCount = tripScriptRepository.countByTrip(trip);
		List<S3TripMedia> allMediaForTrip = s3TripMediaRepository.findByTripUid(trip.getTripUid());

		TripDto tripDto = new TripDto();
		tripDto.setTripUid(trip.getTripUid()); // Use UUID as the identifier instead of internal ID
		tripDto.setName(trip.getName());
		tripDto.setStartDate(trip.getStartDate());
		tripDto.setEndDate(trip.getEndDate());
		tripDto.setCompleted(trip.isCompleted());
		tripDto.setCountries(
				trip.getCountries().stream()
						.map(CountryDto::new) // Convert Country to CountryDto using the constructor
						.collect(Collectors.toList()));
		tripDto.setScripted(trip.getScripted());

		List<DaysDto> dayDtos = trip.getDays().stream().map(day -> {
			DaysDto dayDto = new DaysDto();
			dayDto.setNumber(day.getNumber());
			dayDto.setLocations(day.getLocations().stream().map(loc -> {
				LocationDto locDto = new LocationDto();
				locDto.setNumber(loc.getNumber());
				locDto.setName(loc.getName());
				locDto.setLongitude(loc.getLongitude());
				locDto.setLatitude(loc.getLatitude());
				locDto.setDescription(loc.getDescription());
				locDto.setCountryIso2(loc.getCountryIso2());

				List<S3TripMediaResponse> mediaResponses = allMediaForTrip.stream()
						.filter(media -> media.getDayNum().equals(day.getNumber()) && // 해당 Day의 Number와 일치
								media.getLocationNum().equals(loc.getNumber()) // 해당 Location의 Number와 일치
				)
						.map(media -> {
							URL presignedOrPublicUrl = s3BucketService.generatePresignedUrl(media.getS3Key(), 604800);
							return new S3TripMediaResponse(
									media.getObjectOwner(), // UUID를 String으로 변환
									media.getS3Key(),
									presignedOrPublicUrl);
						})
						.collect(Collectors.toList());

				locDto.setMedia(mediaResponses);
				// --- LocationDto에 media 설정 로직 끝 ---

				return locDto;
			})
					.sorted(Comparator.comparingInt(LocationDto::getNumber))
					.collect(Collectors.toList()));
			return dayDto;
		})
				.sorted(Comparator.comparingInt(DaysDto::getNumber))
				.collect(Collectors.toList());

		tripDto.setDays(dayDtos);
		tripDto.setIsLiked(isLiked);
		tripDto.setCommentsCount(tripCommentCount);
		tripDto.setLikesCount(tripLikesCount);
		tripDto.setScriptedCount(tripScriptCount);
		return tripDto;
	}

	public TripDto toCloneTripDto(Trip trip) {

		TripDto tripDto = new TripDto();
		tripDto.setTripUid(trip.getTripUid()); // Use UUID as the identifier instead of internal ID
		tripDto.setName(trip.getName());
		tripDto.setStartDate(trip.getStartDate());
		tripDto.setEndDate(trip.getEndDate());
		tripDto.setCompleted(trip.isCompleted());
		tripDto.setCountries(
				trip.getCountries().stream()
						.map(CountryDto::new) // Convert Country to CountryDto using the constructor
						.collect(Collectors.toList()));

		List<DaysDto> dayDtos = trip.getDays().stream().map(day -> {
			DaysDto dayDto = new DaysDto();
			dayDto.setNumber(day.getNumber());
			dayDto.setLocations(day.getLocations().stream().map(loc -> {
				LocationDto locDto = new LocationDto();
				locDto.setNumber(loc.getNumber());
				locDto.setName(loc.getName());
				locDto.setDescription(loc.getDescription());
				return locDto;
			})
					.sorted(Comparator.comparingInt(LocationDto::getNumber))
					.collect(Collectors.toList()));
			return dayDto;
		})
				.sorted(Comparator.comparingInt(DaysDto::getNumber))
				.collect(Collectors.toList());

		tripDto.setDays(dayDtos);
		return tripDto;
	}

	public TripListDto toListDto(Trip trip) {
		Long tripCommentCount = commentRepository.countByTargetTypeAndTargetUidAndDeletedFalse("TRIP", trip.getTripUid());
		Long tripLikesCount = likesRepository.countByTargetTypeAndTargetUid("TRIP", trip.getTripUid());
		Long tripScriptCount = tripScriptRepository.countByTrip(trip);
		List<S3TripMedia> allMediaForTrip = s3TripMediaRepository.findByTripUid(trip.getTripUid());
		List<S3TripMediaResponse> mediaResponses = allMediaForTrip.stream()
				.limit(5)
				.map(media -> {
					URL presignedOrPublicUrl = s3BucketService.generatePresignedUrl(media.getS3Key(), 604800);
					return new S3TripMediaResponse(
							media.getObjectOwner(), // UUID를 String으로 변환
							media.getS3Key(),
							presignedOrPublicUrl);
				})
				.collect(Collectors.toList());

		TripListDto tripListDto = new TripListDto();
		tripListDto.setTripUid(trip.getTripUid());
		tripListDto.setName(trip.getName());
		tripListDto.setUsername(trip.getUid().getUsername());
		tripListDto.setStartDate(trip.getStartDate());
		tripListDto.setEndDate(trip.getEndDate());
		tripListDto.setCountries(
				trip.getCountries().stream()
						.map(CountryDto::new) // Convert Country to CountryDto using the constructor
						.collect(Collectors.toList()));
		tripListDto.setIsCompleted(trip.isCompleted());
		tripListDto.setScriptedCount(tripScriptCount);
		tripListDto.setLikesCount(tripLikesCount);
		tripListDto.setCommentsCount(tripCommentCount);
		tripListDto.setMedia(mediaResponses);
		return tripListDto;
	}
}
