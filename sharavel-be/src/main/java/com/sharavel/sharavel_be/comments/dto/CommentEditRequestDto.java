package com.sharavel.sharavel_be.comments.dto;

public class CommentEditRequestDto {
	private String commentUid;
	private String content;

	public String getCommentUid() {
		return commentUid;
	}

	public void setCommentUid(String commentUid) {
		this.commentUid = commentUid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
