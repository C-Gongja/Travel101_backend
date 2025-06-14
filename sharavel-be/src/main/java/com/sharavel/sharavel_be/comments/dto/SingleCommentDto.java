package com.sharavel.sharavel_be.comments.dto;

import java.time.LocalDateTime;

public class SingleCommentDto {
	private String uid;
	private String content;
	private String picture;
	private String username;
	private String targetUid;
	private String parentUid;
	private LocalDateTime createdAt;

	public SingleCommentDto(String uid, String content, String picture, String userName, String targetUid,
			LocalDateTime createdAt,
			String parentUid) {
		this.uid = uid;
		this.content = content;
		this.picture = picture;
		this.username = userName;
		this.targetUid = targetUid;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTargetUid() {
		return targetUid;
	}

	public void setTargetUid(String targetUid) {
		this.targetUid = targetUid;
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

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	@Override
	public String toString() {
		return "SingleCommentDto [uid=" + uid + ", content=" + content + ", userName=" + username + ", targetUid="
				+ targetUid + ", parentUid=" + parentUid + ", createdAt=" + createdAt + "]";
	}
}
