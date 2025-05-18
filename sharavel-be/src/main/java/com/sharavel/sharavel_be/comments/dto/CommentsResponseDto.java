package com.sharavel.sharavel_be.comments.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CommentsResponseDto {
	private String uid;
	private String content;
	private String userName; // or userId, or both
	private String userUid;
	private LocalDateTime createdAt;
	private List<CommentsResponseDto> replies; // 재귀적 구조

	public CommentsResponseDto(String uid, String content, String userName, String userUid, LocalDateTime createdAt,
			List<CommentsResponseDto> replies) {
		this.uid = uid;
		this.content = content;
		this.userName = userName;
		this.userUid = userUid;
		this.createdAt = createdAt;
		this.replies = replies;
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

	public List<CommentsResponseDto> getReplies() {
		return replies;
	}

	public void setReplies(List<CommentsResponseDto> replies) {
		this.replies = replies;
	}

}
