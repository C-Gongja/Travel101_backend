package com.sharavel.sharavel_be.s3bucket.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sharavel.sharavel_be.s3bucket.entity.S3TripMedia;

@Repository
public interface S3TripMediaRepository extends JpaRepository<S3TripMedia, Long> {
	Optional<S3TripMedia> findByS3Key(String s3Key);

	List<S3TripMedia> findByTripUid(String tripUid);

	List<S3TripMedia> findByTripUidAndDayNumAndLocationNum(String tripUid, Integer dayNum, Integer locNum);

	void deleteByS3Key(String s3Key);

	// 필요에 따라 findByTripIdAndDayId, findByTripIdAndLocationId 등 추가 가능
}
