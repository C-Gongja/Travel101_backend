package com.sharavel.sharavel_be.s3bucket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sharavel.sharavel_be.s3bucket.entity.S3UserMedia;

@Repository
public interface S3ProfileRepository extends JpaRepository<S3UserMedia, Long> {
	S3UserMedia findByUserUid(String uuid);

	S3UserMedia findByS3Key(String s3Key);
}
