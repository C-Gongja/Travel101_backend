package com.sharavel.sharavel_be.follow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

	// 특정 유저 팔로워 수 조회
	@GetMapping("/inffollowers")
	public ResponseEntity<Page<UserSnippetDto>> getInfFollowers(
			@RequestParam String uuid,
			@RequestParam int page,
			@RequestParam int limit) {
		Page<UserSnippetDto> followers = userFollowService.getInfFollowers(uuid, page, limit);
		return ResponseEntity.ok(followers);
	}

	// 특정 유저 팔로워 수 조회
	@GetMapping("/inffollowing")
	public ResponseEntity<Page<UserSnippetDto>> getInfFollowing(
			@RequestParam String uuid,
			@RequestParam int page,
			@RequestParam int limit) {
		Page<UserSnippetDto> followers = userFollowService.getInfFollowing(uuid, page, limit);
		return ResponseEntity.ok(followers);
	}
}
