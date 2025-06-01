package com.sharavel.sharavel_be.trip.mapper;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sharavel.sharavel_be.comments.repository.CommentRepository;
import com.sharavel.sharavel_be.countries.dto.CountryDto;
import com.sharavel.sharavel_be.likes.repository.LikesRepository;
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

	public TripDto toDto(Trip trip, Users user) {
		boolean isLiked = likesRepository.existsByTargetTypeAndTargetUidAndUser("TRIP", trip.getTripUid(), user);
		Long tripLikesCount = likesRepository.countByTargetTypeAndTargetUid("TRIP", trip.getTripUid());
		Long tripCommentCount = commentRepository.countByTargetTypeAndTargetUidAndDeletedFalse("TRIP", trip.getTripUid());
		Long tripScriptCount = tripScriptRepository.countByCopiedTrip(trip);

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

	public TripListDto toListDto(Trip trip) {
		Long tripCommentCount = commentRepository.countByTargetTypeAndTargetUidAndDeletedFalse("TRIP", trip.getTripUid());
		Long tripLikesCount = likesRepository.countByTargetTypeAndTargetUid("TRIP", trip.getTripUid());

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
		tripListDto.setScriptedCount(trip.getScripted());
		tripListDto.setLikesCount(tripLikesCount);
		tripListDto.setCommentsCount(tripCommentCount);
		return tripListDto;
	}
}
