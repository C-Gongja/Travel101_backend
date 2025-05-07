package com.sharavel.sharavel_be.follow.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.sharavel.sharavel_be.follow.entity.UserFollow;
import com.sharavel.sharavel_be.follow.repository.UserFollowRepository;
import com.sharavel.sharavel_be.follow.service.UserFollowService;
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
	public List<Users> getAllFollowing(String username) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getAllFollowing'");
	}

	@Override
	public List<Users> getAllFollowers(String username) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getAllFollowers'");
	}
}
