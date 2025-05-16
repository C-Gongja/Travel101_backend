package com.sharavel.sharavel_be.follow.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sharavel.sharavel_be.follow.service.UserFollowService;
import com.sharavel.sharavel_be.user.dto.UserSnippetDto;

@RestController
@RequestMapping("/public/follow")
public class PublicUserFollowController {
	@Autowired
	private UserFollowService userFollowService;

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
}
