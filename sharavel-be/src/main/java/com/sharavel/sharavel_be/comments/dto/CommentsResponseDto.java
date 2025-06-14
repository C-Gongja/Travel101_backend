package com.sharavel.sharavel_be.comments.dto;

import java.time.LocalDateTime;

public class CommentsResponseDto {
	private String uid;
	private String content;
	private String picture;
	private String username; // or userId, or both
	private String userUid;
	private String parentUid;
	private LocalDateTime createdAt;
	private boolean isLiked;
	private Long likesCount;
	private Long childCount;

	public CommentsResponseDto(String uid, String content, String picture, String username, String userUid,
			String parentUid, LocalDateTime createdAt, boolean isLiked, Long likesCount, Long childCount) {
		this.uid = uid;
		this.content = content;
		this.picture = picture;
		this.username = username;
		this.userUid = userUid;
		this.parentUid = parentUid;
		this.createdAt = createdAt;
		this.isLiked = isLiked;
		this.likesCount = likesCount;
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

	public boolean isLiked() {
		return isLiked;
	}

	public void setIsLiked(boolean isLiked) {
		this.isLiked = isLiked;
	}

	public Long getLikesCount() {
		return likesCount;
	}

	public void setLikesCount(Long likesCount) {
		this.likesCount = likesCount;
	}

	public Long getChildCount() {
		return childCount;
	}

	public void setChildCount(Long childCount) {
		this.childCount = childCount;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}
}
