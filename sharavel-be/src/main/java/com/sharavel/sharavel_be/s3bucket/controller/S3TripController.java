package com.sharavel.sharavel_be.s3bucket.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sharavel.sharavel_be.s3bucket.dto.response.S3TripMediaResponse;
import com.sharavel.sharavel_be.s3bucket.entity.S3TripMedia;
import com.sharavel.sharavel_be.s3bucket.repository.S3TripMediaRepository;
import com.sharavel.sharavel_be.s3bucket.service.S3TripService;

@RestController
@RequestMapping("/api/s3")
public class S3TripController {
	@Autowired
	private S3TripService s3Service;
	@Autowired
	private S3TripMediaRepository tripMediaRepository; // DB 조회를 위해 주입

	@PostMapping("/upload/{tripUid}/{dayNum}/{locNum}")
	public ResponseEntity<List<?>> uploadFile(
			@RequestParam("file") List<MultipartFile> files,
			@PathVariable String tripUid, @PathVariable Integer dayNum, @PathVariable Integer locNum) {
		try {
			if (files == null || files.isEmpty()) {
				throw new IllegalArgumentException("업로드할 파일이 없습니다.");
			}

			List<S3TripMediaResponse> response = new ArrayList<>();
			for (MultipartFile file : files) {
				// 단일 파일 업로드 로직을 반복하여 호출
				S3TripMediaResponse uploaded = s3Service.uploadFile(file, tripUid, dayNum, locNum);
				response.add(uploaded);
			}
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(List.of("파일 업로드 실패: " + e.getMessage()));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of("파일 업로드 실패: " + e.getMessage()));
		}
	}

	@GetMapping("/presigned-url/{s3Key}")
	public ResponseEntity<String> getPresignedUrl(@PathVariable String s3Key) {
		try {
			// Presigned URL 유효 시간 1시간 (3600초) 설정
			URL url = s3Service.generatePresignedUrl(s3Key, 3600);
			return ResponseEntity.ok(url.toString());
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Presigned URL 생성 실패: " + e.getMessage());
		}
	}

	@GetMapping("/trip/{tripUid}/media-urls")
	public ResponseEntity<List<String>> getTripMediaUrls(@PathVariable String tripUid) {
		// 1. DB에서 해당 tripId에 연결된 모든 TripMedia 객체 조회 AWS_ACCESS_KEY
		List<S3TripMedia> mediaList = tripMediaRepository.findByTripUid(tripUid);

		// 2. 각 TripMedia 객체의 S3 Key를 사용하여 Presigned URL 생성
		List<String> presignedUrls = mediaList.stream()
				.map(media -> s3Service.generatePresignedUrl(media.getS3Key(), 3600).toString()) // 1시간 유효 URL
				.collect(Collectors.toList());

		return ResponseEntity.ok(presignedUrls);
	}

	@GetMapping("/download/{s3Key}")
	public ResponseEntity<byte[]> downloadFile(@PathVariable String s3Key) {
		try {
			byte[] fileBytes = s3Service.getObjectBytes(s3Key);
			// S3 Key에서 확장자를 추출하여 Content-Type 추정
			String contentType = "application/octet-stream"; // 기본값
			if (s3Key.contains(".")) {
				String extension = s3Key.substring(s3Key.lastIndexOf(".") + 1).toLowerCase();
				switch (extension) {
					case "jpg", "jpeg" -> contentType = MediaType.IMAGE_JPEG_VALUE;
					case "png" -> contentType = MediaType.IMAGE_PNG_VALUE;
					case "gif" -> contentType = MediaType.IMAGE_GIF_VALUE;
					case "mp4" -> contentType = "video/mp4";
					case "mov" -> contentType = "video/quicktime";
				}
			}

			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + s3Key + "\"")
					.contentType(MediaType.parseMediaType(contentType)) // 파일의 실제 MIME 타입
					.body(fileBytes);
		} catch (RuntimeException e) {
			if (e.getMessage().contains("찾을 수 없습니다")) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@DeleteMapping("/delete/{s3Key}")
	public ResponseEntity<String> deleteFile(@PathVariable String s3Key) {
		try {
			s3Service.deleteFile(s3Key);
			return ResponseEntity.ok("파일 삭제 성공: " + s3Key);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 삭제 실패: " + e.getMessage());
		}
	}
}
