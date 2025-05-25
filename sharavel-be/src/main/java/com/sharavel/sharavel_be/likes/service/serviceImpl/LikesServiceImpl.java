package com.sharavel.sharavel_be.likes.service.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sharavel.sharavel_be.comments.repository.CommentRepository;
import com.sharavel.sharavel_be.likes.entity.Likes;
import com.sharavel.sharavel_be.likes.repository.LikesRepository;
import com.sharavel.sharavel_be.likes.service.LikesService;
import com.sharavel.sharavel_be.trip.repository.TripRepository;
import com.sharavel.sharavel_be.user.entity.Users;
import com.sharavel.sharavel_be.user.repository.UserRepository;

@Service
public class LikesServiceImpl implements LikesService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TripRepository tripRepository;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private LikesRepository likesRepository;

	private Users getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("User not authenticated");
		}
		String email = authentication.getName();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalStateException("Current user not found"));
	}

	private void validateTarget(String targetType, String targetUid) {
		switch (targetType) {
			case "COMMENT":
				if (!commentRepository.existsByUid(targetUid)) {
					throw new RuntimeException("Comment not found");
				}
				break;
			case "TRIP":
				if (!tripRepository.existsByTripUid(targetUid)) {
					throw new RuntimeException("Trip not found");
				}
				break;
			default:
				throw new RuntimeException("Invalid target type");
		}
	}

	@Override
	@Transactional
	public String addLike(String targetType, String targetUid) {
		Users user = getCurrentUser();
		validateTarget(targetType, targetUid);

		// 중복 좋아요 체크
		if (likesRepository.existsByTargetTypeAndTargetUidAndUser(targetType, targetUid, user)) {
			throw new RuntimeException("Already liked");
		}

		Likes like = new Likes();
		like.setTargetType(targetType);
		like.setTargetUid(targetUid);
		like.setUser(user);
		like.setCreatedAt(LocalDateTime.now());
		likesRepository.save(like);

		return "successfully add like";
	}

	@Override
	@Transactional
	public String removeLike(String targetType, String targetUid) {
		Users user = getCurrentUser();

		Likes like = likesRepository.findByTargetTypeAndTargetUidAndUser(targetType, targetUid, user)
				.orElseThrow(() -> new RuntimeException("Like not found"));

		likesRepository.delete(like);

		return "successfully remove like";
	}

	@Override
	public Long getLikeCount(String targetType, String targetUid) {
		return likesRepository.countByTargetTypeAndTargetUid(targetType, targetUid);
	}

}
