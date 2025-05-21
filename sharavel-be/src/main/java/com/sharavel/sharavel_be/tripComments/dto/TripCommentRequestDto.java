package com.sharavel.sharavel_be.tripComments.dto;

public class TripCommentRequestDto {
	private String tripUid;
	private String userUid;
	private String content;
	private String parentUid;

	public TripCommentRequestDto(String tripUid, String userUid, String content, String parentUid) {
		this.tripUid = tripUid;
		this.userUid = userUid;
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
