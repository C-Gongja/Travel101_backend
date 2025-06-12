package com.sharavel.sharavel_be.s3bucket.service.serviceImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.sharavel.sharavel_be.s3bucket.service.S3BucketService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class S3BucketServiceImpl implements S3BucketService {
	@Autowired
	AmazonS3 getAWSS3Client;

	@Override
	public List<Bucket> getBucketList() {
		// log.info("getting bucket list... ");
		return getAWSS3Client.listBuckets();
	}

	@Override
	public boolean validateBucket(String bucketName) {
		List<Bucket> bucketList = getBucketList();
		// log.info("Bucket list:" + bucketList);
		return bucketList.stream().anyMatch(m -> bucketName.equals(m.getName()));
	}

	@Override
	public void getObjectFromBucket(String bucketName, String objectName) throws IOException {
		S3Object s3Object = getAWSS3Client.getObject(bucketName, objectName);
		S3ObjectInputStream inputStream = s3Object.getObjectContent();
		FileOutputStream fos = new FileOutputStream(new File("opt/test/v1/" + objectName));
		byte[] read_buf = new byte[1024];
		int read_len = 0;
		while ((read_len = inputStream.read(read_buf)) > 0) {
			fos.write(read_buf, 0, read_len);
		}
		inputStream.close();
		fos.close();
	}

	@Override
	public void createBucket(String bucketName) {
		getAWSS3Client.createBucket(bucketName);
	}

	@Override
	public void putObjectIntoBucket(String bucketName, String objectName, String filePathToUpload) {
		try {
			getAWSS3Client.putObject(bucketName, objectName, new File(filePathToUpload));
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			System.exit(1);
		}
	}

	@Override
	public List<String> listFiles(final String bucketName) throws AmazonClientException {
		List<String> keys = new ArrayList<>();
		ObjectListing objectListing = getAWSS3Client.listObjects(bucketName);

		while (true) {
			List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
			if (objectSummaries.isEmpty()) {
				break;
			}

			objectSummaries.stream()
					.filter(item -> !item.getKey().endsWith("/"))
					.map(S3ObjectSummary::getKey)
					.forEach(keys::add);

			objectListing = getAWSS3Client.listNextBatchOfObjects(objectListing);
		}

		log.info("Files found in bucket({}): {}", bucketName, keys);
		return keys;
	}
}
