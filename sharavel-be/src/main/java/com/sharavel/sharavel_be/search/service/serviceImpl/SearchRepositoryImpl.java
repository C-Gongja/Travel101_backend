package com.sharavel.sharavel_be.search.service.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sharavel.sharavel_be.search.service.SearchRepositoryCustom;
import com.sharavel.sharavel_be.trip.dto.TripListDto;
import com.sharavel.sharavel_be.trip.entity.QDays;
import com.sharavel.sharavel_be.trip.entity.QLocations;
import com.sharavel.sharavel_be.trip.entity.QTrip;
import com.sharavel.sharavel_be.user.entity.QUsers;

@Repository
public class SearchRepositoryImpl implements SearchRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public SearchRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	// ⚠ leftJoin(trip.tripDays, days).leftJoin(days.locations, location)처럼 관계를 정확히
	// 따라가야 해.
	@Override
	public List<TripListDto> searchAll(String keyword) {
		QTrip trip = QTrip.trip;
		QLocations location = QLocations.locations;
		QDays days = QDays.days;
		QUsers user = QUsers.users;
		// QCountry country = QCountry.country;
		// QTag tag = QTag.tag;

		return queryFactory
				.select(Projections.constructor(TripListDto.class,
						trip.uid,
						trip.name,
						user.username,
						trip.startDate,
						trip.endDate,
						trip.countries,
						trip.scripted))
				.from(trip)
				.leftJoin(trip.uid, user)
				.leftJoin(trip.days, days)
				.leftJoin(days.locations, location)
				.where(
						trip.name.containsIgnoreCase(keyword)
								.or(user.username.containsIgnoreCase(keyword))
								.or(Expressions.stringTemplate("array_to_string({0}, ',')", trip.countries).containsIgnoreCase(keyword))
				// .or(tag.name.containsIgnoreCase(keyword))
				)
				.distinct()
				.fetch();
	}
}
