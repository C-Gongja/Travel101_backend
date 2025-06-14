package com.sharavel.sharavel_be.s3bucket.dto;

import java.time.LocalDateTime;

public class S3TripMediaDto {
	private String objectOwner;
	private Long tripId;
	private Integer dayNum;
	private Integer locationNnum;
	private String mediaType; // IMAGE, VIDEO
	private String s3Key; // S3에 저장된 파일 이름 (Object Key)
	private Long fileSize; // 파일 크기 (bytes)
	private LocalDateTime uploadedAt; // 업로드 시간

	public String getObjectOwner() {
		return objectOwner;
	}

	public void setObjectOwner(String objectOwner) {
		this.objectOwner = objectOwner;
	}

	public Long getTripId() {
		return tripId;
	}

	public void setTripId(Long tripId) {
		this.tripId = tripId;
	}

	public Integer getDayNum() {
		return dayNum;
	}

	public void setDayNum(Integer dayNum) {
		this.dayNum = dayNum;
	}

	public Integer getLocationNnum() {
		return locationNnum;
	}

	public void setLocationNnum(Integer locationNnum) {
		this.locationNnum = locationNnum;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public String getS3Key() {
		return s3Key;
	}

	public void setS3Key(String s3Key) {
		this.s3Key = s3Key;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public LocalDateTime getUploadedAt() {
		return uploadedAt;
	}

	public void setUploadedAt(LocalDateTime uploadedAt) {
		this.uploadedAt = uploadedAt;
	}

}
