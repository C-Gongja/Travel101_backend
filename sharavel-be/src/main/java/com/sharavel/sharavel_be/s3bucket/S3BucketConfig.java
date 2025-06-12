package com.sharavel.sharavel_be.s3bucket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class S3BucketConfig {
	@Value("${aws.access.key}")
	String awsAccessKey;

	@Value("${aws.secret.key}")
	String awsSecretKey;

	@Bean
	public AmazonS3 getAWSS3Client() {
		AWSCredentials credentails = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
		return AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentails))
				.withRegion(Regions.US_EAST_2)
				.build();
	}
}
