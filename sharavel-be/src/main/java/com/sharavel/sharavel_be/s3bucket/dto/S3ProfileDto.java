package com.sharavel.sharavel_be.s3bucket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Lombok: Getter, Setter, toString, equals, hashCode
@NoArgsConstructor // Lombok: 기본 생성자
@AllArgsConstructor // Lombok: 모든 필드를 포함하는 생성자
public class S3ProfileDto {
	private String presignedUrl;
}
