package com.sharavel.sharavel_be.s3bucket.service;

import java.net.URL;

import org.springframework.web.multipart.MultipartFile;

import com.sharavel.sharavel_be.s3bucket.dto.S3ProfileDto;

public interface S3ProfileService {
	public S3ProfileDto uploadUserProfilePic(MultipartFile file, String uuid);

	public String getS3UserProfileImg(String uuid);

	public URL generatePresignedUrl(String s3Key, long expirationSeconds);
}
