package com.sharavel.sharavel_be.follow.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	@Autowired
	private UserFollowMapper userFollowMapper;

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
	public Page<UserSnippetDto> getInfFollowers(String uuid, int page, int limit) {
		Users targetUser = userRepository.findByUuid(uuid).orElseThrow(() -> new RuntimeException("User not found"));
		Users currentUser = null;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()
				&& !"anonymousUser".equals(authentication.getName())) {
			String email = authentication.getName();
			currentUser = userRepository.findByEmail(email).orElse(null);
		}
		final Users finalCurrentUser = currentUser;
		Pageable pageable = PageRequest.of(page, limit);
		// targetUser의 followers 가져오기
		// Page<UserFollow> followers =
		// userFollowRepository.findByFollowingUuid(targetUser.getUuid(), pageable);
		// return followers.map(f -> UserFollowMapper.toFollowerDto(f, finalCurrentUser,
		// userFollowRepository));

		return userFollowRepository.findByFollowingUuid(targetUser.getUuid(), pageable)
				.map(follow -> userFollowMapper.toFollowerDto(follow, finalCurrentUser));
	}

	@Override
	public Page<UserSnippetDto> getInfFollowing(String uuid, int page, int limit) {
		Users targetUser = userRepository.findByUuid(uuid).orElseThrow(() -> new RuntimeException("User not found"));
		Users currentUser = null;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()
				&& !"anonymousUser".equals(authentication.getName())) {
			String email = authentication.getName();
			currentUser = userRepository.findByEmail(email).orElse(null);
		}
		final Users finalCurrentUser = currentUser;

		Pageable pageable = PageRequest.of(page, limit);
		// targetUser의 following 가져오기
		// Page<UserFollow> following = userFollowRepository.findByFollowerUuid(targetUser.getUuid(), pageable);

		return userFollowRepository.findByFollowingUuid(targetUser.getUuid(), pageable)
				.map(follow -> userFollowMapper.toFollowingDto(follow, finalCurrentUser));
	}
}
