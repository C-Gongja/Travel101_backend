package com.sharavel.sharavel_be.comments.dto;

public class CommentRequestDto {
	private String tripUid;
	private String userUid;
	private String targetType;
	private String content;
	private String parentUid;

	public CommentRequestDto(String tripUid, String userUid, String targetType, String content, String parentUid) {
		this.tripUid = tripUid;
		this.userUid = userUid;
		this.targetType = targetType;
		this.content = content;
		this.parentUid = parentUid;
	}

	public String getTripUid() {
		return tripUid;
	}

	public void setTripUid(String tripUid) {
		this.tripUid = tripUid;
	}

	public String getUserUid() {
		return userUid;
	}

	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getParentUid() {
		return parentUid;
	}

	public void setParentUid(String parentUid) {
		this.parentUid = parentUid;
	}
}
