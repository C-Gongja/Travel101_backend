package com.sharavel.sharavel_be.comments.dto;

public class CommentRootRequestDto {
	private String targetUid;
	private String targetType;

	public CommentRootRequestDto(String targetUid, String targetType) {
		this.targetUid = targetUid;
		this.targetType = targetType;
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
}
