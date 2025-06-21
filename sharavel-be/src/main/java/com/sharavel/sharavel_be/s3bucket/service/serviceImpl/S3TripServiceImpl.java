package com.sharavel.sharavel_be.s3bucket.service.serviceImpl;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sharavel.sharavel_be.s3bucket.dto.response.S3TripMediaResponse;
import com.sharavel.sharavel_be.s3bucket.entity.S3TripMedia;
import com.sharavel.sharavel_be.s3bucket.entity.S3TripMedia.MediaType;
import com.sharavel.sharavel_be.s3bucket.repository.S3TripMediaRepository;
import com.sharavel.sharavel_be.s3bucket.service.S3TripService;
import com.sharavel.sharavel_be.trip.entity.Trip;
import com.sharavel.sharavel_be.trip.repository.TripRepository;
import com.sharavel.sharavel_be.user.entity.Users;
import com.sharavel.sharavel_be.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
@Slf4j
public class S3TripServiceImpl implements S3TripService {
	@Autowired
	private S3Client s3Client;
	@Autowired
	private S3Presigner s3Presigner; // Presigned URL 생성을 위한 클라이언트
	@Autowired
	private S3TripMediaRepository tripMediaRepository; // DB 연동을 위한 Repository
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TripRepository tripRepository;

	@Value("${aws.s3.bucket-name}")
	private String bucketName;

	private Users getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("User not authenticated");
		}
		String email = authentication.getName();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalStateException("Current user not found"));
	}

	@Override
	@Transactional // S3 업로드와 DB 저장이 하나의 트랜잭션으로 처리되도록 함 (실패 시 롤백)
	public S3TripMediaResponse uploadFile(MultipartFile file, String tripUid, Integer dayNum, Integer locNum) {
		Users user = getCurrentUser();
		Trip trip = tripRepository.findByTripUid(tripUid)
				.orElseThrow(() -> new RuntimeException("getTripByUuid Trip not found"));

		if (!user.getUuid().equals(trip.getUid().getUuid())) {
			throw new IllegalArgumentException("permission denied");
		}

		// current user랑 trip의 소유자랑 같은지 확인 필요

		if (file.isEmpty()) {
			throw new IllegalArgumentException("file is empty.");
		}

		String originalFilename = file.getOriginalFilename();
		String fileExtension = "";
		if (originalFilename != null && originalFilename.contains(".")) {
			fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
		}
		// S3에 저장될 파일의 고유한 이름 생성 (UUID + 원본 확장자)
		String s3Key = String.format("trips/%s/days/%d/locations/%d/%s%s",
				tripUid, dayNum, locNum, UUID.randomUUID().toString(), fileExtension);

		String contentType = file.getContentType();
		MediaType mediaType = (contentType != null && contentType.startsWith("video")) ? MediaType.VIDEO : MediaType.IMAGE;

		try {
			// 1. S3에 파일 업로드 (V2 SDK 방식)
			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
					.bucket(bucketName) // S3 버킷 이름
					.key(s3Key) // S3에 저장될 파일의 키 (이름)
					.contentType(contentType) // 파일의 Content-Type (예: image/jpeg, video/mp4)
					.contentLength(file.getSize()) // 파일 크기
					.build();

			// MultipartFile의 바이트 배열을 RequestBody로 변환하여 업로드
			s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

			// 2. 파일 메타데이터를 데이터베이스에 저장
			S3TripMedia tripMedia = new S3TripMedia();
			tripMedia.setObjectOwner(user.getUuid());
			tripMedia.setTripUid(tripUid);
			tripMedia.setDayNum(dayNum);
			tripMedia.setLocationNum(locNum);
			tripMedia.setS3Key(s3Key);
			tripMedia.setOriginalFileName(originalFilename);
			tripMedia.setMediaType(mediaType);
			// tripMedia.setUsageType(MediaUsageType.LOCATION_MEDIA);
			tripMedia.setFileSize(file.getSize());
			tripMedia.setUploadedAt(LocalDateTime.now()); // 현재 시간 기록

			tripMediaRepository.save(tripMedia); // DB에 저장

			URL presignedUrl = generatePresignedUrl(s3Key, 604800);

			S3TripMediaResponse response = new S3TripMediaResponse(user.getUuid(), s3Key, presignedUrl);
			// return s3key and presignedURL
			return response; // S3 키 반환 (나중에 조회/삭제 시 사용)

		} catch (IOException e) {
			// MultipartFile에서 바이트를 가져오는 중 발생할 수 있는 IO 에러
			throw new RuntimeException("파일의 내용을 읽는 중 오류가 발생했습니다: " + e.getMessage(), e);
		} catch (S3Exception e) {
			// S3 서비스에서 발생할 수 있는 에러 (예: 권한 없음, 버킷 없음 등)
			throw new RuntimeException("S3 업로드 중 오류가 발생했습니다: " + e.awsErrorDetails().errorMessage(), e);
		} catch (Exception e) {
			// 그 외 예상치 못한 에러
			throw new RuntimeException("파일 업로드 및 DB 저장 중 알 수 없는 오류 발생: " + e.getMessage(), e);
		}
	}

	@Override
	public URL generatePresignedUrl(String s3Key, long expirationSeconds) {
		// Presigned URL을 생성할 객체에 대한 요청 빌드
		GetObjectRequest getObjectRequest = GetObjectRequest.builder()
				.bucket(bucketName)
				.key(s3Key)
				.build();

		// Presigned URL 요청 빌드 (유효 시간 설정)
		GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
				.signatureDuration(Duration.ofSeconds(expirationSeconds)) // URL 유효 시간 설정 (V2는 Duration 사용)
				.getObjectRequest(getObjectRequest)
				.build();

		try {
			// S3Presigner를 사용하여 Presigned URL 생성
			PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(presignRequest);
			return presignedGetObjectRequest.url();
		} catch (S3Exception e) {
			throw new RuntimeException("Presigned URL 생성 중 S3 서비스 오류 발생: " + e.awsErrorDetails().errorMessage(), e);
		} catch (Exception e) {
			throw new RuntimeException("Presigned URL 생성 중 알 수 없는 오류 발생: " + e.getMessage(), e);
		}
	}

	@Override
	public byte[] getObjectBytes(String s3Key) {
		try {
			// S3에서 객체를 가져오기 위한 요청 빌드
			GetObjectRequest getObjectRequest = GetObjectRequest.builder()
					.bucket(bucketName)
					.key(s3Key)
					.build();

			// 객체의 내용을 바이트 배열로 가져옴
			return s3Client.getObjectAsBytes(getObjectRequest).asByteArray();
		} catch (S3Exception e) {
			if (e.statusCode() == 404) {
				throw new RuntimeException("S3에서 파일을 찾을 수 없습니다: " + s3Key, e);
			}
			throw new RuntimeException("S3에서 파일을 가져오는 중 오류 발생: " + e.awsErrorDetails().errorMessage(), e);
		} catch (Exception e) {
			throw new RuntimeException("파일을 바이트로 가져오는 중 알 수 없는 오류 발생: " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional
	public void deleteFile(String s3Key) {
		try {
			// 1. S3에서 파일 삭제
			DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
					.bucket(bucketName)
					.key(s3Key)
					.build();
			s3Client.deleteObject(deleteObjectRequest);

			// 2. 데이터베이스에서 파일 메타데이터 삭제
			tripMediaRepository.deleteByS3Key(s3Key);

		} catch (S3Exception e) {
			throw new RuntimeException("S3에서 파일 삭제 중 오류 발생: " + e.awsErrorDetails().errorMessage(), e);
		} catch (Exception e) {
			throw new RuntimeException("파일 삭제 및 DB 업데이트 중 알 수 없는 오류 발생: " + e.getMessage(), e);
		}
	}
}
