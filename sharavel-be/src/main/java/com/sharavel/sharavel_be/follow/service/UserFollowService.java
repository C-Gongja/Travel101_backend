package com.sharavel.sharavel_be.follow.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.sharavel.sharavel_be.user.entity.Users;

public interface UserFollowService {
	public ResponseEntity<?> followUser(String followingId);

	public ResponseEntity<?> unfollowUser(String unFollowId);

	public Long getFollowingCount(String userId);

	public Long getFollowersCount(String userId);

	public List<Users> getAllFollowing(String username);

	public List<Users> getAllFollowers(String username);
}
