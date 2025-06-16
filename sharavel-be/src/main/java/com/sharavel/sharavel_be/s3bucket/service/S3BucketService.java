package com.sharavel.sharavel_be.s3bucket.service;

import java.net.URL;

import org.springframework.web.multipart.MultipartFile;

import com.sharavel.sharavel_be.s3bucket.dto.response.S3TripMediaResponse;

public interface S3BucketService {
	S3TripMediaResponse uploadFile(MultipartFile file, String tripUid, Integer dayNum, Integer locNum);

	URL generatePresignedUrl(String s3Key, long expirationSeconds);

	byte[] getObjectBytes(String s3Key);

	void deleteFile(String s3Key);
}
