package com.sharavel.sharavel_be.tripComments.dto;

import java.time.LocalDateTime;

public class TripCommentsResponseDto {
	private String uid;
	private String content;
	private String username; // or userId, or both
	private String userUid;
	private String parentUid;
	private LocalDateTime createdAt;
	private Long childCount;

	public TripCommentsResponseDto(String uid, String content, String username, String userUid,
			String parentUid, LocalDateTime createdAt, Long childCount) {
		this.uid = uid;
		this.content = content;
		this.username = username;
		this.userUid = userUid;
		this.parentUid = parentUid;
		this.createdAt = createdAt;
		this.childCount = childCount;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserUid() {
		return userUid;
	}

	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getParentUid() {
		return parentUid;
	}

	public void setParentUid(String parentUid) {
		this.parentUid = parentUid;
	}

	public Long getChildCount() {
		return childCount;
	}

	public void setChildCount(Long childCount) {
		this.childCount = childCount;
	}

}
