package com.sharavel.sharavel_be.s3bucket.service;

import java.net.URL;

import org.springframework.web.multipart.MultipartFile;

public interface S3BucketService {
	String uploadFile(MultipartFile file, Long tripId);

	URL generatePresignedUrl(String s3Key, long expirationSeconds);

	byte[] getObjectBytes(String s3Key);

	void deleteFile(String s3Key);
}
