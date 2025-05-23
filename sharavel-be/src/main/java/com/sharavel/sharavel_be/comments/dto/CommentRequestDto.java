package com.sharavel.sharavel_be.comments.dto;

public class CommentRequestDto {
	private String targetUid;
	private String targetType;
	private String content;
	private String parentUid;

	public CommentRequestDto(String targetUid, String targetType, String content, String parentUid) {
		this.targetUid = targetUid;
		this.targetType = targetType;
		this.content = content;
		this.parentUid = parentUid;
	}

	public String getTargetUid() {
		return targetUid;
	}

	public void setTargetUid(String targetUid) {
		this.targetUid = targetUid;
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
