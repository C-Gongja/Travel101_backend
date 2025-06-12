package com.sharavel.sharavel_be.s3bucket.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.s3.model.Bucket;
import com.sharavel.sharavel_be.s3bucket.service.S3BucketService;

@RestController
@RequestMapping("/public/s3")
public class S3TripController {
	@Autowired
	S3BucketService s3BucketService;

	@GetMapping("/bucket")
	public ResponseEntity<?> getBucketList() {
		List<Bucket> bucketList = s3BucketService.getBucketList();
		return ResponseEntity.ok(bucketList);
	}

	@GetMapping("/getTestObjects")
	public ResponseEntity<?> getObject() {
		List<String> objectList = s3BucketService.listFiles("sharavel-s3-bucket");
		return ResponseEntity.ok(objectList);
	}

	@GetMapping("/downloadObj")
	public void downloadObject(@RequestParam("bucketName") String bucketName, @RequestParam("objName") String objName)
			throws Exception {
		s3BucketService.getObjectFromBucket(bucketName, objName);
	}

	@PostMapping("/uploadObj")
	public void uploadObject(@RequestParam("bucketName") String bucketName, @RequestParam("objName") String objName)
			throws Exception {
		s3BucketService.putObjectIntoBucket(bucketName, objName, "opt/test/v1/uploadfile.txt");
	}

	@PostMapping("/createBucket")
	public String createBucket(@RequestParam("bucketName") String bucketName) {
		s3BucketService.createBucket(bucketName);
		return "done";
	}
}
