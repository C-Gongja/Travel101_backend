package com.sharavel.sharavel_be.follow.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sharavel.sharavel_be.follow.service.UserFollowService;
import com.sharavel.sharavel_be.user.dto.UserSnippetDto;

@RestController
@RequestMapping("/api/users")
public class UserFollowController {

	@Autowired
	private UserFollowService userFollowService;

	// 팔로우 유저 (팔로우를 추가하는 API)
	@PostMapping("/follow")
	public ResponseEntity<String> followUser(@RequestParam String targetId) {
		try {
			// 팔로우 로직 처리
			userFollowService.followUser(targetId);
			return ResponseEntity.ok("Followed successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to follow user");
		}
	}

	// 언팔로우 유저 (팔로우를 제거하는 API)
	@DeleteMapping("/unfollow")
	public ResponseEntity<String> unfollowUser(@RequestParam String targetId) {
		try {
			// 언팔로우 로직 처리
			userFollowService.unfollowUser(targetId);
			return ResponseEntity.ok("Unfollowed successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to unfollow user");
		}
	}

	// 특정 유저가 팔로우하는 유저들 조회 (예: followerId가 팔로우하는 유저들)
	@GetMapping("/following")
	public ResponseEntity<List<UserSnippetDto>> getFollowing(@RequestParam String uuid) {
		List<UserSnippetDto> followingUsers = userFollowService.getAllFollowing(uuid);
		return ResponseEntity.ok(followingUsers);
	}

	// 특정 유저를 팔로우하는 유저들 조회 (예: followingId를 팔로우한 유저들)
	@GetMapping("/followers")
	public ResponseEntity<List<UserSnippetDto>> getFollowers(@RequestParam String uuid) {
		List<UserSnippetDto> followers = userFollowService.getAllFollowers(uuid);
		return ResponseEntity.ok(followers);
	}

	// // 특정 유저 팔로워 수 조회
	// @GetMapping("/{userId}/followers/count")
	// public ResponseEntity<Long> getFollowersCount(@PathVariable Long userId) {
	// Long followersCount = userService.getFollowersCount(userId);
	// return ResponseEntity.ok(followersCount);
	// }

	// // 특정 유저 팔로잉 수 조회
	// @GetMapping("/{userId}/following/count")
	// public ResponseEntity<Long> getFollowingCount(@PathVariable Long userId) {
	// Long followingCount = userService.getFollowingCount(userId);
	// return ResponseEntity.ok(followingCount);
	// }
}
