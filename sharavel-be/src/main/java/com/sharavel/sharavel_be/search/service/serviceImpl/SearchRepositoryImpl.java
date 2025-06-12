package com.sharavel.sharavel_be.search.service.serviceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.sharavel.sharavel_be.search.service.SearchRepositoryCustom;
import com.sharavel.sharavel_be.trip.dto.TripListDto;
import com.sharavel.sharavel_be.trip.entity.Trip;
import com.sharavel.sharavel_be.trip.mapper.TripMapper;


@Repository
public class SearchRepositoryImpl implements SearchRepositoryCustom {

	// private final JPAQueryFactory queryFactory;
	// private final TripMapper tripMapper;

	// public SearchRepositoryImpl(JPAQueryFactory queryFactory, TripMapper
	// tripMapper) {
	// this.queryFactory = queryFactory;
	// this.tripMapper = tripMapper;
	// }

	// // ⚠ leftJoin(trip.tripDays, days).leftJoin(days.locations, location)처럼 관계를
	// 정확히
	// // 따라가야 해.
	// @Override
	// public List<TripListDto> searchAll(String keyword) {
	// QTrip trip = QTrip.trip;
	// QLocations location = QLocations.locations;
	// QDays days = QDays.days;
	// QUsers user = QUsers.users;
	// QCountry country = QCountry.country;
	// // QTag tag = QTag.tag;

	// // First get the trips that match the criteria
	// List<String> matchingTripUids = queryFactory
	// .select(trip.tripUid)
	// .from(trip)
	// .leftJoin(trip.uid, user)
	// .leftJoin(trip.days, days)
	// .leftJoin(days.locations, location)
	// .leftJoin(trip.countries, country)
	// .where(
	// trip.name.containsIgnoreCase(keyword)
	// .or(user.username.containsIgnoreCase(keyword))
	// .or(country.name.containsIgnoreCase(keyword))
	// // .or(tag.name.containsIgnoreCase(keyword))
	// )
	// .distinct()
	// .fetch();

	// // Now build the DTOs with the correct data
	// if (matchingTripUids.isEmpty()) {
	// return Collections.emptyList();
	// }

	// List<TripListDto> results = new ArrayList<>();

	// for (String tripUid : matchingTripUids) {
	// // Get the trip details
	// Trip tripEntity = queryFactory
	// .selectFrom(trip)
	// .leftJoin(trip.uid, user).fetchJoin()
	// .where(trip.tripUid.eq(tripUid))
	// .fetchOne();

	// if (tripEntity != null) {
	// // Use the mapper to convert Trip to TripListDto
	// TripListDto dto = tripMapper.toListDto(tripEntity);
	// results.add(dto);
	// }
	// }

	// return results;
	// }
}
