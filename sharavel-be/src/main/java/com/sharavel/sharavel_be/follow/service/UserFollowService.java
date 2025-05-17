package com.sharavel.sharavel_be.follow.service;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import com.sharavel.sharavel_be.user.dto.UserSnippetDto;

public interface UserFollowService {
	public ResponseEntity<?> followUser(String followingId);

	public ResponseEntity<?> unfollowUser(String unFollowId);

	public Long getFollowingCount(String userId);

	public Long getFollowersCount(String userId);

	public Page<UserSnippetDto> getInfFollowers(String uuid, int page, int limit);

	public Page<UserSnippetDto> getInfFollowing(String uuid, int page, int limit);
}
