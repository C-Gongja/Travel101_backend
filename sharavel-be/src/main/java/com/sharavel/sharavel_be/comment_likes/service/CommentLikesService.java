package com.sharavel.sharavel_be.comment_likes.service;

public interface CommentLikesService {
	public String addLike(String commentUid);

	public String removeLike(String commentUid);

	public int getCommentLikes(String commentUid);
}
