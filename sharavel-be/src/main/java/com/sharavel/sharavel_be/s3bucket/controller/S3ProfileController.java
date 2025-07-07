package com.sharavel.sharavel_be.s3bucket.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sharavel.sharavel_be.s3bucket.dto.S3ProfileDto;
import com.sharavel.sharavel_be.s3bucket.service.S3ProfileService;

@RestController
@RequestMapping("/api/s3/profile")
public class S3ProfileController {
	@Autowired
	private S3ProfileService s3ProfileService;

	@PostMapping("/upload/{uuid}")
	public ResponseEntity<?> uploadUserProfilePic(
			@RequestParam("newProfilePic") MultipartFile file,
			@PathVariable String uuid) {
		try {
			if (file == null || file.isEmpty()) {
				throw new IllegalArgumentException("업로드할 파일이 없습니다.");
			}

			S3ProfileDto uploaded = s3ProfileService.uploadUserProfilePic(file, uuid);
			return ResponseEntity.ok(uploaded);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(List.of("파일 업로드 실패: " + e.getMessage()));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of("파일 업로드 실패: " + e.getMessage()));
		}
	}
}
