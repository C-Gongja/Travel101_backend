package com.sharavel.sharavel_be.s3bucket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class S3BucketConfig {
	@Value("${aws.access.key}")
	String awsAccessKey;

	@Value("${aws.secret.key}")
	String awsSecretKey;

	@Value("${aws.s3.region}")
	private String awsRegion;

	@Bean
	public S3Client s3Client() {
		// V2에서는 Region.of()를 사용하여 문자열을 Region 객체로 변환합니다.
		Region region = Region.of(awsRegion);

		// V2에서는 AwsBasicCredentials와 StaticCredentialsProvider를 사용합니다.
		AwsBasicCredentials credentials = AwsBasicCredentials.create(awsAccessKey, awsSecretKey);

		return S3Client.builder()
				.credentialsProvider(StaticCredentialsProvider.create(credentials))
				.region(region)
				.build();
	}

	// Presigned URL 생성을 위해 S3Presigner 빈을 별도로 정의. (s3Client 빈이 의존하는 region을 사용하도록 설정할 수 있음.)
	@Bean
	public S3Presigner s3Presigner(S3Client s3Client) { // S3Client 빈을 주입받아 동일한 설정 사용
		// S3Client의 설정(리전, 자격 증명)을 S3Presigner에도 적용하여 일관성을 유지합니다.
		return S3Presigner.builder()
				.region(s3Client.serviceClientConfiguration().region()) // S3Client와 동일한 리전 사용
				.credentialsProvider(s3Client.serviceClientConfiguration().credentialsProvider()) // S3Client와 동일한
				.build();
	}
}
