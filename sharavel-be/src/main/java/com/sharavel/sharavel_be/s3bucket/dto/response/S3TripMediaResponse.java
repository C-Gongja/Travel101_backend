package com.sharavel.sharavel_be.s3bucket.dto.response;

import java.net.URL;

public class S3TripMediaResponse {
	private String ownerUid;
	private String s3Key;
	private URL presignedUrl;

	public S3TripMediaResponse(String ownerUid, String s3Key, URL presignedUrl) {
		this.ownerUid = ownerUid;
		this.s3Key = s3Key;
		this.presignedUrl = presignedUrl;
	}

	public String getOwnerUid() {
		return ownerUid;
	}

	public void setOwnerUid(String ownerUid) {
		this.ownerUid = ownerUid;
	}

	public String getS3Key() {
		return s3Key;
	}

	public void setS3Key(String s3Key) {
		this.s3Key = s3Key;
	}

	public URL getPresignedUrl() {
		return presignedUrl;
	}

	public void setPresignedUrl(URL presignedUrl) {
		this.presignedUrl = presignedUrl;
	}
}
