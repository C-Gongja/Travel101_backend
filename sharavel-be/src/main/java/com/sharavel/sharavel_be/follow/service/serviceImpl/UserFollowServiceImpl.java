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
	public ResponseEntity<?> followUser(String userId) {
		Users currentUser = getCurrentUser();
		Users followedUser = userRepository.findByUuid(userId)
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
	public ResponseEntity<?> unfollowUser(String userId) {
		Users currentUser = getCurrentUser();
		Users unfollowedUser = userRepository.findByUuid(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (currentUser.equals(unfollowedUser)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("You cannot unfollow yourself");
		}

		Optional<UserFollow> existingFollow = userFollowRepository.findByFollowerAndFollowing(currentUser, unfollowedUser);

		if (existingFollow.isPresent()) {
			userFollowRepository.delete(existingFollow.get());
			return ResponseEntity.ok("Unfollowed the user");
		} else {
			return ResponseEntity.ok("You are not following this user");
		}
	}

	@Override
	public Long getFollowingCount(Long userId) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getFollowingCount'");
	}

	@Override
	public Long getFollowersCount(Long userId) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getFollowersCount'");
	}

	@Override
	public List<Users> getFollowing(Long userId) {
		throw new UnsupportedOperationException("Unimplemented method 'getFollowers'");
	}

	@Override
	public List<Users> getFollowers(Long userId) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getFollowers'");
	}
}
