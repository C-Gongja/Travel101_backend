package com.sharavel.sharavel_be.comments.dto;

public class CommentRequestDto {
	private String tripUid; // 어떤 Trip에 작성하는지
	private String userUid; // 누가 작성하는지
	private String content; // 댓글 내용
	private String parentUid; // 대댓글일 경우 (optional)

	public CommentRequestDto(String tripUid, String userUid, String content, String parentUid) {
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
