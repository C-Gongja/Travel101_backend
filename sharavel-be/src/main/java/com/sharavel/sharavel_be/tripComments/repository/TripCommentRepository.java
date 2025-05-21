package com.sharavel.sharavel_be.tripComments.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sharavel.sharavel_be.trip.entity.Trip;
import com.sharavel.sharavel_be.tripComments.entity.TripComment;

@Repository
public interface TripCommentRepository extends JpaRepository<TripComment, Long> {
	Optional<TripComment> findByUid(String uid);

	Boolean existsByParent(TripComment parent);

	Long countByParentAndDeletedFalse(TripComment parent);

	List<TripComment> findByTrip_TripUidOrderByCreatedAtAsc(String tripUid);

	List<TripComment> findByTripAndParentIsNullAndDeletedFalseOrderByCreatedAtAsc(Trip trip);

	List<TripComment> findByTripAndParentIsNullOrderByCreatedAtAsc(Trip trip);

	List<TripComment> findByTripAndParentIsNull(Trip trip);

	List<TripComment> findByParentAndDeletedFalseOrderByCreatedAtAsc(TripComment parent);

	List<TripComment> findByParentOrderByCreatedAtAsc(TripComment parent);

	List<TripComment> findByParentAndDeletedFalse(TripComment parent);
}
