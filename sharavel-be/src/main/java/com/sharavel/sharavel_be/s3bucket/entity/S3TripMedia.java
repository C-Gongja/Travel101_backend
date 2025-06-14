package com.sharavel.sharavel_be.s3bucket.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
	private Long tripId;

	// 필요에 따라 dayId, locationId 등 추가 가능
	@Column(nullable = false)
	private Integer dayNum;

	@Column(nullable = false)
	private Integer locationNnum;

	@Column(nullable = false, length = 10)
	private String mediaType; // IMAGE, VIDEO

	@Column(nullable = false, unique = true, length = 255)
	private String s3Key; // S3에 저장된 파일 이름 (Object Key)

	@Column(nullable = false, length = 255)
	private String originalFileName; // 원본 파일 이름

	private Long fileSize; // 파일 크기 (bytes)

	@Column(nullable = false)
	private LocalDateTime uploadedAt; // 업로드 시간
}
