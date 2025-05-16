package com.sharavel.sharavel_be.follow.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.sharavel.sharavel_be.follow.entity.UserFollow;
import com.sharavel.sharavel_be.follow.mapper.UserFollowMapper;
import com.sharavel.sharavel_be.follow.repository.UserFollowRepository;
import com.sharavel.sharavel_be.follow.service.UserFollowService;
import com.sharavel.sharavel_be.user.dto.UserSnippetDto;
import com.sharavel.sharavel_be.user.entity.Users;
import com.sharavel.sharavel_be.user.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserFollowServiceImpl implements UserFollowService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserFollowRepository userFollowRepository;

	private Users getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("User not authenticated");
		}
		String email = authentication.getName();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalStateException("Current user not found"));
	}

	@Override
	public ResponseEntity<?> followUser(String targetId) {
		Users currentUser = getCurrentUser();
		Users followedUser = userRepository.findByUuid(targetId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (currentUser.equals(followedUser)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("You cannot follow yourself");
		}

		Optional<UserFollow> existingFollow = userFollowRepository.findByFollowerAndFollowing(currentUser, followedUser);

		if (existingFollow.isPresent()) {
			return ResponseEntity.ok("Already following the user");
		}

		UserFollow userFollow = new UserFollow();
		userFollow.setFollower(currentUser);
		userFollow.setFollowing(followedUser);
		userFollow.setCreatedAt(LocalDateTime.now());
		userFollowRepository.save(userFollow);
		return ResponseEntity.ok("Followed the user");
	}

	@Override
	@Transactional
	public ResponseEntity<?> unfollowUser(String targetId) {
		Users currentUser = getCurrentUser();
		Users unfollowedUser = userRepository.findByUuid(targetId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (currentUser.equals(unfollowedUser)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("You cannot unfollow yourself");
		}

		try {
			userFollowRepository.deleteByFollowerAndFollowing(currentUser, unfollowedUser);
			return ResponseEntity.ok("Unfollowed the user");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to unfollow the user: " + e.getMessage());
		}
	}

	@Override
	public Long getFollowingCount(String userId) {
		Users user = userRepository.findByUuid(userId).orElseThrow(() -> new RuntimeException("User not found"));
		return userFollowRepository.countByFollower(user);
	}

	@Override
	public Long getFollowersCount(String userId) {
		Users user = userRepository.findByUuid(userId).orElseThrow(() -> new RuntimeException("User not found"));
		return userFollowRepository.countByFollowing(user);
	}

	@Override
	public List<UserSnippetDto> getAllFollowing(String uuid) {
		Users targetUser = userRepository.findByUuid(uuid).orElseThrow(() -> new RuntimeException("User not found"));
		Users currentUser = null;

		// 현재 로그인한 사용자 찾기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()
				&& !"anonymousUser".equals(authentication.getName())) {
			String email = authentication.getName();
			currentUser = userRepository.findByEmail(email).orElse(null);
		}

		if (currentUser != null) {
			System.out.println("!!!!!!currentUser: " + currentUser.toString());
		}

		final Users finalCurrentUser = currentUser;
		// targetUser의 following 가져오기
		List<UserFollow> following = userFollowRepository.findByFollower(targetUser);

		// 각각의 팔로워가 currentUser로부터 팔로우되고 있는지 확인
		return following.stream()
				.map(f -> UserFollowMapper.toFollowingDto(f, finalCurrentUser, userFollowRepository))
				.collect(Collectors.toList());
	}

	@Override
	public List<UserSnippetDto> getAllFollowers(String uuid) {
		Users targetUser = userRepository.findByUuid(uuid).orElseThrow(() -> new RuntimeException("User not found"));

		// 현재 로그인한 사용자 찾기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Users currentUser = null;

		if (authentication != null && authentication.isAuthenticated()
				&& !"anonymousUser".equals(authentication.getName())) {
			String email = authentication.getName();
			currentUser = userRepository.findByEmail(email).orElse(null);
		}

		if (currentUser != null) {
			System.out.println("!!!!!!currentUser: " + currentUser.toString());
		}

		final Users finalCurrentUser = currentUser;
		// targetUser의 followers 가져오기
		List<UserFollow> followers = userFollowRepository.findByFollowing(targetUser);

		// 각각의 팔로워가 currentUser로부터 팔로우되고 있는지 확인
		return followers.stream()
				.map(f -> UserFollowMapper.toFollowerDto(f, finalCurrentUser, userFollowRepository))
				.collect(Collectors.toList());
	}
}
