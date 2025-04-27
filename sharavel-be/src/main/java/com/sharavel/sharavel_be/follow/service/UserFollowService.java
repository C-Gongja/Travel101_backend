package com.sharavel.sharavel_be.follow.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.sharavel.sharavel_be.user.entity.Users;

public interface UserFollowService {
	public ResponseEntity<?> followUser(String userId);

	public ResponseEntity<?> unfollowUser(String userId);

	public Long getFollowingCount(Long userId);

	public Long getFollowersCount(Long userId);

	public List<Users> getFollowing(Long userId);

	public List<Users> getFollowers(Long userId);
}
