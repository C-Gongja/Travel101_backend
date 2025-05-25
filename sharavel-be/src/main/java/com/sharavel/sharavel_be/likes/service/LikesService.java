package com.sharavel.sharavel_be.likes.service;

public interface LikesService {
	public String addLike(String targetType, String targetUid);

	public String removeLike(String targetType, String targetUid);

	public Long getLikeCount(String targetType, String targetUid);
}
