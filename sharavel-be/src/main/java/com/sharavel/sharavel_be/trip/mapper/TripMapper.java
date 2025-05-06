package com.sharavel.sharavel_be.trip.mapper;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sharavel.sharavel_be.countries.dto.CountryDto;
import com.sharavel.sharavel_be.trip.dto.DaysDto;
import com.sharavel.sharavel_be.trip.dto.LocationDto;
import com.sharavel.sharavel_be.trip.dto.TripDto;
import com.sharavel.sharavel_be.trip.dto.TripListDto;
import com.sharavel.sharavel_be.trip.entity.Trip;

@Component
public class TripMapper {
	public TripDto toDto(Trip trip) {
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
		return tripDto;
	}

	public TripListDto toListDto(Trip trip) {
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
		tripListDto.setScripted(trip.getScripted());
		return tripListDto;
	}
}
