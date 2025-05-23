package com.sharavel.sharavel_be.comments.dto;

// uid: originalComment.uid,
// targetType: targetType,
// targetUid: targetUid,
// content: newCommentText.trim(),
// parentUid: originalComment.parentUid || null,

public class CommentEditRequestDto {
	private String uid;
	private String targetType;
	private String targetUid;
	private String content;
	private String parentUid;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getTargetUid() {
		return targetUid;
	}

	public void setTargetUid(String targetUid) {
		this.targetUid = targetUid;
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

	@Override
	public String toString() {
		return "CommentEditRequestDto [commentUid=" + uid + ", content=" + content + "]";
	}

}
