package com.sharavel.sharavel_be.s3bucket.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trip_media")
@Data // Lombok: Getter, Setter, toString, equals, hashCode
@NoArgsConstructor // Lombok: 기본 생성자
@AllArgsConstructor // Lombok: 모든 필드를 포함하는 생성자
public class S3TripMedia {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String objectOwner;

	@Column(nullable = false)
	private String tripUid;

	// 필요에 따라 dayId, locationId 등 추가 가능
	@Column(nullable = true)
	private Integer dayNum;

	@Column(nullable = true)
	private Integer locationNum;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private MediaType mediaType; // IMAGE, VIDEO

	@Column(nullable = false, unique = true, length = 255)
	private String s3Key; // S3에 저장된 파일 이름 (Object Key)

	@Column(nullable = false, length = 255)
	private String originalFileName; // 원본 파일 이름

	private Long fileSize; // 파일 크기 (bytes)

	@Column(nullable = false)
	private LocalDateTime uploadedAt; // 업로드 시간

	// private Integer orderInLocation;

	// @Enumerated(EnumType.STRING)
	// @Column(nullable = false, length = 20) // 예를 들어 'TRIP_COVER',
	// 'LOCATION_MEDIA' 등
	// private MediaUsageType usageType; // 새로 추가될 필드 (아래 Enum 참조)

	public enum MediaType {
		IMAGE,
		VIDEO
		// 추가적인 미디어 타입 (예: AUDIO, DOCUMENT)
	}

	// MediaUsageType Enum 정의
	public enum MediaUsageType {
		TRIP_COVER, // 여행 전체를 대표하는 이미지/비디오 (예: 썸네일)
		TRIP_GALLERY, // 여행 전체에 대한 일반 갤러리 이미지/비디오
		LOCATION_MEDIA; // 특정 Location에 속하는 이미지/비디오
		// 추가: PROFILE_PIC, COMMENT_ATTACHMENT 등 필요에 따라 추가
	}
}