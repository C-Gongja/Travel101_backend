package com.sharavel.sharavel_be.comments.dto;

import java.time.LocalDateTime;

public class SingleCommentDto {
	private String uid;
	private String content;
	private String userName;
	private String userUid;
	private LocalDateTime createdAt;
	private String parentUid;

	public SingleCommentDto(String uid, String content, String userName, String userUid, LocalDateTime createdAt,
			String parentUid) {
		this.uid = uid;
		this.content = content;
		this.userName = userName;
		this.userUid = userUid;
		this.createdAt = createdAt;
		this.parentUid = parentUid;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

}
